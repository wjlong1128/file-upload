package com.github.wjlong1128.fileupload.handle;

import com.github.wjlong1128.fileupload.domain.exception.BusinessException;
import com.github.wjlong1128.fileupload.domain.result.Result;
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

    @ExceptionHandler(BusinessException.class)
    public Result handler(BusinessException e) {
        log.error("系统异常, 异常类型: {}, 异常消息: {}", e.getClass().getSimpleName(), e.getMessage(), e);
        return Result.fail(e);
    }

    @ExceptionHandler(Exception.class)
    public Result handler(Exception e) {
        log.error("系统异常, 异常类型: {}, 异常消息: {}", e.getClass().getSimpleName(), e.getMessage(), e);
        return Result.fail();
    }

}
