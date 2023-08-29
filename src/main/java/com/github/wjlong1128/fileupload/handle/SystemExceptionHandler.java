package com.github.wjlong1128.fileupload.handle;

import com.github.wjlong1128.fileupload.domain.result.RestResp;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author wjlong1128
 * @version 1.0
 * @date 2023/8/21
 * @desc
 */
@Slf4j
@RestControllerAdvice
public class SystemExceptionHandler {

    @ExceptionHandler(Exception.class)
    public RestResp handler(Exception e) {
        log.error("系统异常, 异常类型: {}, 异常消息: {}", e.getClass().getSimpleName(), e.getMessage(), e);
        return RestResp.fail();
    }

}
