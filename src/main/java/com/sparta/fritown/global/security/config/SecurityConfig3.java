package com.sparta.fritown.global.security.config;

import com.sparta.fritown.global.security.exception.MyAuthenticationFailureHandler;
import com.sparta.fritown.global.security.exception.MyAuthenticationSuccessHandler;
import com.sparta.fritown.global.security.service.CustomOAuth2UserService;
import com.sparta.fritown.global.security.util.JwtAuthFilter;
import com.sparta.fritown.global.security.util.JwtExceptionFilter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@Slf4j
public class SecurityConfig3 {
    // security 관련 보안 filter, 로직 처리 (JWT 인증, OAuth2, 권한 부여)
    private final MyAuthenticationSuccessHandler oAuth2LoginSuccessHandler;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final JwtAuthFilter jwtAuthFilter;
    private final MyAuthenticationFailureHandler oAuth2LoginFailureHandler;
    private final JwtExceptionFilter jwtExceptionFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        log.info("filterChain started");
        http
                .httpBasic(httpBasic -> httpBasic.disable()) // 기본 로그인 창으로 이동되지 않도록 비활성화
                .cors(cors -> {}) // CORS 활성화 ; 다른 origin에서 오는 요청 허용
                .csrf(csrf -> csrf.disable()) // CSRF 비활성화
                .formLogin(form -> form.disable()) // Form 로그인 비활성화
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 세션 관리 설정 ; 세션을 절대 사용하지 않겠다.
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/health/**").permitAll()
                        .requestMatchers("/login/success").permitAll()
                        .requestMatchers("/token/**").permitAll() // 토큰 발급 경로는 허용
                        .requestMatchers("/api/auth/register").permitAll()
                        .anyRequest().authenticated()) // 나머지 요청은 인증 필요
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService)) // customOAuth2UserService로, 사용자 정보 처리
                        .failureHandler(oAuth2LoginFailureHandler)
                        .successHandler(oAuth2LoginSuccessHandler));
        // JWT 인증 필터를 추가
        return http
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtExceptionFilter, JwtAuthFilter.class)
                .build();
    }
}
