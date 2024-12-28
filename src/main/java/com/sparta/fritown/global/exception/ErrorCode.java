package com.sparta.fritown.global.exception;


import org.springframework.http.HttpStatus;

public enum ErrorCode implements ApiCode {

    //exception
    IO_EXCEPTION(HttpStatus.BAD_REQUEST, "E001", "IO error"),
    USER_NOT_ACCEPTABLE(HttpStatus.NOT_ACCEPTABLE, "E002", "유저가 허용되지 않습니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND,"E003","유저를 찾지 못했습니다"),

    // match
    MATCH_NOT_FOUND(HttpStatus.NOT_FOUND,"M001","매치를 찾지 못했습니다"),
    USER_MATCH_NOT_FOUND(HttpStatus.NOT_FOUND,"M002","유저 매치를 찾지 못했습니다"),

    // user
    RECOMMENDED_USERS_NOT_FOUND(HttpStatus.NOT_FOUND,"U001","추천할 사용자가 없습니다");


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