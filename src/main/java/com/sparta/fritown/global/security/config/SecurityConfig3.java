package com.sparta.fritown.global.security.config;


import com.sparta.fritown.global.security.util.JwtAuthFilter;
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
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@EnableWebSecurity
@Slf4j
public class SecurityConfig3 {

    private final JwtAuthFilter jwtAuthFilter;

    public SecurityConfig3(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)  // CSRF 보호 비활성화
                .authorizeHttpRequests(auth -> auth
                        // 게스트용 API 인증 없이 호출 가능
                        .requestMatchers(AuthenticatedMatchers.guestArray).permitAll()
                        .requestMatchers(AuthenticatedMatchers.loginArray).permitAll()
                        .requestMatchers(AuthenticatedMatchers.testArray).permitAll()
                        .requestMatchers(AuthenticatedMatchers.swaggerArray).permitAll()
                        .requestMatchers(AuthenticatedMatchers.socketArray).permitAll()
                        .requestMatchers(AuthenticatedMatchers.voteArray).permitAll()
                        .anyRequest().authenticated())  // 다른 모든 요청은 인증 필요
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .logout(LogoutConfigurer::permitAll)
                .securityContext(securityContext -> securityContext.requireExplicitSave(false));

        return http.build();
    }
}
