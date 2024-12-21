package com.sparta.fritown.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum SuccessCode implements ApiCode {

    //Common
    CREATED(HttpStatus.CREATED, "C001", "Created"),
    OK(HttpStatus.OK, "C002", "well done");

    private final HttpStatus status;
    private final String code;
    private final String message;

    SuccessCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
