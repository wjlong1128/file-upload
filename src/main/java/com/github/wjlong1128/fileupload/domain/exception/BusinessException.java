package com.github.wjlong1128.fileupload.domain.exception;

import com.github.wjlong1128.fileupload.domain.result.ResultState;

/**
 * @author wjlong1128
 * @version 1.0
 * @date 2023/8/21
 * @desc 通用业务异常类
 */

public class BusinessException extends RuntimeException implements ResultState {

    private final int code;

    public BusinessException(String message) {
        super(message);
        this.code = ResultState.SYSTEM_ERR_CODE;
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
        this.code = ResultState.SYSTEM_ERR_CODE;
    }

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }

    public BusinessException(int code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public BusinessException(ResultState resultState) {
        super(resultState.getMessage());
        this.code = resultState.getCode();
    }

    public BusinessException(ResultState resultState, Throwable cause) {
        super(resultState.getMessage(), cause);
        this.code = resultState.getCode();
    }

    @Override
    public int getCode() {
        return this.code;
    }
}
