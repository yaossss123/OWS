package com.example.order.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Arrays;

/**
 * Swagger配置类
 * 
 * @desc 配置OpenAPI 3.0文档
 * @author Order Management System
 * @version 1.0.0
 */
@Slf4j
@Configuration
public class SwaggerConfig {

    @Value("${server.port:8080}")
    private String serverPort;

    @Value("${spring.application.name:Order Management System}")
    private String applicationName;

    /**
     * 配置OpenAPI
     * 
     * @return OpenAPI配置对象
     */
    @Bean
    public OpenAPI customOpenAPI() {
        log.info("初始化Swagger配置");
        
        return new OpenAPI()
                .info(apiInfo())
                .servers(Arrays.asList(
                        new Server().url("http://localhost:" + serverPort).description("本地开发环境"),
                        new Server().url("https://api.ordermanagement.com").description("生产环境")
                ))
                .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
                .components(new Components()
                        .addSecuritySchemes("Bearer Authentication", createAPIKeyScheme()));
    }

    /**
     * API信息配置
     * 
     * @return API信息对象
     */
    private Info apiInfo() {
        return new Info()
                .title("订单管理系统 API")
                .description("基于Spring Boot的订单管理系统RESTful API文档")
                .version("1.0.0")
                .contact(new Contact()
                        .name("开发团队")
                        .email("dev@ordermanagement.com")
                        .url("https://github.com/ordermanagement"))
                .license(new License()
                        .name("MIT License")
                        .url("https://opensource.org/licenses/MIT"));
    }

    /**
     * 创建API密钥安全方案
     * 
     * @return 安全方案对象
     */
    private SecurityScheme createAPIKeyScheme() {
        return new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .bearerFormat("JWT")
                .scheme("bearer")
                .description("JWT访问令牌，格式：Bearer {token}");
    }
} 