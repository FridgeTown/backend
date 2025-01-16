package com.sparta.fritown.global.security.config;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class AuthenticatedMatchers {
    public static final String[] loginArray = {
            "/login/sucess",
            "/token/**",
            "/api/auth/register",
            "/login",
            "/signup"
    };

    public static final String[] guestArray ={
            "/voting/guest/**",
            "/live/list",
            "/live/watch/**"
    };

    public static final String[] testArray = {
            "/health/**",
            "/test/health"
    };

    public static final String[] swaggerArray = {
            "/swagger-ui/**",
            "/swagger",
            "/v3/api-docs/**",
    };

    public static final String[] socketArray = {
            "/channel/**"
    };

    public static final String[] voteArray = {
            "/simple/**"
    };
}

