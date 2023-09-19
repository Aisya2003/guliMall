package com.example.mall.common.model.exception;

public class ThreadPoolException extends RuntimeException {
    public ThreadPoolException(String message) {
        super(message);
    }

    public ThreadPoolException(String message, Throwable cause) {
        super(message, cause);
    }

    public static void error(String message) {
        throw new ThreadPoolException(message);
    }

    public static void error(String message, Throwable cause) {
        throw new ThreadPoolException(message, cause);
    }
}
