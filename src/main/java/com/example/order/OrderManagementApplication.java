package com.example.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 订单管理系统主应用类
 * 
 * @author Order Management Team
 * @version 0.1.0
 * @since 2024-01-01
 */
@SpringBootApplication
public class OrderManagementApplication {

    /**
     * 应用程序入口点
     * 
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        SpringApplication.run(OrderManagementApplication.class, args);
    }
} 