package com.sparta.fritown.global.web.config;

import lombok.NonNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig {
    // 전역적인 요청/응답 로직을 처리(CORS 정책, 전역 인터셉터); SecurityConfig3 동작 후, WebConfig 동작
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(@NonNull CorsRegistry registry) {
                registry.addMapping("/**") // 모든 엔드포인트 허용
                        .allowedOrigins("http://localhost:5173") // Vite 서버 주소
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // 허용할 HTTP 메서드
                        .allowedHeaders("*") // 모든 헤더 허용
                        .allowCredentials(true); // 쿠키 포함 요청 허용
            }
        };
    }
}
