package com.nageoffer.shortlink.common.config;


import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty; // 示例条件

@AutoConfiguration  // Spring Boot 3.x 新注解，标记为自动配置类
@ComponentScan(basePackages = "com.nageoffer.shortlink.common") // 自扫描的包
@ConditionalOnProperty(prefix = "autoScanTool", name = "enabled", havingValue = "true", matchIfMissing = true) // 条件配置
public class AutoScanConfig {
    // 这里可以添加其他配置，如 @Bean 定义
    // 避免反射密集操作，以兼容 AOT
}
