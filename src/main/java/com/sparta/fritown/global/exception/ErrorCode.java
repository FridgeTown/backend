package com.sparta.fritown.global.exception;


import org.springframework.http.HttpStatus;

public enum ErrorCode implements ApiCode {

    //exception
    IO_EXCEPTION(HttpStatus.BAD_REQUEST, "E001", "IO error"),
    USER_NOT_ACCEPTABLE(HttpStatus.NOT_ACCEPTABLE, "E002", "유저가 허용되지 않습니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND,"E003","유저를 찾지 못했습니다."),

    // match
    MATCH_NOT_FOUND(HttpStatus.NOT_FOUND,"M001","매치를 찾지 못했습니다."),
    SELF_FIGHT_REQUEST(HttpStatus.NOT_ACCEPTABLE,"M002" , "자신에게 스파링 요청을 보낼 수 없습니다."),
    USER_NOT_CHALLENGED_TO(HttpStatus.NOT_ACCEPTABLE, "M003", "도전 받은 유저가 아닙니다."),
    MATCH_NOT_PENDING(HttpStatus.NOT_ACCEPTABLE, "M004", "대기 상태의 매치가 아닙니다."),

    //user
    USER_MATCH_NOT_FOUND(HttpStatus.NOT_FOUND,"U001","유저 매치를 찾지 못했습니다."),
    USER_OP_NOT_FOUND(HttpStatus.NOT_FOUND, "U002" ,"대결 상대 정보를 찾지 못했습니다.");


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