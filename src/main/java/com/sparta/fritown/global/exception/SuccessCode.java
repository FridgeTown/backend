package com.sparta.fritown.global.exception;

import org.springframework.http.HttpStatus;

public enum SuccessCode implements ApiCode {
    OK(HttpStatus.OK, "C001", "Well done"),
    CREATED(HttpStatus.CREATED, "C002", "Created successfully"),

    //match
    MATCHED_USERS(HttpStatus.OK, "M001", "스파링 했던 상대들을 성공적으로 반환하였습니다."),
    MATCHING_USERS(HttpStatus.OK, "M002", "스파링 예정 상대들을 성공적으로 반환하였습니다."),
    MATCHING_ACCEPT(HttpStatus.OK, "M003", "매칭이 성사되었습니다."),
    MATCHING_REJECT(HttpStatus.OK, "M004", "매칭 거절이 완료되었습니다."),
    MATCH_REQUEST(HttpStatus.OK, "M003", "스파링을 성공적으로 요청하였습니다.");

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