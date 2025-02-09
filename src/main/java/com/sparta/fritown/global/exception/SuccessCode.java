package com.sparta.fritown.global.exception;

import org.springframework.http.HttpStatus;

public enum SuccessCode implements ApiCode {
    OK(HttpStatus.OK, "C001", "Well done"),
    CREATED(HttpStatus.CREATED, "C002", "Created successfully"),

    //user
    USER_DELETED(HttpStatus.OK, "U001", "성공적으로 탈퇴하였습니다"),
    USER_BIO_UPDATED(HttpStatus.OK, "U002", "성공적으로 유저 Bio가 수정되었습니다."),
    USER_WEIGHT_UPDATED(HttpStatus.OK, "U003", "성공적으로 유저 몸무게가 수정되었습니다."),

    //match
    MATCHED_USERS(HttpStatus.OK, "M001", "스파링 했던 상대들을 성공적으로 반환하였습니다."),
    MATCHING_USERS(HttpStatus.OK, "M002", "스파링 예정 상대들을 성공적으로 반환하였습니다."),
    MATCHING_ACCEPT(HttpStatus.OK, "M003", "매칭이 성사되었습니다."),
    MATCHING_REJECT(HttpStatus.OK, "M004", "매칭 거절이 완료되었습니다."),
    MATCH_REQUEST(HttpStatus.OK, "M005", "스파링을 성공적으로 요청하였습니다."),
    MATCHES_PENDING(HttpStatus.OK, "M006", "요청 받은 스파링을 성공적으로 반환하였습니다." ),

    //image
    IMAGE_UPLOADED(HttpStatus.OK, "I001", "이미지를 성공적으로 업로드 하였습니다."),

    //live
    LIVE_LIST(HttpStatus.OK, "L001", "실시간 방송중인 리스트를 성공적으로 반환하였습니다."),
    LIVE_WATCH_STARTED(HttpStatus.OK,"L002","매치의 viewNum을 성공적으로 1 증가시켰습니다."),
    LIVE_WATCH_ENDED(HttpStatus.OK,"L003","매치의 viewNum을 성공적으로 1 감소시켰습니다."),
    LIVE_PROGRESS(HttpStatus.OK, "L004", "매치 상태를 PROGRESS로 성공적으로 설정하였습니다."),
    LIVE_DONE(HttpStatus.OK, "L005", "매치 상태를 DONE으로 성공적으로 설정하였습니다."),

    //vote
    SUBSCRIBE_SUCCESS(HttpStatus.OK, "V001", "구독이 성공적으로 완료되었습니다."),
    UNSUBSCRIBE_SUCCESS(HttpStatus.OK, "V002", "구독이 성공적으로 해지되었습니다."),
    VOTE_SUCCESS(HttpStatus.OK, "V003", "투표가 성공적으로 완료되었습니다."),

    // guest
    GUEST_ID_GENERATED(HttpStatus.OK, "G001", "게스트 ID가 성공적으로 생성되었습니다."),

    // punchGame
    PUNCH_GAME_STARTED(HttpStatus.OK,"PG001", "펀치 게임이 성공적으로 시작되었습니다."),
    PUNCH_GAME_ENDED(HttpStatus.OK, "PG002", "펀치 게임이 성공적으로 종료되었습니다.");


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