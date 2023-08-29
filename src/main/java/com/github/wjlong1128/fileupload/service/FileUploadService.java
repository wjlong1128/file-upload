package com.github.wjlong1128.fileupload.service;

import com.github.wjlong1128.fileupload.domain.bo.DownloadBO;
import com.github.wjlong1128.fileupload.domain.exception.FileServerException;
import com.github.wjlong1128.fileupload.domain.vo.FileVO;

/**
 * @author wjlong1128
 * @version 1.0
 * @date 2023/8/20
 * @desc
 */
public interface FileUploadService {

    FileVO upload(String originalName, String contentType, byte[] bytes);

    DownloadBO downLoad(String fileName);

    DownloadBO downLoad(String bucket,String filePath) throws FileServerException;

    boolean deleteFile(String id);

}
