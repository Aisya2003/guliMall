package com.example.mall.common.model.exception;

public class ESException extends RuntimeException {
    public ESException(String message) {
        super(message);
    }

    public ESException(String message, Throwable cause) {
        super(message, cause);
    }

    public static void error(String message) {
        throw new ESException(message);
    }

    public static void error(String message, Throwable cause) {
        throw new ESException(message, cause);
    }
}
