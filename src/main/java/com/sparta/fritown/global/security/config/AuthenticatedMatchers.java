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
}

