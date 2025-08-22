package com.nageoffer.shortlink.admin;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;

/**
 * @author AlanC
 */
@Slf4j
@SpringBootApplication
public class ShortLinkAdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShortLinkAdminApplication.class, args);
    }

    /**
     * 启动成功后打印提示信息
     */
    @Bean
    public ApplicationListener<ApplicationReadyEvent> readyEventApplicationListener() {
        return event -> {
            String port = event.getApplicationContext().getEnvironment().getProperty("server.port", "8080");
            String appName = event.getApplicationContext().getEnvironment().getProperty("spring.application.name", "short-link-admin");
            String profile = event.getApplicationContext().getEnvironment().getProperty("spring.profiles.active", "default");

            log.info("============================================");
            log.info("🚀 应用 [{}] 启动成功！", appName);
            log.info("🌐 访问地址: http://localhost:{}", port);
            log.info("📌 当前环境: {}", profile);
            log.info("============================================");
        };
    }
}
