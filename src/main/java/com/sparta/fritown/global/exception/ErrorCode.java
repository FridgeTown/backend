package com.sparta.fritown.global.exception;


import org.springframework.http.HttpStatus;

public enum ErrorCode implements ApiCode {

    //exception
    IO_EXCEPTION(HttpStatus.BAD_REQUEST, "E001", "IO error"),

    //user
    USER_NOT_ACCEPTABLE(HttpStatus.NOT_ACCEPTABLE, "U001", "유저가 허용되지 않습니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "U002", "해당 유저를 찾을 수 없습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;

    ErrorCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }

    public HttpStatus getStatus() { return status; }
    public String getCode() { return code; }
    public String getMessage() { return message; }
}