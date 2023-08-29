package com.github.wjlong1128.fileupload.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.github.wjlong1128.fileupload.domain.bo.DownloadBO;
import com.github.wjlong1128.fileupload.domain.bo.FileBO;
import com.github.wjlong1128.fileupload.domain.entity.FileRecord;
import com.github.wjlong1128.fileupload.domain.exception.BusinessException;
import com.github.wjlong1128.fileupload.domain.exception.FileServerException;
import com.github.wjlong1128.fileupload.domain.result.UploadMessage;
import com.github.wjlong1128.fileupload.domain.vo.FileVO;
import com.github.wjlong1128.fileupload.server.FileServer;
import com.github.wjlong1128.fileupload.service.FileRecordService;
import com.github.wjlong1128.fileupload.service.FileUploadService;
import com.github.wjlong1128.fileupload.utils.MimeTypeUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import java.util.UUID;

/**
 * @author wjlong1128
 * @version 1.0
 * @date 2023/8/20
 * @desc
 */
@Service
public class FileUploadServiceImpl implements FileUploadService {

    @Resource
    private FileServer fileServer;

    @Resource
    private FileRecordService fileRecordService;


    @Override
    public FileVO upload(String originalName, String contentType, byte[] bytes) {

        String md5 = DigestUtils.md5DigestAsHex(bytes);
        String mimeType = MimeTypeUtils.getMimeWithMagic(bytes);
        String suffix = originalName.substring(originalName.lastIndexOf('.'));
        String fileName = getFilePathWithMD5(md5, suffix);
        String bucket = null;
        try {
            bucket = fileServer.getBucket(originalName);
        } catch (FileServerException e) {
            throw new BusinessException(UploadMessage.Normal.UNABLE_GET_BUCKET, e);
        }
        FileBO bo = null;
        try {
            bo = fileServer.uploadObject(bucket, fileName, mimeType, bytes);
        } catch (FileServerException e) {
            throw new BusinessException(UploadMessage.Normal.UNABLE_UPLOAD_FILE, e);
        }
        // 入库
        FileRecord record = new FileRecord();
        record.setId(md5);
        record.setFileName(md5 + suffix);
        record.setMimeType(mimeType);
        record.setContentType(contentType);
        record.setSize((bo.getSize() / 1024) + "KB");
        record.setBucket(bo.getBucket());
        record.setPath(bo.getPath());
        record.setUrl(bo.getUrl());
        record.setOriginalName(originalName);
        record.setStorageType(bo.getServerType());
        this.fileRecordService.saveOrUpdate(record);
        return BeanUtil.copyProperties(record, FileVO.class);
    }

    @NotNull
    private static String getFilePathWithMD5(String hex, String suffix) {
        String sp = "/";
        String fileName = hex.charAt(0) + sp + hex.charAt(1) + sp + hex.charAt(2) + sp + hex + suffix;
        return fileName;
    }

    @Override
    public DownloadBO downLoad(String fileName) {
        try {
            FileRecord record = this.fileRecordService.lambdaQuery()
                    .select(
                            FileRecord::getContentType,
                            FileRecord::getPath,
                            FileRecord::getBucket,
                            FileRecord::getFileName
                    )
                    .eq(FileRecord::getFileName, fileName)
                    .one();
            if (record == null) {
                throw new BusinessException(UploadMessage.Normal.FILE_NOT_EXISTS);
            }
            byte[] bytes = fileServer.getObject(record.getBucket(), record.getPath());
            return DownloadBO.builder()
                    .size(bytes.length)
                    .content(bytes)
                    .fileName(this.uuidFileName(record.getFileName()))
                    .contextType(record.getContentType())
                    .build();
        } catch (FileServerException e) {
            throw new BusinessException(UploadMessage.Normal.UNABLE_GET_FILE, e);
        }
    }

    @Override
    public DownloadBO downLoad(String bucket, String filePath) throws FileServerException {
        FileRecord record = this.fileRecordService.lambdaQuery()
                .select(
                        FileRecord::getContentType,
                        FileRecord::getPath,
                        FileRecord::getBucket,
                        FileRecord::getFileName
                )
                .eq(FileRecord::getPath, filePath)
                .eq(FileRecord::getBucket, bucket)
                .one();
        byte[] bytes = fileServer.getObject(bucket, filePath);
        return DownloadBO.builder()
                .size(bytes.length)
                .content(bytes)
                .fileName(this.uuidFileName(record.getFileName()))
                .contextType(record.getContentType())
                .build();
    }

    @Override
    public boolean deleteFile(String id) {
        FileRecord record = this.fileRecordService.getById(id);
        if (record == null) {
            return false;
        }
        try {
            this.fileServer.deleteObject(record.getBucket(), record.getPath());
        } catch (FileServerException e) {
            throw new BusinessException(UploadMessage.Normal.FILE_DELETE_FAIL, e);
        }
        this.fileRecordService.removeById(record);
        return true;
    }

    private String uuidFileName(String fileName) {
        String suffix = fileName.substring(fileName.lastIndexOf('.'));
        return UUID.randomUUID().toString().replace('-', '_') + suffix;
    }


}
