package com.github.wjlong1128.fileupload.server;

import com.github.wjlong1128.fileupload.domain.bo.ChunkFileBO;
import com.github.wjlong1128.fileupload.domain.exception.FileServerException;

import java.util.List;

/**
 * @author wjlong1128
 * @version 1.0
 * @date 2023/8/26
 * @desc
 */
public interface MultipartUploadFileServer extends FileServer {

    void mergeServerFile(String objectName, String objectBucket, List<ChunkFileBO> chunkFileBOS) throws FileServerException;

}
