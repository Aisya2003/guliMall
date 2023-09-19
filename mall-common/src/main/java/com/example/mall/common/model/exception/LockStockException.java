package com.example.mall.common.model.exception;

public class LockStockException extends RuntimeException {
    public LockStockException(String message) {
        super(message);
    }

    public LockStockException(String message, Throwable cause) {
        super(message, cause);
    }

    public static void error(String message) {
        throw new LockStockException(message);
    }

    public static void error(String message, Throwable cause) {
        throw new LockStockException(message, cause);
    }

}
