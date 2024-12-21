package com.sparta.fritown.global.exception.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.fritown.global.exception.dto.ResponseDto;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.io.PrintWriter;

@Slf4j
public class CustomResponseUtil {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static void success(HttpServletResponse response, String message, HttpStatus status) {
        sucessLogging(status, message);
        ResponseDto<Void> successResponse = ResponseDto.success(message); // successResponse 생성을 통해 json에 넣어줄 값 정리
        writeJsonResponse(response, successResponse, status);
    }


    private static void sucessLogging(HttpStatus status, String message) {
        log.info("CustomResponse Success : [{}] {}", status, message);
    }

    private static void writeJsonResponse(HttpServletResponse httpResponse, Object responseDto, HttpStatus status) {
        httpResponse.setStatus(status.value()); //status.value()에는 코드 같은 거 들어감. ex) 100
        httpResponse.setContentType("application/json");
        httpResponse.setCharacterEncoding("UTF-8");

        try (PrintWriter writer = httpResponse.getWriter()) { //printWriter : 클라이언트로 데이터를 전송할 수 있는 출력 스트림 반환
            String jsonResponse = objectMapper.writeValueAsString(responseDto);
            writer.write(jsonResponse);
        } catch (IOException e) {
            log.error("Error : Error while writing httpResponse");
        }
    }

}
