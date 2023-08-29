package com.github.wjlong1128.fileupload.server;

import com.github.wjlong1128.fileupload.domain.bo.FileBO;
import com.github.wjlong1128.fileupload.domain.exception.FileServerException;

import java.io.File;
import java.util.List;

/**
 * @author wjlong1128
 * @version 1.0
 * @date 2023/8/19
 * @desc
 */
public interface FileServer {

    /**
     * 上传文件
     */
    FileBO uploadObject(String bucket, String filePath, String mimeType, byte[] bytes) throws FileServerException;

    /**
     * 上传文件
     */
    FileBO uploadObject(String bucket, String filePath, String mimeType, File file) throws FileServerException;


    /**
     * 删除文件
     */
    boolean deleteObject(String bucket, String filePath) throws FileServerException;

    /**
     * 获取文件
     */
    byte[] getObject(String bucket, String filePath) throws FileServerException;


    /**
     * 判断文件是否存在
     */
    boolean isExists(String bucket, String filePath) throws FileServerException;


    /**
     * 当前文件服务器类型
     */
    String getServerType();

    /**
     * 通过key获取对应的bucket
     *
     * @param key 根据语义，这里可能是根据context-type或者file.suffix
     * @return
     * @throws FileServerException
     */

    String getBucket(String key) throws FileServerException;

    String getAddress();

    void batchDelete(String bucketName, List<String> fileNames) throws FileServerException;
}
