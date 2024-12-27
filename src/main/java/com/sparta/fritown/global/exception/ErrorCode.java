package com.sparta.fritown.global.exception;


import org.springframework.http.HttpStatus;

public enum ErrorCode implements ApiCode {

    //exception
    IO_EXCEPTION(HttpStatus.BAD_REQUEST, "E001", "IO error"),
    USER_NOT_ACCEPTABLE(HttpStatus.NOT_ACCEPTABLE, "E002", "유저가 허용되지 않습니다."),
    MATCH_NOT_FOUND(HttpStatus.NOT_FOUND, "E003", "매치가 존재하지 않습니다."),
    MATCH_NOT_PENDING(HttpStatus.FORBIDDEN, "E004", "매치가 대기 상태가 아닙니다."),
    USER_NOT_PARTICIPANT(HttpStatus.FORBIDDEN, "E005", "도전자 또는 수락자가 아닙니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "E006", "유저가 존재하지 않습니다.");


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