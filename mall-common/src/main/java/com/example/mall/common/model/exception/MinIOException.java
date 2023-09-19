package com.example.mall.common.model.exception;

public class MinIOException extends RuntimeException {
    public MinIOException(String message) {
        super(message);
    }

    public MinIOException(String message, Throwable cause) {
        super(message, cause);
    }

    public static void error(String message) {
        throw new MinIOException(message);
    }

    public static void error(String message, Throwable cause) {
        throw new MinIOException(message, cause);
    }

}
