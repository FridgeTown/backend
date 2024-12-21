package com.sparta.fritown.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    //Common
    IO_EXCEPTION(HttpStatus.BAD_REQUEST, "C001", "IO Error");

    private final HttpStatus status;
    private final String code;
    private final String message;

    ErrorCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }


}
