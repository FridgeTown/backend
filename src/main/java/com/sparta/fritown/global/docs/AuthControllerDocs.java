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
                    사용자가 제공한 정보를 기반으로 회원가입을 처리한 후, 자동으로 로그인을 수행합니다.
                    회원가입 성공 시, 액세스 토큰과 채팅 토큰을 반환합니다.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "회원가입 및 로그인 성공", content = @Content(
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
            @ApiResponse(responseCode = "400", description = "회원가입 요청 실패 (잘못된 요청)", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = StatusResponseDto.class),
                    examples = @ExampleObject(value = """
                            {
                                "status": "error",
                                "message": "회원가입 요청이 잘못되었습니다."
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
    ResponseEntity<StatusResponseDto> signup(@RequestBody(description = "회원가입 요청 정보", required = true, content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = RegisterRequestDto.class),
            examples = @ExampleObject(value = """
                    {
                        "email": "user@example.com",
                        "provider": "google",
                        "name": "User Name",
                        "idToken": "<id_token>"
                    }
                    """)
    )) RegisterRequestDto registerRequestDto);
}