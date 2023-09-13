package com.github.wjlong1128.fileupload.domain.result;

/**
 * @author wjlong1128
 * @version 1.0
 * @date 2023/8/21
 * @desc
 */
public interface ResultState {

    int SUCCESS_CODE  = 200;
    String SUCCESS_MESSAGE = "success";
    int SYSTEM_ERR_CODE = 500;
    String SYSTEM_ERR_MESSAGE = "系统异常";

    int getCode();
    String getMessage();

}
