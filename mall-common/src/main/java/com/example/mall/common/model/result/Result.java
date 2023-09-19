package com.example.mall.common.model.result;

import com.alibaba.fastjson2.JSON;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.Data;
import lombok.Getter;
import org.apache.http.HttpStatus;

import java.io.Serializable;
import java.util.List;

@Getter
public class Result<T> implements Serializable {
    private Object data;
    private boolean success;
    private int code;
    private String msg;

    public T castData(Class<T> tClass) {
        return JSON.parseObject(JSON.toJSONString(this.data), tClass);
    }

    public Result(Object data, boolean success, int code, String message) {
        this.data = data;
        this.success = success;
        this.code = code;
        this.msg = message;
    }

    public Result() {

    }

    public static Result ok(Object data, int code, String message) {
        return new Result(data, true, code, message);
    }
    public static Result ok(Object data, String message) {
        return new Result(data, true, HttpStatus.SC_OK, message);
    }

    public static Result ok(Object data, int code) {
        return new Result(data, true, code, "");
    }

    public static Result ok(Object data) {
        return new Result(data, true, HttpStatus.SC_OK, "");
    }

    public static Result ok() {
        return new Result(null, true, HttpStatus.SC_OK, "");
    }

    public static Result fail() {
        return new Result(null, false, HttpStatus.SC_INTERNAL_SERVER_ERROR, "");
    }

    public static Result fail(String message) {
        return new Result(null, false, HttpStatus.SC_SERVICE_UNAVAILABLE, message);
    }

    public static Result fail(int code, String message) {
        return new Result(null, false, code, message);
    }

    public static Result fail(Object data, int code, String message) {
        return new Result(data, false, code, message);
    }
}
