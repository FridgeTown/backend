package com.sparta.fritown.global.docs;

import com.sparta.fritown.global.exception.dto.ErrorResponseDto;
import com.sparta.fritown.global.exception.dto.ResponseDto;
import com.sparta.fritown.global.security.dto.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Test", description = "테스트용 API 모음")
public interface TestControllerDocs {

    @Operation(
            summary = "에러 응답 테스트",
            description = "ServiceException 핸들러 작동을 확인하기 위한 테스트 API"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "400",
                    description = "에러 발생 시 status, message, code가 포함된 응답 반환",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))
            )
    })
    String errorHealthCheck();

    @Operation(
            summary = "성공 응답 테스트",
            description = "성공적인 응답을 확인하기 위한 테스트 API"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "성공적으로 상태 확인",
                    content = @Content(schema = @Schema(implementation = ResponseDto.class))
            )
    })
    ResponseDto<Void> successHealthCheck();

    @Operation(
            summary = "인증된 사용자 ID 반환 테스트",
            description = "인증된 사용자의 ID를 반환합니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "성공적으로 사용자 ID 반환",
                    content = @Content(schema = @Schema(implementation = ResponseDto.class))
            )
    })
    ResponseDto<Long> successAuthCheck(
            @Parameter(description = "인증된 사용자 정보", hidden = true)
            UserDetailsImpl userDetails
    );

    @Operation(
            summary = "헬스 체크 API",
            description = "서버 상태 확인을 위한 간단한 API"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "서버 상태 정상 확인",
                    content = @Content(mediaType = "text/plain")
            )
    })
    String healthCheck();

    @Operation(
            summary = "인증 상태 테스트 (SecurityContextHolder 사용)",
            description = "SecurityContextHolder를 통해 인증 상태를 확인합니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "SecurityContextHolder 인증 상태 확인 성공",
                    content = @Content(mediaType = "text/plain")
            )
    })
    String testAuthentication();

    @Operation(
            summary = "인증 상태 테스트 (UserDetails 사용)",
            description = "AuthenticationPrincipal을 통해 인증된 사용자 정보를 반환합니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "AuthenticationPrincipal 인증 상태 확인 성공",
                    content = @Content(mediaType = "text/plain")
            )
    })
    String testAuthentication(
            @Parameter(description = "인증된 사용자 정보", hidden = true)
            UserDetailsImpl userDetails
    );
}