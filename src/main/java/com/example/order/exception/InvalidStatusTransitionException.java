package com.example.order.exception;

/**
 * 无效状态转换异常
 * 
 * @author Order Management System
 * @version 1.0
 * @since 2024-01-01
 */
public class InvalidStatusTransitionException extends CustomException {
    
    public InvalidStatusTransitionException(String message) {
        super(message);
    }
    
    public InvalidStatusTransitionException(String currentStatus, String newStatus) {
        super(String.format("Invalid status transition from '%s' to '%s'", currentStatus, newStatus));
    }
} 