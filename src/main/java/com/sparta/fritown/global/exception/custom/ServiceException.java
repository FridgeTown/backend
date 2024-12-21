package com.sparta.fritown.global.exception.custom;

import com.sparta.fritown.global.exception.ErrorCode;
import com.sparta.fritown.global.exception.dto.ErrorResponseDto;
import lombok.Getter;
import org.springframework.http.HttpStatus;


@Getter
public class ServiceException extends RuntimeException {

    private final HttpStatus status;
    private final String code;

    private ServiceException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.status = errorCode.getStatus();
        this.code = errorCode.getCode();
    }

    private ServiceException(ErrorCode errorCode, String customMessage) {
        super(customMessage);
        this.status = errorCode.getStatus();
        this.code = errorCode.getCode();
    }

    public static ServiceException of(ErrorCode errorCode) {
        return new ServiceException(errorCode);
    }

    public static ServiceException of(ErrorCode errorCode, String customMessage) {
        return new ServiceException(errorCode, customMessage);
    }

}
