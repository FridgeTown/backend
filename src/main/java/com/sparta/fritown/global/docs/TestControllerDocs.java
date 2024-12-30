package com.sparta.fritown.global.docs;

import com.sparta.fritown.global.exception.dto.ErrorResponseDto;
import com.sparta.fritown.global.exception.dto.ResponseDto;
import com.sparta.fritown.global.security.dto.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Test", description = "테스트용 API 모음")
public interface TestControllerDocs {

    @Operation(
            summary = "에러 응답 테스트",
            description = """
                    ServiceException 핸들러 작동을 확인하기 위한 테스트 API입니다.
                    호출 시 400 상태 코드와 함께 에러 응답이 반환됩니다.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "400",
                    description = "에러 발생 시 status, message, code가 포함된 응답 반환",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class),
                            examples = @ExampleObject(value = """
                                    {
                                        "status": "error",
                                        "message": "잘못된 요청입니다.",
                                        "code": "400"
                                    }
                                    """))
            )
    })
    String errorHealthCheck();

    @Operation(
            summary = "성공 응답 테스트",
            description = """
                    성공적인 응답을 확인하기 위한 테스트 API입니다.
                    호출 시 200 상태 코드와 함께 성공 응답이 반환됩니다.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "성공적으로 상태 확인",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDto.class),
                            examples = @ExampleObject(value = """
                                    {
                                        "status": "success",
                                        "data": null
                                    }
                                    """))
            )
    })
    ResponseDto<Void> successHealthCheck();

    @Operation(
            summary = "인증된 사용자 ID 반환 테스트",
            description = """
                    인증된 사용자의 ID를 반환하는 API입니다.
                    인증이 필요한 API로, 호출 시 사용자의 고유 ID를 반환합니다.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "성공적으로 사용자 ID 반환",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDto.class),
                            examples = @ExampleObject(value = """
                                    {
                                        "status": "success",
                                        "data": 12345
                                    }
                                    """))
            )
    })
    ResponseDto<Long> successAuthCheck(
            @Parameter(description = "인증된 사용자 정보", hidden = true)
            UserDetailsImpl userDetails
    );

    @Operation(
            summary = "헬스 체크 API",
            description = """
                    서버 상태 확인을 위한 간단한 API입니다.
                    호출 시 서버의 상태를 'OK' 문자열로 반환합니다.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "서버 상태 정상 확인",
                    content = @Content(mediaType = "text/plain",
                            examples = @ExampleObject(value = "OK"))
            )
    })
    String healthCheck();

    @Operation(
            summary = "인증 상태 테스트 (SecurityContextHolder 사용)",
            description = """
                    SecurityContextHolder를 통해 인증 상태를 확인하는 API입니다.
                    인증된 사용자의 인증 정보를 로그로 출력합니다.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "SecurityContextHolder 인증 상태 확인 성공",
                    content = @Content(mediaType = "text/plain",
                            examples = @ExampleObject(value = "Test Endpoint"))
            )
    })
    String testAuthentication();

    @Operation(
            summary = "인증 상태 테스트 (UserDetails 사용)",
            description = """
                    AuthenticationPrincipal을 통해 인증된 사용자 정보를 반환하는 API입니다.
                    인증된 사용자의 이메일을 반환합니다.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "AuthenticationPrincipal 인증 상태 확인 성공",
                    content = @Content(mediaType = "text/plain",
                            examples = @ExampleObject(value = "Authenticated user: user@example.com"))
            )
    })
    String testAuthentication(
            @Parameter(description = "인증된 사용자 정보", hidden = true)
            UserDetailsImpl userDetails
    );
}