package com.github.wjlong1128.fileupload.domain.result;

import lombok.Data;

/**
 * @author wjlong1128
 * @version 1.0
 * @date 2023/8/21
 * @desc
 */
@Data
public class RestResp {

    private int code;
    private String message;
    private Object data;

    private RestResp() {
        this.code = 200;
        this.message = "success";
    }

    public RestResp(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public RestResp data(Object data) {
        this.data = data;
        return this;
    }

    public RestResp message(String message) {
        this.message = message;
        return this;
    }


    public static RestResp success() {
        return new RestResp();
    }

    public static RestResp fail() {
        return new RestResp(ErrorCode.SYSTEM_ERR_CODE, ErrorCode.SYSTEM_ERR_MESSAGE);
    }

    public static RestResp fail(int code, String message) {
        return new RestResp(code, message);
    }

    public static RestResp fail(String message) {
        return new RestResp(ErrorCode.SYSTEM_ERR_CODE, message);
    }

    public static RestResp fail(ErrorCode errorCode) {
        return new RestResp(errorCode.errCode(), errorCode.errMessage());
    }

    public static RestResp success(String message) {
        return new RestResp(200, message);
    }

}
