package com.sparta.fritown.global.exception.handler;

import com.sparta.fritown.global.exception.custom.ServiceException;
import com.sparta.fritown.global.exception.custom.TokenException;
import com.sparta.fritown.global.exception.dto.ErrorResponseDto;
import com.sparta.fritown.global.exception.util.CustomResponseUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice // global exception handling annotation
@RequiredArgsConstructor
public class CustomExceptionHandler {

    @ExceptionHandler(ServiceException.class)
    public ResponseEntity<ErrorResponseDto> handleServiceException(ServiceException e) {
        return CustomResponseUtil.fail(e.getCode(), e.getMessage(), e.getStatus());
    }

    @ExceptionHandler(TokenException.class)
    public ResponseEntity<ErrorResponseDto> handleTokenException(TokenException e) {
        return CustomResponseUtil.fail(e.getCode(), e.getMessage(), e.getStatus());
    }

}
