package com.github.wjlong1128.fileupload.service.impl;

import com.github.wjlong1128.fileupload.domain.bo.ChunkFileBO;
import com.github.wjlong1128.fileupload.domain.entity.FileRecord;
import com.github.wjlong1128.fileupload.domain.exception.FileServerException;
import com.github.wjlong1128.fileupload.server.ShardUploadFileServer;
import com.github.wjlong1128.fileupload.service.BigFileUploadService;
import com.github.wjlong1128.fileupload.service.FileRecordService;
import com.github.wjlong1128.fileupload.utils.FileUtils;
import com.github.wjlong1128.fileupload.utils.MimeTypeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author wjlong1128
 * @version 1.0
 * @date 2023/8/26
 * @desc
 */
@Slf4j
@Service
public class BigFileUploadServiceImpl implements BigFileUploadService {

    /**
     * bigfile:hex: chunkNo
     */
    public static final String CACHE_PREFIX = "bigfile:";

    @Resource
    private ShardUploadFileServer fileServer;

    @Resource
    private FileRecordService fileRecordService;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private ExecutorService executorService;

    @Override
    public boolean isExistsFile(String hex) {
        FileRecord record = this.fileRecordService.lambdaQuery()
                .eq(FileRecord::getId, hex)
                .one();
        if (record != null) {
            return true;
        }
        // TODO 如果库中不存在，但是服务器中存在，那么就可以提交一个异步任务，进行入库
        return false;
    }

    @Override
    public boolean isExistsChunk(String hex, Integer chunkNo) {
        String key = CACHE_PREFIX + hex;
        Boolean member = this.stringRedisTemplate.opsForSet().isMember(key, chunkNo.toString());
        return Boolean.TRUE.equals(member);
    }

    @Override
    public boolean uploadChunk(String hex, Integer chunkNo, MultipartFile file) {
        try {
            String type = MediaType.APPLICATION_OCTET_STREAM_VALUE;
            String bucket = this.fileServer.getBucket(type);
            this.fileServer.uploadObject(bucket, generatChunkFilePath(hex, chunkNo), type, file.getBytes());
            String key = CACHE_PREFIX + hex;
            this.stringRedisTemplate.opsForSet().add(key, chunkNo.toString());
            return true;
        } catch (Exception e) {
            throw new RuntimeException("分块文件上传失败", e);
        }
    }

    @Override
    public void mergeChunk(String originalName, String md5, Integer chunkNum) {
        String key = CACHE_PREFIX + md5;
        Long size = this.stringRedisTemplate.opsForSet().size(key);
        if (size == null || size.intValue() != chunkNum) {
            return;
        }
        String chunkBucket = null;
        String mergeBucket = null;
        try {
            chunkBucket = this.fileServer.getBucket(MediaType.APPLICATION_OCTET_STREAM_VALUE);
            mergeBucket = this.fileServer.getBucket(originalName);
        } catch (FileServerException e) {
            throw new RuntimeException(e);
        }
        try {
            String finalChunkBucket = chunkBucket;
            List<ChunkFileBO> chunkFileBOS = Stream.iterate(0, i -> ++i).limit(chunkNum)
                    .map(i -> new ChunkFileBO(finalChunkBucket, generatChunkFilePath(md5, i)))
                    .collect(Collectors.toList());
            // 获取后缀 .jpg
            String suffix = FileUtils.getSuffix(originalName,true);
            // 路径
            String filePathName = generatMergeFilePath(md5, suffix);
            // 合并
            this.fileServer.mergeServerFile(filePathName, mergeBucket, chunkFileBOS);
            // 异步入库
            String finalMergeBucket = mergeBucket;
            this.executorService.submit(()->{
                FileRecord record = new FileRecord();
                record.setBucket(finalMergeBucket);
                record.setId(md5);
                record.setFileName(md5 + suffix);
                record.setUrl(this.fileServer.getAddress() + "/" + finalMergeBucket + "/" + filePathName);
                record.setPath(filePathName);
                record.setOriginalName(originalName);
                record.setContentType(MimeTypeUtils.getMimeWithSuffix(originalName));
                record.setMimeType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
                this.fileRecordService.save(record);
                log.debug("文件合并入库:{}", md5);
            });
            // 异步删除残留
            this.executorService.submit(() -> {
                this.stringRedisTemplate.delete(key);
                try {
                    this.fileServer.batchDelete(finalChunkBucket, chunkFileBOS.stream().map(ChunkFileBO::getChunkFilePath).collect(Collectors.toList()));
                } catch (FileServerException e) {
                    log.error("删除分块文件失败", e);
                }
                log.debug("删除{}残留成功",md5);
            });
        } catch (FileServerException e) {
            throw new RuntimeException("文件合并失败",e);
        }
    }

    private String generatChunkFilePath(String md5, Integer chunkNo) {
        return md5.charAt(0) + "/" + md5.charAt(1) + "/" + md5.charAt(2) + "/" + "chunk" + "/" + chunkNo;
    }

    private String generatMergeFilePath(String md5, String suffix) {
        return md5.charAt(0) + "/" + md5.charAt(1) + "/" + md5.charAt(2) + "/" + md5 + suffix;
    }
}
