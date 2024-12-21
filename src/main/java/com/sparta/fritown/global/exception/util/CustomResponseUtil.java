package com.sparta.fritown.global.exception.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.fritown.global.exception.ErrorCode;
import com.sparta.fritown.global.exception.SuccessCode;
import com.sparta.fritown.global.exception.custom.ServiceException;
import com.sparta.fritown.global.exception.dto.ErrorResponseDto;
import com.sparta.fritown.global.exception.dto.ResponseDto;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.io.PrintWriter;

@Slf4j
public class CustomResponseUtil {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static ResponseEntity<ResponseDto<Void>> success(SuccessCode successCode) {
        logging("Success", successCode.getCode(), successCode.getMessage());
        ResponseDto<Void> successResponse = ResponseDto.success(successCode); // successResponse 생성을 통해 json에 넣어줄 값 정리
        return ResponseEntity.status(successCode.getStatus()).body(successResponse);
    }

    public static ResponseEntity<ErrorResponseDto> fail(String code, String message, HttpStatus status) {
        logging("Failure", code, message);
        ErrorResponseDto errorResponseDto = ErrorResponseDto.failure(status, code, message);
        return ResponseEntity.status(status).body(errorResponseDto);
    }


    private static void logging(String type, String code, String message) {
        log.info("{} CustomResponse : [{}] {}", type, code, message);
    }

}
