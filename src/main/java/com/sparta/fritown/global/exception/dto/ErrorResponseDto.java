package com.sparta.fritown.global.exception.dto;

import org.springframework.http.HttpStatus;

public class ErrorResponseDto extends BaseResponse {
    private final String code;

    private ErrorResponseDto(int status, String message, String code) {
        super(status, message);
        this.code = code;
    }

    public static ErrorResponseDto fromErrorCode(HttpStatus status, String code, String message) {
        /* 응답 객체를 생성하기 위함.
         * 정적 팩토리 메서드란 생성자를 호출하면서도, 객체 생성 과정을 캡슐화한 메서드
         * 이점: ErrorResponseDto의 내부 구조가 수정 (필드 이름 수정,,) 되어도, 수정할 필요가 없음.
         */
        return new ErrorResponseDto(status.value(), message, code);
    }
}
