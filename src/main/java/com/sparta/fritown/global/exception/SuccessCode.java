package com.sparta.fritown.global.exception;

import org.springframework.http.HttpStatus;

public enum SuccessCode implements ApiCode {
    OK(HttpStatus.OK, "C001", "Well done"),
    CREATED(HttpStatus.CREATED, "C002", "Created successfully"),

    //match
    MATCHED_USERS(HttpStatus.OK, "M001", "스파링 했던 상대들이 성공적으로 반환되었습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;

    SuccessCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }

    public HttpStatus getStatus() { return status; }
    public String getCode() { return code; }
    public String getMessage() { return message; }
}