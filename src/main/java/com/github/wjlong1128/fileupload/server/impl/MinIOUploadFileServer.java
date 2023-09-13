package com.github.wjlong1128.fileupload.server.impl;

import com.github.wjlong1128.fileupload.config.minio.properties.MinioProperties;
import com.github.wjlong1128.fileupload.domain.bo.ChunkFileBO;
import com.github.wjlong1128.fileupload.domain.bo.FileBO;
import com.github.wjlong1128.fileupload.domain.exception.FileServerException;
import com.github.wjlong1128.fileupload.server.AbstractFileServer;
import com.github.wjlong1128.fileupload.server.MultipartUploadFileServer;
import io.minio.*;
import io.minio.errors.ErrorResponseException;
import io.minio.messages.DeleteError;
import io.minio.messages.DeleteObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.IOUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author wjlong1128
 * @version 1.0
 * @date 2023/8/19
 * @desc 封装了上传、删除、获取等常见操作
 */
@Slf4j
@Component
public class MinIOUploadFileServer extends AbstractFileServer implements MultipartUploadFileServer {

    public static final String SERVER_TYPE = "MINIO";

    @Resource
    private MinioClient minioClient;

    @Resource
    private MinioProperties minioProperties;


    @Override
    public FileBO uploadObject(String bucket, String filePath, String mimeType, byte[] bytes) throws FileServerException {
        int size = bytes.length;
        try (ByteArrayInputStream stream = new ByteArrayInputStream(bytes)) {
            this.minioClient.putObject(PutObjectArgs.builder()
                    .object(filePath)
                    .contentType(mimeType)
                    .bucket(bucket)
                    .stream(stream, size, -1)
                    .build());
            FileBO bo = new FileBO();
            bo.setPath(filePath);
            bo.setContentType(mimeType);
            bo.setUrl(this.minioProperties.getEndpointUrl() + "/" + bucket + "/" + filePath);
            bo.setBucket(bucket);
            bo.setSize(size);
            bo.setServerType(SERVER_TYPE);
            return bo;
        } catch (IOException e) {
            throw new FileServerException("读取文件字节失败", e);
        } catch (Exception e) {
            throw new FileServerException("上传文件失败", e);
        }
    }

    @Override
    public FileBO uploadObject(String bucket, String filePath, String mimeType, File file) throws FileServerException {
        if (ObjectUtils.isEmpty(file)) {
            throw new FileServerException("文件不能为空");
        }
        try (FileInputStream stream = new FileInputStream(file)) {
            return this.uploadObject(bucket, filePath, mimeType, IOUtils.toByteArray(stream));
        } catch (IOException e) {
            throw new FileServerException("创建文件流失败", e);
        }
    }

    @Override
    public boolean deleteObject(String bucket, String filePath) throws FileServerException {
        if (!StringUtils.hasText(bucket) || !StringUtils.hasText(filePath)) {
            throw new FileServerException("参数不合法 bucket:" + bucket + ", filePath:" + filePath);
        }
        try {
            this.minioClient.removeObject(RemoveObjectArgs
                    .builder()
                    .bucket(bucket)
                    .object(filePath)
                    .build());
            return true;
        } catch (Exception e) {
            throw new FileServerException("删除文件失败 bucket: " + bucket + ", filePath: " + filePath, e);
        }
    }

    @Override
    public byte[] getObject(String bucket, String filePath) throws FileServerException {
        if (!StringUtils.hasText(bucket) || !StringUtils.hasText(filePath)) {
            throw new FileServerException("参数不合法 bucket:" + bucket + ", filePath:" + filePath);
        }
        try {
            try (GetObjectResponse response = this.minioClient.getObject(GetObjectArgs.builder()
                    .bucket(bucket)
                    .object(filePath)
                    .build())) {
                return IOUtils.toByteArray(response);
            }
        } catch (ErrorResponseException e) {
            if (e.response().code() == 404) {
                throw new FileServerException("该文件不存在", e);
            } else {
                throw new FileServerException("获取文件失败", e);
            }
        } catch (Exception e) {
            throw new FileServerException("获取文件失败", e);
        }
    }

    public void mergeServerFile(String objectName, String objectBucket, List<ChunkFileBO> chunkFileBOS) throws FileServerException {
        List<ComposeSource> sources = chunkFileBOS.stream()
                .map(bo -> ComposeSource.builder().bucket(bo.getBucket()).object(bo.getChunkFilePath()).build())
                .collect(Collectors.toList());
        try {
            this.minioClient.composeObject(ComposeObjectArgs.builder()
                    .bucket(objectBucket)
                    .sources(sources)
                    .object(objectName)
                    .build());
        } catch (Exception e) {
            throw new FileServerException("合并分块文件失败", e);
        }
    }

    @Override
    public void batchDelete(String bucketName, List<String> fileNames) throws FileServerException {
        try {
            List<DeleteObject> objects = fileNames.stream()
                    .map(DeleteObject::new)
                    .collect(Collectors.toList());
            Iterable<Result<DeleteError>> results = this.minioClient
                    .removeObjects(RemoveObjectsArgs.builder()
                            .bucket(bucketName)
                            .objects(objects)
                            .build());
            for (Result<DeleteError> result : results) {
                log.debug("remove file: " + result.get());
            }
        } catch (Exception e) {
            throw new FileServerException("批量删除文件异常", e);
        }
    }


    @Override
    public boolean isExists(String bucket, String filePath) throws FileServerException {
        this.getObject(bucket, filePath);
        return false;
    }

    @Override
    public String getServerType() {
        return SERVER_TYPE;
    }

    @Override
    public String getAddress() {
        return this.minioProperties.getEndpointUrl();
    }


    public void createBucket(String bucket) throws FileServerException {
        try {
            this.minioClient.makeBucket(MakeBucketArgs.builder()
                    .bucket(bucket)
                    .build());
        } catch (Exception e) {
            throw new FileServerException("创建bucket:" + bucket + " 失败");
        }
    }


    @Override
    public void ifNotExistsCreateBucket(String bucketName) throws FileServerException {
        try {
            boolean bucketExists = this.minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
            if (!bucketExists) {
                this.createBucket(bucketName);
            }
        } catch (Exception e) {
            throw new FileServerException("创建bucket"+bucketName+"失败", e);
        }

    }


}
