package com.sparta.fritown.global.docs;

import com.sparta.fritown.domain.dto.punchGame.PunchGameStartRequestDto;
import com.sparta.fritown.domain.dto.punchGame.PunchGameStartResponseDto;
import com.sparta.fritown.global.exception.dto.ResponseDto;
import com.sparta.fritown.global.security.dto.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;

@Tag(name = "PunchGame", description = "펀치 게임 관련 API")
public interface PunchGameControllerDocs {

    @Operation(summary = "펀치 게임 시작", description = "채널 ID를 기반으로 상대방 유저 정보를 반환합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "펀치 게임이 성공적으로 시작되었습니다.", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResponseDto.class),
                    examples = @ExampleObject(
                            name = "성공 응답 예시",
                            value = """
                            {
                                "success": true,
                                "code": "PG001",
                                "message": "펀치 게임이 성공적으로 시작되었습니다.",
                                "data": {
                                    "opponentId": 2,
                                    "opponentNickname": "Boxer123",
                                    "opponentGender": "MALE",
                                    "opponentAge": 25,
                                    "opponentHeight": 180,
                                    "opponentWeight": 75,
                                    "opponentWeightClass": "MIDDLE",
                                    "opponentProfileImg": "https://example.com/profile.jpg"
                                }
                            }
                            """
                    )
            )),
            @ApiResponse(responseCode = "404", description = "매치를 찾을 수 없음", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(type = "string", example = "매치를 찾을 수 없습니다.")
            ))
    })
    @PostMapping("/start")
    ResponseDto<PunchGameStartResponseDto> punchGameStart(
            @RequestBody(description = "펀치 게임 시작 요청 데이터", required = true, content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = PunchGameStartRequestDto.class),
                    examples = @ExampleObject(
                            name = "요청 데이터 예시",
                            value = """
                            {
                                "channelId": "12345"
                            }
                            """
                    )
            )) PunchGameStartRequestDto requestDto,

            @Parameter(
                    description = "현재 인증된 사용자 정보",
                    hidden = true
            )
            @AuthenticationPrincipal UserDetailsImpl userDetails
    );
}
