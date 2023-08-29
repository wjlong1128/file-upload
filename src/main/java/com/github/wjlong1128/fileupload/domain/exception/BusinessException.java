package com.github.wjlong1128.fileupload.domain.exception;

/**
 * @author wjlong1128
 * @version 1.0
 * @date 2023/8/21
 * @desc
 */
public class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }
}
