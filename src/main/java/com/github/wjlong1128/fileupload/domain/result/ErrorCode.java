package com.github.wjlong1128.fileupload.domain.result;

/**
 * @author wjlong1128
 * @version 1.0
 * @date 2023/8/21
 * @desc
 */
public interface ErrorCode {
    String SYSTEM_ERR_MESSAGE = "系统异常";
    int SYSTEM_ERR_CODE = 500;
    int errCode();
    String errMessage();
}
