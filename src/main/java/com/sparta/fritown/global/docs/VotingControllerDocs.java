package com.sparta.fritown.global.docs;

import com.sparta.fritown.domain.dto.vote.VoteRequestDto;
import com.sparta.fritown.domain.dto.vote.VoteResponseDto;
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
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;


@Tag(name = "Voting", description = "투표 관련 API")
public interface VotingControllerDocs {

    @Operation(
            summary = "SSE 구독",
            description = """ 
                    특정 매치(matchId)에 대해 인증된 사용자가 SSE 연결을 생성합니다.
                    SSE 를 통해 실시간 투표 업데이트를 받을 수 있습니다.
                    클라이언트는 'Authorization: Bearer <JWT_ACCESS_TOKEN>' 헤더를 포함해야 합니다.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "SSE 구독 성공",
                    content = @Content(
                            mediaType = "text/event-stream",
                            schema = @Schema(type = "string", example = "SSE 연결 성공! matchId: 1")
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "매치를 찾지 못했습니다.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(type = "object"),
                            examples = @ExampleObject(value = """
                {
                    "status": 404,
                    "message": "매치를 찾지 못했습니다.",
                    "code": "M001"
                }
                """)
                    )
            )
    })
    SseEmitter subscribe(@PathVariable Long matchId, @AuthenticationPrincipal UserDetailsImpl userDetails);


    @Operation(
            summary = "SSE 구독 해지",
            description = """
                    특정 매치(matchId)와 인증된 사용자 ID로 SSE 구독을 해지합니다.
                    구독을 해지하면 SSE 알림을 안받을 수 있습니다.
                    클라이언트는 'Authorization: Bearer <JWT_ACCESS_TOKEN>' 헤더를 포함해야 합니다.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "구독 해지 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDto.class),
                            examples = @ExampleObject(value = """
                {
                    "status": 200,
                    "message": "구독이 성공적으로 해지되었습니다.",
                    "data": null
                }
                """)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "매치를 찾지 못했습니다.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(type = "object"),
                            examples = @ExampleObject(value = """
                {
                    "status": 404,
                    "message": "매치를 찾지 못했습니다.",
                    "code": "M001"
                }
                """)
                    )
            )
    })
    ResponseDto<Void> unsubscribe(@PathVariable Long matchId, @AuthenticationPrincipal UserDetailsImpl userDetails);


    @Operation(
            summary = "투표",
            description = """ 
                    특정 matchId의 playerNickname(투표 대상 닉네임)에게 투표를 수행합니다. 
                    사용자가 이미 투표한 경우 에러를 반환합니다.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "투표 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = VoteResponseDto.class),
                            examples = @ExampleObject(value = """
            {
                "status": 200,
                "message": "투표가 성공적으로 완료되었습니다.",
                "data": {
                    "matchId": 1,
                    "playerNickname": "player1"
                }
            }
            """)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "이미 투표한 사용자입니다.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(type = "object"),
                            examples = @ExampleObject(value = """
            {
                "status": 400,
                "message": "이미 투표한 사용자입니다.",
                "code": "V001"
            }
            """)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "매치를 찾지 못했습니다.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(type = "object"),
                            examples = @ExampleObject(value = """
            {
                "status": 404,
                "message": "매치를 찾지 못했습니다.",
                "code": "M001"
            }
            """)
                    )
            ),
            @ApiResponse(
                    responseCode = "406",
                    description = "투표하려는 유저는 매치에 참여하고 있지 않습니다.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(type = "object"),
                            examples = @ExampleObject(value= """
                                    {
                                        "status" : 406,
                                        "message": "투표하려는 유저는 이 매치에 참여하고 있지 않습니다.",
                                        "code": "M005"
                                    }
                                    """)
                    )

            )
    })
    ResponseDto<VoteResponseDto> vote(
            @RequestBody(
                    description = "투표 요청 데이터 (matchId와 playerNickname 포함)",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = VoteRequestDto.class),
                            examples = @ExampleObject(value = """
                        {
                            "matchId": 1,
                            "playerNickname": "player1"
                        }
                        """)
                    )
            ) VoteRequestDto voteRequestDto,
            @Parameter(description = "사용자 인증 정보", hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails
    );

    @Operation(
            summary = "게스트 ID 생성",
            description = "UUID를 기반으로 고유한 게스트 ID를 생성하여 반환합니다."

    )
    @ApiResponses(value={
            @ApiResponse(
                    responseCode = "200",
                    description = "게스트 ID 생성 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(type ="object"),
                            examples = @ExampleObject(value= """
                                    {
                                        "status" 200,
                                        "message" "게스트 ID가 성공적으로 생성되었습니다."
                                        "data": "550e8400-e29b-41d4-a716-446655440000"
                                    }
                                    """)
                    )
            )


    })
    ResponseDto<String> generateGuestId();


    @Operation(
            summary = "게스트 SSE 구독",
            description = """
                특정 매치(matchId)에 대해 게스트가 SSE 연결을 생성합니다.
                이 API 를 통해 게스트는 실시간 투표 업데이트를 수신할 수 있습니다.
                """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "SSE 구독 성공",
                    content = @Content(
                            mediaType = "text/event-stream",
                            schema = @Schema(type = "string", example = "SSE 연결 성공! matchId: 1")
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "매치를 찾을 수 없습니다.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(type = "object"),
                            examples = @ExampleObject(value = """
                {
                    "status": 404,
                    "message": "매치를 찾지 못했습니다.",
                    "code": "M001"
                }
                """)
                    )
            )
    })
    SseEmitter guestSubscribe(
            @Parameter(description = "구독하려는 매치 ID",example="1") @PathVariable Long matchId,
            @Parameter(description = "게스트 고유 ID(UUID)", example ="550e8400-e29b-41d4-a716-446655440000")
            @RequestHeader(value ="Guest-Id") String guestId
    );

    @Operation(
            summary = "게스트 SSE 구독 해지",
            description = """
                    특정 매치(matchId)와 게스트 ID를 기반으로 SSE 구독을 해지합니다.
                    구독 해지를 통해 게스트는 더 이상 실시간 투표 업데이트를 받지 않습니다.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "구독 해지 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDto.class),
                            examples = @ExampleObject(value = """
                {
                    "status": 200,
                    "message": "구독이 성공적으로 해지되었습니다.",
                    "data": null
                }
                """)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "매치를 찾지 못했습니다.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(type = "object"),
                            examples = @ExampleObject(value = """
                {
                    "status": 404,
                    "message": "매치를 찾지 못했습니다.",
                    "code": "M001"
                }
                """)
                    )
            )
    })
    ResponseDto<Void> guestUnsubscribe(
            @Parameter(description = "구독 해지하려는 MatchId",example = "1" ) @PathVariable Long matchId,
            @Parameter(description = "게스트의 고유 ID(UUID)", example = "550e8400-e29b-41d4-a716-446655440000") @RequestHeader(value="Guest-Id") String guestId);


    @Operation(
            summary = "게스트 투표",
            description = """
                특정 matchId의 playerNickname(투표 대상 닉네임)에게 게스트가 투표를 수행합니다.
                이미 투표한 경우에는 에러를 반환합니다.
                """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "투표 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = VoteResponseDto.class),
                            examples = @ExampleObject(value = """
            {
                "status": 200,
                "message": "투표가 성공적으로 완료되었습니다.",
                "data": {
                    "matchId": 1,
                    "playerNickname": "player1"
                }
            }
            """)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "이미 투표한 게스트입니다.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(type = "object"),
                            examples = @ExampleObject(value = """
            {
                "status": 400,
                "message": "이미 투표한 게스트입니다.",
                "code": "V001"
            }
            """)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "매치를 찾지 못했습니다.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(type = "object"),
                            examples = @ExampleObject(value = """
            {
                "status": 404,
                "message": "매치를 찾지 못했습니다.",
                "code": "M001"
            }
            """)
                    )
            ),
            @ApiResponse(
                    responseCode = "406",
                    description = "투표하려는 유저는 매치에 참여하고 있지 않습니다.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(type = "object"),
                            examples = @ExampleObject(value = """
            {
                "status": 406,
                "message": "투표하려는 유저는 이 매치에 참여하고 있지 않습니다.",
                "code": "M005"
            }
            """)
                    )
            )
    })
    ResponseDto<VoteResponseDto> guestVote(
            @Parameter(description = "게스트의 고유 ID(UUID)", example = "550e8400-e29b-41d4-a716-446655440000")
            @RequestHeader(value = "Guest-Id") String guestId,
            @RequestBody(
                    description = "투표 요청 데이터 (matchId와 playerNickname 포함)",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = VoteRequestDto.class),
                            examples = @ExampleObject(value = """
                        {
                            "matchId": 1,
                            "playerNickname": "player1"
                        }
                        """)
                    )
            ) VoteRequestDto voteRequestDto
    );


}
