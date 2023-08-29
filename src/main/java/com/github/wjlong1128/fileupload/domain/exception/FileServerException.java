package com.github.wjlong1128.fileupload.domain.exception;

/**
 * @author wjlong1128
 * @version 1.0
 * @date 2023/8/20
 * @desc 文件服务器异常
 */
public class FileServerException extends Exception {

    public FileServerException(String message) {
        super(message);
    }

    public FileServerException(String message, Throwable throwable) {
        super(message, throwable);
    }

}
