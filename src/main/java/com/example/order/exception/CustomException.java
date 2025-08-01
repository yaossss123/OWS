package com.example.order.exception;

/**
 * 自定义业务异常基类
 * 
 * @author Order Management System
 * @version 1.0
 * @since 2024-01-01
 */
public class CustomException extends RuntimeException {
    
    public CustomException(String message) {
        super(message);
    }
    
    public CustomException(String message, Throwable cause) {
        super(message, cause);
    }
} 