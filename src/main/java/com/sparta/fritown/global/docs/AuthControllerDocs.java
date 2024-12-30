package com.sparta.fritown.global.docs;

import com.sparta.fritown.domain.dto.user.LoginRequestDto;
import com.sparta.fritown.domain.dto.user.RegisterRequestDto;
import com.sparta.fritown.global.exception.dto.ErrorResponseDto;
import com.sparta.fritown.global.security.dto.StatusResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(name = "Auth", description = "사용자 인증 및 회원가입 관련 API")
public interface AuthControllerDocs {

    @Operation(
            summary = "로그인 API",
            description = """
                    사용자가 제공한 ID 토큰과 이메일 정보를 통해 인증을 수행합니다.
                    인증 성공 시, 액세스 토큰과 채팅 토큰을 반환합니다.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그인 성공", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = StatusResponseDto.class),
                    examples = @ExampleObject(value = """
                            {
                                "status": "success",
                                "data": {
                                    "accessToken": "<access_token>",
                                    "chatToken": "<chat_token>"
                                }
                            }
                            """)
            )),
            @ApiResponse(responseCode = "401", description = "인증 실패 또는 사용자 미존재", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = StatusResponseDto.class),
                    examples = @ExampleObject(value = """
                            {
                                "status": "error",
                                "message": "유효하지 않은 사용자입니다."
                            }
                            """)
            )),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = StatusResponseDto.class),
                    examples = @ExampleObject(value = """
                            {
                                "status": "error",
                                "message": "서버 내부 오류가 발생했습니다."
                            }
                            """)
            ))
    })
    ResponseEntity<StatusResponseDto> login(@RequestBody(description = "로그인 요청 정보", required = true, content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = LoginRequestDto.class),
            examples = @ExampleObject(value = """
                    {
                        "email": "user@example.com",
                        "provider": "google",
                        "idToken": "<id_token>"
                    }
                    """)
    )) LoginRequestDto loginRequestDto);
    @Operation(
            summary = "회원가입 API",
            description = """
                사용자가 제공한 정보를 기반으로 회원가입을 처리합니다.
                회원가입 성공 시, 로그인 정보를 반환하며, 이메일 또는 닉네임 중복 시 에러가 발생합니다.
                """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "회원가입 및 로그인 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = StatusResponseDto.class),
                            examples = @ExampleObject(value = """
                                {
                                    "status": "success",
                                    "data": {
                                        "accessToken": "<access_token>",
                                        "chatToken": "<chat_token>"
                                    }
                                }
                                """)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 (닉네임 또는 이메일 중복)",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class),
                            examples = @ExampleObject(value = """
                                {
                                    "status": 400,
                                    "code": "U004",
                                    "message": "닉네임은 2자에서 7자 사이로, 영어 또는 한글만 사용 가능합니다."
                                }
                                """)
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "이메일 중복",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class),
                            examples = @ExampleObject(value = """
                                {
                                    "status": 409,
                                    "code": "U005",
                                    "message": "기존에 존재하는 이메일입니다."
                                }
                                """)
                    )
            )
    })
    ResponseEntity<StatusResponseDto> signup(
            @RequestBody(
                    description = "회원가입 요청 정보",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = RegisterRequestDto.class),
                            examples = @ExampleObject(value = """
                                {
                                    "email": "user@example.com",
                                    "provider": "google",
                                    "profileImage": "https://example.com/profile.jpg",
                                    "gender": "FEMALE",
                                    "age": 25,
                                    "weight": 55,
                                    "height": 165,
                                    "bio": "Hello! I love coding and sports.",
                                    "role": "USER",
                                    "nickname": "User Name",
                                    "idToken": "<id_token>"
                                }
                                """)
                    )
            ) RegisterRequestDto registerRequestDto
    );
}