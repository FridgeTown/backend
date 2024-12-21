package com.sparta.fritown.global.exception;

import org.springframework.http.HttpStatus;

public interface ApiCode {
    HttpStatus getStatus();
    String getCode();
    String getMessage();
}

