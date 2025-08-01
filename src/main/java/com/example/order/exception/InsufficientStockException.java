package com.example.order.exception;

/**
 * 库存不足异常
 * 
 * @author Order Management System
 * @version 1.0
 * @since 2024-01-01
 */
public class InsufficientStockException extends CustomException {
    
    public InsufficientStockException(String message) {
        super(message);
    }
    
    public InsufficientStockException(String productName, int requestedQuantity, int availableQuantity) {
        super(String.format("Insufficient stock for product '%s'. Requested: %d, Available: %d", 
                productName, requestedQuantity, availableQuantity));
    }
} 