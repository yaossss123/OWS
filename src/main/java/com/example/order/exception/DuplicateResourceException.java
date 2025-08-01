package com.example.order.exception;

/**
 * 重复资源异常
 * 
 * @author Order Management System
 * @version 1.0
 * @since 2024-01-01
 */
public class DuplicateResourceException extends CustomException {
    
    public DuplicateResourceException(String message) {
        super(message);
    }
    
    public DuplicateResourceException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s already exists with %s : '%s'", resourceName, fieldName, fieldValue));
    }
} 