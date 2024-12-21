package com.sparta.fritown.global.security.config;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class AuthenticatedMatchers {
    public static final String[] loginArray = {
            "/login/sucess",
            "/token/**",
            "/api/auth/register"
    };

    public static final String[] testArray = {
            "/health/**"
    };

    public static final String[] swaggerArray = {
            "/swagger-ui/**",
            "/swagger",
            "/swagger/**",
            "/v3/api-docs/**",
            "/swagger-ui.html"
    };
}

