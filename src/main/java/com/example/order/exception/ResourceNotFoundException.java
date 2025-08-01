package com.example.order.exception;

/**
 * 资源未找到异常
 * 
 * @author Order Management System
 * @version 1.0
 * @since 2024-01-01
 */
public class ResourceNotFoundException extends CustomException {
    
    public ResourceNotFoundException(String message) {
        super(message);
    }
    
    public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s not found with %s : '%s'", resourceName, fieldName, fieldValue));
    }
} 