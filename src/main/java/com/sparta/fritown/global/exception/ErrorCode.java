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
    USER_OP_NOT_FOUND(HttpStatus.NOT_FOUND, "U002" ,"대결 상대 정보를 찾지 못했습니다."),
    WEIGHT_NOT_ACCEPTABLE(HttpStatus.NOT_ACCEPTABLE, "U003", "유효하지 않은 몸무게입니다."),
    USER_NICKNAME_INVALID(HttpStatus.BAD_REQUEST, "U004", "닉네임은 2자에서 7자 사이로, 영어 또는 한글만 사용 가능합니다."),

    USER_EMAIL_DUPLICATE(HttpStatus.BAD_REQUEST, "U005", "기존에 존재하는 이메일입니다."),
    // image
    IMAGE_UPLOAD_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "I001", "이미지 업로드에 실패하였습니다."),

    // live
    VIEW_COUNT_CANNOT_BE_NEGATIVE(HttpStatus.BAD_REQUEST, "L001", "매치의 viewNum이 0보다 작을 수 없습니다.");


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