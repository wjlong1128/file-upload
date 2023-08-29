package com.github.wjlong1128.fileupload.domain.exception;

import com.github.wjlong1128.fileupload.domain.result.ResultMessage;

/**
 * @author wjlong1128
 * @version 1.0
 * @date 2023/8/21
 * @desc 通用业务异常类
 */

public class BusinessException extends RuntimeException implements ResultMessage {

    private final int code;

    public BusinessException(String message) {
        super(message);
        this.code = ResultMessage.SYSTEM_ERR_CODE;
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
        this.code = ResultMessage.SYSTEM_ERR_CODE;
    }

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }

    public BusinessException(int code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public BusinessException(ResultMessage resultMessage) {
        super(resultMessage.getMessage());
        this.code = resultMessage.getCode();
    }

    public BusinessException(ResultMessage resultMessage, Throwable cause) {
        super(resultMessage.getMessage(), cause);
        this.code = resultMessage.getCode();
    }

    @Override
    public int getCode() {
        return this.code;
    }
}
