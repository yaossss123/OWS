package com.example.order.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * JPA配置类
 * 
 * 功能: 配置JPA审计功能和仓库扫描
 * 逻辑链: 配置加载 -> 审计启用 -> 仓库扫描 -> 功能激活
 * 注意事项: 需要配合BaseEntity使用，启用审计功能
 * 
 * @author Order Management Team
 * @version 0.1.0
 * @since 2024-01-01
 */
@Configuration
@EnableJpaAuditing
@EnableJpaRepositories(basePackages = "com.example.order.repository")
public class JpaConfig {
    // JPA配置已通过注解完成
} 