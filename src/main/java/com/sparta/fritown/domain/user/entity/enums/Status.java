package com.sparta.fritown.domain.user.entity.enums;

public enum Status {
    PENDING("대기중"),
    ACCEPTED("수락됨"),
    REJECTED("거절됨"),
    PROGRESS("경기중"),
    DONE("경기종료"),
    CANCELED("취소됨");

    private String message;

    Status(String message) {
        this.message = message;
    }

}
