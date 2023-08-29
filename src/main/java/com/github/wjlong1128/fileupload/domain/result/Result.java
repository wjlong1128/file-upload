package com.github.wjlong1128.fileupload.domain.result;

import lombok.Data;

/**
 * @author wjlong1128
 * @version 1.0
 * @date 2023/8/21
 * @desc
 */
@Data
public class Result<T> {

    private int code;
    private String message;
    private T data;

    private Result() {
        this.code = ResultMessage.SUCCESS_CODE;
        this.message = ResultMessage.SUCCESS_MESSAGE;
    }

    public Result(T data) {
        this();
        this.data = data;
    }

    public Result(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public Result(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }


    public static <T> Result<T> success(T data) {
        return new Result<T>(data);
    }

    public static <T> Result<T> success() {
        return new Result<T>();
    }

    public static <T> Result<T> success(String message, T data) {
        return new Result<T>(ResultMessage.SUCCESS_CODE, message, data);
    }
    public static <T> Result<T> message(String message) {
        return new Result<T>(ResultMessage.SUCCESS_CODE, message);
    }
    public static <T> Result<T> fail() {
        return new Result<>(ResultMessage.SYSTEM_ERR_CODE, ResultMessage.SYSTEM_ERR_MESSAGE);
    }

    public static <T> Result<T> fail(int code, String message) {
        return new Result<>(code, message);
    }


    public static <T> Result<T> fail(String message) {
        return new Result<>(ResultMessage.SYSTEM_ERR_CODE, message);
    }

    public static <T> Result<T> fail(ResultMessage resultMessage) {
        return new Result<>(resultMessage.getCode(), resultMessage.getMessage());
    }


}
