package com.sparta.fritown.global.exception.dto;

import com.sparta.fritown.global.exception.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ErrorResponseDto extends BaseResponse {
    private final String code;

    private ErrorResponseDto(int status, String message, String code) {
        super(status, message);
        this.code = code;
    }

    public static ErrorResponseDto failure(HttpStatus status, String code, String message) {
        return new ErrorResponseDto(status.value(), message, code);
    }


}