package com.sparta.fritown.global.docs;

import com.sparta.fritown.domain.dto.match.MatchFutureDto;
import com.sparta.fritown.domain.dto.match.MatchPendingDto;
import com.sparta.fritown.domain.dto.match.MatchSummaryDto;
import com.sparta.fritown.domain.dto.rounds.RoundsDto;
import com.sparta.fritown.global.exception.dto.ErrorResponseDto;
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
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Tag(name = "Match", description = "매치와 관련된 API")
public interface MatchControllerDocs {

    @Operation(
            summary = "특정 매치의 라운드 정보를 가져오기",
            description = "주어진 MatchId에 대한 소모 칼로리, 심박수 등 자세한 라운드 정보를 가져옵니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "라운드 정보를 성공적으로 가져왔습니다.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = RoundsDto.class),
                            examples = @ExampleObject(value = "[{\"roundNum\": 1, \"kcal\": 300, \"heartBeat\": 120}]"))),
            @ApiResponse(responseCode = "404", description = "매치 또는 유저를 찾을 수 없습니다.",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "406", description = "유저 매치를 찾을 수 없습니다.",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @Parameter(
            name = "matchId",
            description = "매치의 고유 식별자",
            example = "1",
            required = true
    )
    ResponseDto<List<RoundsDto>> getRoundsByMatchId(@PathVariable Long matchId, @AuthenticationPrincipal UserDetailsImpl userDetails);

    @Operation(
            summary = "유저의 매치 기록 가져오기",
            description = "인증된 유저의 완료된 매치 요약 정보를 가져옵니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "매치 기록을 성공적으로 가져왔습니다.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = MatchSummaryDto.class),
                            examples = @ExampleObject(value = "[{\"matchId\": 1, \"matchInfo\": {\"totalKcal\": 900, \"avgHeartRate\": 120, \"totalPunches\": 300, \"rounds\": 3}, \"opponentNickname\": \"JohnDoe\"}]"))),
            @ApiResponse(responseCode = "404", description = "유저를 찾을 수 없습니다.",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @Parameter(
            name = "userDetails",
            description = "인증된 유저의 정보 (자동 주입)",
            required = true
    )
    ResponseDto<List<MatchSummaryDto>> getMatchHistory(@AuthenticationPrincipal UserDetailsImpl userDetails);

    @Operation(
            summary = "유저의 예정된 매치 정보 가져오기",
            description = "인증된 유저의 예정된 매치 정보(아직 완료되지 않은 매치)를 가져옵니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "예정된 매치 정보를 성공적으로 가져왔습니다.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = MatchFutureDto.class),
                            examples = @ExampleObject(value = "[{\"matchId\": 1, \"opponentNickname\": \"JaneDoe\", \"matchDate\": \"2024-01-01\", \"place\": \"Central Gym\"}]"))),
            @ApiResponse(responseCode = "404", description = "유저를 찾을 수 없습니다.",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "예정된 매치가 없습니다.",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @Parameter(
            name = "userDetails",
            description = "인증된 유저의 정보 (자동 주입)",
            required = true
    )
    ResponseDto<List<MatchFutureDto>> getMatchFuture(@AuthenticationPrincipal UserDetailsImpl userDetails);

    @Operation(
            summary = "매치 초대 수락하기",
            description = "인증된 유저가 대기 중인 매치 초대를 수락할 수 있습니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "매치 초대를 성공적으로 수락했습니다.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDto.class),
                            examples = @ExampleObject(value = "{ \"status\": \"success\", \"message\": \"매치 초대를 수락했습니다.\" }"))),
            @ApiResponse(responseCode = "404", description = "매치를 찾을 수 없습니다.",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "406", description = "매치가 대기 상태가 아니거나 유저에게 도전된 매치가 아닙니다.",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @Parameter(
            name = "matchId",
            description = "수락할 매치의 ID",
            example = "1",
            required = true
    )
    ResponseDto<Void> acceptMatch(@PathVariable Long matchId, @AuthenticationPrincipal UserDetailsImpl userDetails);

    @Operation(
            summary = "매치 초대 거절하기",
            description = "인증된 유저가 대기 중인 매치 초대를 거절할 수 있습니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "매치 초대를 성공적으로 거절했습니다.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDto.class),
                            examples = @ExampleObject(value = "{ \"status\": \"success\", \"message\": \"매치 초대를 거절했습니다.\" }"))),
            @ApiResponse(responseCode = "404", description = "매치를 찾을 수 없습니다.",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "406", description = "매치가 대기 상태가 아니거나 유저에게 도전된 매치가 아닙니다.",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @Parameter(
            name = "matchId",
            description = "거절할 매치의 ID",
            example = "1",
            required = true
    )
    ResponseDto<Void> rejectMatch(@PathVariable Long matchId, @AuthenticationPrincipal UserDetailsImpl userDetails);

    @Operation(
            summary = "상대방에게 매치 요청하기",
            description = "인증된 유저가 상대방 유저에게 매치를 요청할 수 있습니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "매치 요청을 성공적으로 보냈습니다.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDto.class),
                            examples = @ExampleObject(value = "{ \"status\": \"success\", \"message\": \"매치 요청을 보냈습니다.\" }"))),
            @ApiResponse(responseCode = "404", description = "상대 유저를 찾을 수 없습니다.",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "406", description = "자신에게 매치를 요청할 수 없거나 유효하지 않은 매치 상태입니다.",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @Parameter(
            name = "opponentId",
            description = "매치 요청을 보낼 상대방의 ID",
            example = "2",
            required = true
    )
    ResponseDto<Void> requestMatch(@PathVariable Long opponentId, @AuthenticationPrincipal UserDetailsImpl userDetails);


    @Operation(
            summary = "요청받은 매치 목록",
            description ="인증된 유저가 요청받은 매치 목록(PENDING)을 조회할 수 있습니다. 만약 매치가 없으면(요청받은 적이 없다면) 빈 리스트를 반환합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "요청 받은 스파링을 성공적으로 반환하였습니다. ChallengedTo 가 현재 로그인 되어있는 userId",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDto.class),
                            examples = @ExampleObject(value = """
                {
                    "status": "success",
                    "message": "PENDING 상태의 매치를 조회했습니다.",
                    "data": [
                        {
                            "matchId": 1,
                            "challengedBy": 2,
                            "challengedTo": 1,
                            "status": "PENDING"
                        },
                        {
                            "matchId": 2,
                            "challengedBy": 3,
                            "challengedTo": 1,
                            "status": "PENDING"
                        }
                    ]
                }
              """))),
    })

    @Parameter(
            name = "userDetails",
            description = "인증된 유저의 정보 (자동 주입)",
            required = true
    )
    ResponseDto<List<MatchPendingDto>> getPendingMatches(@AuthenticationPrincipal UserDetailsImpl userDetails);
}