package com.example.mall.common.model.exception;

import com.example.mall.common.model.result.Result;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;

@Slf4j
@RestControllerAdvice(basePackages = {"com.example.mall.*.controller", "com.example.mall.*.service"})
public class GlobalExceptionHandler {
    @ExceptionHandler(FeignException.FeignClientException.class)
    public Result handleLockStockException(Throwable e) {
        log.error("锁定库存异常->{},异常原因->{}", e.getMessage(), e.getCause());
        return Result.fail(e, ErrMsg.LOCK_STOCK_EXCEPTION.getCode(), ErrMsg.LOCK_STOCK_EXCEPTION.getMsg());
    }
    @ExceptionHandler(FeignException.FeignClientException.class)
    public Result handleFeignClientException(Throwable e) {
        log.error("远程服务调用异常->{},异常原因->{}", e.getMessage(), e.getCause());
        return Result.fail(e, ErrMsg.FEIGN_EXCEPTION.getCode(), ErrMsg.FEIGN_EXCEPTION.getMsg());
    }

    @ExceptionHandler(ThreadPoolException.class)
    public Result handleThreadPoolException(Throwable e) {
        log.error("线程池线程执行异常->{},异常原因->{}", e.getMessage(), e.getCause());
        return Result.fail(e, ErrMsg.THREAD_EXECUTION_EXCEPTION.getCode(), ErrMsg.THREAD_EXECUTION_EXCEPTION.getMsg());
    }

    @ExceptionHandler(ESException.class)
    public Result handleESException(Throwable e) {
        log.error("ES服务器异常->{},异常原因->{}", e.getMessage(), e.getCause());
        return Result.fail(e, ErrMsg.ES_SERVER_EXCEPTION.getCode(), ErrMsg.ES_SERVER_EXCEPTION.getMsg());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public Result handleIllegalArgumentException(Throwable e) {
        log.error("用户提供参数异常->{},异常原因->{}", e.getMessage(), e.getCause());
        return Result.fail(e, ErrMsg.VALID_EXCEPTION.getCode(), ErrMsg.VALID_EXCEPTION.getMsg());
    }

    @ExceptionHandler(MinIOException.class)
    public Result handleMinIOException(Throwable e) {
        log.error("文件服务器异常->{},异常原因->{}", e.getMessage(), e.getCause());
        return Result.fail(e, ErrMsg.FILE_SERVER_EXCEPTION.getCode(), ErrMsg.FILE_SERVER_EXCEPTION.getMsg());
    }

    @ExceptionHandler(IOException.class)
    public Result handleIOException(Throwable e) {
        log.error("读写文件异常->{},异常原因->{}", e.getMessage(), e.getCause());
        return Result.fail(e, ErrMsg.IO_EXCEPTION.getCode(), ErrMsg.IO_EXCEPTION.getMsg());
    }

    @ExceptionHandler(Exception.class)
    public Result handleException(Throwable e) {
        log.error("未知异常->{},异常原因->{}", e.getMessage(), e.getCause());
        return Result.fail(e, ErrMsg.UNKNOWN_EXCEPTION.getCode(), ErrMsg.UNKNOWN_EXCEPTION.getMsg());
    }
}
