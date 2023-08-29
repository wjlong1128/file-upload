package com.github.wjlong1128.fileupload.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * @author wjlong1128
 * @version 1.0
 * @date 2023/8/26
 * @desc
 */
public interface BigFileUploadService {

    boolean isExistsFile(String md5);

    boolean isExistsChunk(String md5, Integer chunkNo);

    boolean uploadChunk(String md5, Integer chunkNo, MultipartFile file);

    void mergeChunk(String fileName, String md5, Integer chunkNum);

}
