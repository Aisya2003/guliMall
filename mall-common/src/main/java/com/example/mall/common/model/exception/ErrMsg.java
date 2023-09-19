package com.example.mall.common.model.exception;

import lombok.Data;


public enum ErrMsg {
    UNKNOWN_EXCEPTION(10000, "系统未知错误"),
    VALID_EXCEPTION(10001, "参数格式错误"),
    LOCK_STOCK_EXCEPTION(10002, "锁定库存错误"),
    IO_EXCEPTION(20001, "读写文件错误"),
    FILE_SERVER_EXCEPTION(30001, "文件服务器错误"),
    ES_SERVER_EXCEPTION(40001, "检索服务器错误"),
    THREAD_EXECUTION_EXCEPTION(50001, "线程池线程执行异常"),
    FEIGN_EXCEPTION(60001, "远程服务不可用");
    private final int code;
    private final String msg;

    ErrMsg(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
