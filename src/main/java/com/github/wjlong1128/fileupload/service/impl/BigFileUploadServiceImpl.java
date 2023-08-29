package com.github.wjlong1128.fileupload.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.github.wjlong1128.fileupload.domain.bo.ChunkFileBO;
import com.github.wjlong1128.fileupload.domain.dto.ChunkCheckDTO;
import com.github.wjlong1128.fileupload.domain.entity.FileRecord;
import com.github.wjlong1128.fileupload.domain.exception.BusinessException;
import com.github.wjlong1128.fileupload.domain.exception.FileServerException;
import com.github.wjlong1128.fileupload.domain.result.UploadMessage;
import com.github.wjlong1128.fileupload.domain.vo.FileVO;
import com.github.wjlong1128.fileupload.server.ShardUploadFileServer;
import com.github.wjlong1128.fileupload.service.BigFileUploadService;
import com.github.wjlong1128.fileupload.service.FileRecordService;
import com.github.wjlong1128.fileupload.task.delay.ChunkCheckDelay;
import com.github.wjlong1128.fileupload.task.delay.ChunkCheckDelayTaskExcutor;
import com.github.wjlong1128.fileupload.utils.FileUtils;
import com.github.wjlong1128.fileupload.utils.MimeTypeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
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
    public static final String CACHE_PREFIX = "big-file:";

    @Resource
    private ShardUploadFileServer fileServer;

    @Resource
    private FileRecordService fileRecordService;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private ExecutorService executorService;

    @Resource
    private ChunkCheckDelayTaskExcutor chunkCheckDelayTaskExcutor;

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

        String type = MediaType.APPLICATION_OCTET_STREAM_VALUE;
        String bucket = null;
        try {
            bucket = this.fileServer.getBucket(type);
        } catch (FileServerException e) {
            throw new BusinessException(UploadMessage.Big.UNABLE_GET_BUCKET, e);
        }
        String filePath = generateChunkFilePath(hex, chunkNo);
        try {
            this.fileServer.uploadObject(bucket, filePath, type, file.getBytes());
        } catch (FileServerException e) {
            throw new BusinessException(UploadMessage.Big.UNABLE_UPLOAD_FILE, e);
        } catch (IOException e) {
            throw new BusinessException(UploadMessage.Big.UNABLE_READ_FILE, e);
        }
        String key = CACHE_PREFIX + hex;
        this.stringRedisTemplate.opsForSet().add(key, chunkNo.toString());
        // 提交延迟任务，如果过期就删除
        this.chunkCheckDelayTaskExcutor.add(new ChunkCheckDelay(new ChunkCheckDTO(key, chunkNo, bucket, filePath), 1, TimeUnit.DAYS));
        return true;

    }


    @Override
    public FileVO mergeChunk(String originalName, String md5, Integer chunkNum) {
        String key = CACHE_PREFIX + md5;
        Long size = this.stringRedisTemplate.opsForSet().size(key);
        if (size == null || size.intValue() != chunkNum) {
            throw new BusinessException(UploadMessage.Big.CHUNK_NOT_ALL);
        }
        String chunkBucket = null;
        String mergeBucket = null;
        try {
            chunkBucket = this.fileServer.getBucket(MediaType.APPLICATION_OCTET_STREAM_VALUE);
            mergeBucket = this.fileServer.getBucket(originalName);
        } catch (FileServerException e) {
            throw new BusinessException(UploadMessage.Big.UNABLE_GET_BUCKET, e);
        }
        String finalChunkBucket = chunkBucket;
        List<ChunkFileBO> chunkFileBOS = Stream.iterate(0, i -> ++i).limit(chunkNum)
                .map(i -> new ChunkFileBO(finalChunkBucket, generateChunkFilePath(md5, i)))
                .collect(Collectors.toList());
        // 获取后缀 .jpg
        String suffix = FileUtils.getSuffix(originalName, true);
        // 路径
        String filePathName = generateMergeFilePath(md5, suffix);
        // 合并
        try {
            this.fileServer.mergeServerFile(filePathName, mergeBucket, chunkFileBOS);
        } catch (FileServerException e) {
            throw new BusinessException(UploadMessage.Big.CHUNK_MERGE_FAIL, e);
        }
        // 入库
        FileRecord record = new FileRecord();
        record.setBucket(mergeBucket);
        record.setId(md5);
        record.setFileName(md5 + suffix);
        record.setUrl(this.fileServer.getAddress() + "/" + mergeBucket + "/" + filePathName);
        record.setPath(filePathName);
        record.setOriginalName(originalName);
        record.setContentType(MimeTypeUtils.getMimeWithSuffix(originalName));
        byte[] bytes = null;
        try {
            bytes = this.fileServer.getObject(mergeBucket, filePathName);
        } catch (FileServerException e) {
            throw new BusinessException(UploadMessage.Big.UNABLE_CHECK_FILE, e);
        }
        record.setMimeType(MimeTypeUtils.getMimeWithMagic(bytes));
        record.setSize((bytes.length / 1024 / 1024) + "MB");
        bytes = null;
        System.gc();
        this.fileRecordService.save(record);
        log.debug("文件合并入库:{}", md5);
        // 异步删除残留
        this.executorService.submit(() -> {
            this.stringRedisTemplate.delete(key);
            try {
                this.fileServer.batchDelete(finalChunkBucket, chunkFileBOS.stream().map(ChunkFileBO::getChunkFilePath).collect(Collectors.toList()));
            } catch (FileServerException e) {
                log.error("删除分块文件失败", e);
            }
            log.debug("删除{}残留分片成功", md5);
        });
        return BeanUtil.copyProperties(record, FileVO.class);
    }


    /**
     * 根据md5和分块序号获取分块文件的路径
     */
    private String generateChunkFilePath(String md5, Integer chunkNo) {
        return md5.charAt(0) + "/" + md5.charAt(1) + "/" + md5.charAt(2) + "/" + "chunk" + "/" + chunkNo;
    }

    /**
     * 根据md5和文件后缀生成文件路径 /a/b/c/{md5} + .jpg
     */
    private String generateMergeFilePath(String md5, String suffix) {
        return md5.charAt(0) + "/" + md5.charAt(1) + "/" + md5.charAt(2) + "/" + md5 + suffix;
    }
}
