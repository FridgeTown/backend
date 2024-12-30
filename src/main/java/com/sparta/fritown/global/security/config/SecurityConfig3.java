package com.sparta.fritown.global.security.config;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@Slf4j
public class SecurityConfig3 {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)  // CSRF 보호 비활성화
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(AuthenticatedMatchers.loginArray).permitAll()
                        .requestMatchers(AuthenticatedMatchers.testArray).permitAll()
                        .requestMatchers(AuthenticatedMatchers.swaggerArray).permitAll()
                        .anyRequest().authenticated())  // 다른 모든 요청은 인증 필요
                .logout(LogoutConfigurer::permitAll)
                .securityContext(securityContext -> securityContext.requireExplicitSave(false));

        return http.build();
    }
}
