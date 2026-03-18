package org.example.internal_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.retry.annotation.EnableRetry;

@EnableRetry        // Retryable, Recover 기능 설정
@EnableFeignClients // OpenFeign 기본 설정
@SpringBootApplication
public class ExternalApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(ExternalApiApplication.class, args);
    }

}
