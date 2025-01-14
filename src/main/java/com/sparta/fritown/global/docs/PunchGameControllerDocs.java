package com.sparta.fritown.global.docs;

import com.sparta.fritown.domain.dto.punchGame.PunchGameEndResponseDto;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

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

    @Operation(summary = "펀치 게임 종료", description = "펀치 게임이 종료되며, 결과 데이터를 반환합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "펀치 게임이 성공적으로 종료되었습니다.", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResponseDto.class),
                    examples = @ExampleObject(
                            name = "성공 응답 예시",
                            value = """
                        {
                            "success": true,
                            "code": "PG002",
                            "message": "펀치 게임이 성공적으로 종료되었습니다.",
                            "data": [
                                {
                                    "nickname": "Player1",
                                    "finalPunch": 150,
                                    "avgHeartRate": 85.5,
                                    "finalCalorie": 200
                                },
                                {
                                    "nickname": "Player2",
                                    "finalPunch": 130,
                                    "avgHeartRate": 90.2,
                                    "finalCalorie": 180
                                }
                            ]
                        }
                        """
                    )
            )),
            @ApiResponse(responseCode = "404", description = "채널 정보를 찾을 수 없음", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(type = "string", example = "채널 정보를 찾을 수 없습니다.")
            ))
    })
    @GetMapping("/end/{channelId}")
    ResponseDto<List<PunchGameEndResponseDto>> punchGameEnd(
            @Parameter(
                    description = "펀치 게임이 종료될 채널 ID",
                    example = "12345",
                    required = true
            ) @PathVariable String channelId
    );
}
