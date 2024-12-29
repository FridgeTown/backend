package com.sparta.fritown.global.docs;

import com.sparta.fritown.domain.dto.user.LoginRequestDto;
import com.sparta.fritown.domain.dto.user.RegisterRequestDto;
import com.sparta.fritown.global.security.dto.StatusResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;

public interface AuthControllerDocs {
    @Operation(
            summary = "로그인 API",
            description = "사용자가 제공한 ID 토큰을 통해 인증을 수행하고 액세스 토큰을 반환합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그인 성공", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = StatusResponseDto.class),
                    examples = @ExampleObject(value = "{ \"status\": \"success\", \"data\": { \"accessToken\": \"<access_token>\" } }")
            )),
            @ApiResponse(responseCode = "401", description = "토큰 검증 실패 또는 유효하지 않은 사용자", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = StatusResponseDto.class),
                    examples = @ExampleObject(value = "{ \"status\": \"error\", \"message\": \"유효하지 않은 토큰입니다.\" }")
            ))
    })
    ResponseEntity<StatusResponseDto> login(@RequestBody(description = "로그인 요청 정보", required = true, content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = LoginRequestDto.class),
            examples = @ExampleObject(value = "{ \"email\": \"user@example.com\", \"provider\": \"google\", \"idToken\": \"<id_token>\" }")
    )) LoginRequestDto loginRequestDto);

    @Operation(
            summary = "회원가입 API",
            description = "사용자의 회원가입을 처리한 후 자동으로 로그인하여 액세스 토큰을 반환합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "회원가입 성공 및 로그인 완료", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = StatusResponseDto.class),
                    examples = @ExampleObject(value = "{ \"status\": \"success\", \"data\": { \"accessToken\": \"<access_token>\" } }")
            )),
            @ApiResponse(responseCode = "400", description = "회원가입 실패 또는 요청 데이터가 잘못됨", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = StatusResponseDto.class),
                    examples = @ExampleObject(value = "{ \"status\": \"error\", \"message\": \"회원가입 요청이 잘못되었습니다.\" }")
            ))
    })
    ResponseEntity<StatusResponseDto> signup(@RequestBody(description = "회원가입 요청 정보", required = true, content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = RegisterRequestDto.class),
            examples = @ExampleObject(value = "{ \"email\": \"user@example.com\", \"provider\": \"google\", \"name\": \"User Name\", \"idToken\": \"<id_token>\" }")
    )) RegisterRequestDto registerRequestDto);
}