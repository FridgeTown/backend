package com.sparta.fritown.global.exception.custom;

import com.sparta.fritown.global.exception.ErrorCode;
import com.sparta.fritown.global.exception.dto.ErrorResponseDto;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;

@Getter
public class TokenException extends AuthenticationException {
    private final HttpStatus status;
    private final String code;

    private TokenException(ErrorCode errorCode) { // capsulation
        super(errorCode.getMessage());
        this.status = errorCode.getStatus();
        this.code = errorCode.getCode();
    }

    private TokenException(HttpStatus status, String code, String customMessage) {
        super(customMessage);
        this.status = status;
        this.code = code;
    }

    public static TokenException of(ErrorCode errorCode) {
        return new TokenException(errorCode);
    }

    public static TokenException of(ErrorCode errorCode, String customMessage) {
        return new TokenException(errorCode.getStatus(), errorCode.getCode(), customMessage);
    }

}
