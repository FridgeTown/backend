//package com.sparta.fritown.global.docs;
//
//import com.sparta.fritown.domain.dto.vote.VoteRequestDto;
//import com.sparta.fritown.domain.dto.vote.VoteResponseDto;
//import com.sparta.fritown.global.exception.dto.ResponseDto;
//import com.sparta.fritown.global.security.dto.UserDetailsImpl;
//import io.swagger.v3.oas.annotations.Operation;
//import io.swagger.v3.oas.annotations.Parameter;
//import io.swagger.v3.oas.annotations.media.Content;
//import io.swagger.v3.oas.annotations.media.ExampleObject;
//import io.swagger.v3.oas.annotations.media.Schema;
//import io.swagger.v3.oas.annotations.responses.ApiResponse;
//import io.swagger.v3.oas.annotations.responses.ApiResponses;
//import io.swagger.v3.oas.annotations.tags.Tag;
//import org.springframework.security.core.annotation.AuthenticationPrincipal;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
//
//@Tag(name = "Voting", description = "투표 관련 API")
//public interface VotingControllerDocs {
//
//    @Operation(summary = "SSE 구독", description = "특정 matchId와 인증된 사용자 ID로 SSE 구독을 시작합니다.")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "특정 matchId에 구독을 함으로써 투표 결과를 받을 수  있습니다.", content = @Content(
//                    mediaType = "text/event-stream",
//                    schema = @Schema(type = "string", example = "SSE 연결 성공! matchId: 1")
//            )),
//            @ApiResponse(responseCode = "404", description = "매치를 찾지 못했습니다.", content = @Content(
//                    mediaType = "application/json",
//                    schema = @Schema(type = "string", example = "매치를 찾지 못했습니다.")
//            ))
//    })
//    SseEmitter subscribe(@PathVariable Long matchId, @AuthenticationPrincipal UserDetailsImpl userDetails);
//
//    @Operation(summary = "SSE 구독 해지", description = "특정 matchId와 인증된 사용자 ID로 SSE 구독을 해지합니다.")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "구독 해지 성공", content = @Content(
//                    mediaType = "application/json",
//                    schema = @Schema(implementation = ResponseDto.class),
//                    examples = @ExampleObject(value = """
//                        {
//                            "status": 200,
//                            "message": "구독이 성공적으로 해지되었습니다.",
//                            "data": null
//                        }
//                        """)
//            )),
//            @ApiResponse(responseCode = "404", description = "매치를 찾지 못했습니다.", content = @Content(
//                    mediaType = "application/json",
//                    schema = @Schema(type = "string", example = "매치를 찾지 못했습니다.")
//            ))
//    })
//    ResponseDto<Void> unsubscribe(@PathVariable Long matchId, @AuthenticationPrincipal UserDetailsImpl userDetails);
//
//
//    @Operation(summary = "투표", description = "특정 matchId의 userId에게 투표합니다.")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "투표 성공", content = @Content(
//                    mediaType = "application/json",
//                    schema = @Schema(implementation = VoteResponseDto.class),
//                    examples = @ExampleObject(value = """
//                        {
//                            "status": 200,
//                            "message": "투표가 성공적으로 완료되었습니다.",
//                            "data": {
//                                "matchId": 1,
//                                "userId": 42
//                            }
//                        }
//                        """)
//            )),
//            @ApiResponse(responseCode = "400", description = "이미 투표한 사용자입니다.", content = @Content(
//                    mediaType = "application/json",
//                    schema = @Schema(type = "string", example = "이미 투표한 사용자입니다.")
//            )),
//            @ApiResponse(responseCode = "404", description = "매치를 찾지 못했습니다.", content = @Content(
//                    mediaType = "application/json",
//                    schema = @Schema(type = "string", example = "매치를 찾지 못했습니다.")
//            ))
//    })
//    ResponseDto<VoteResponseDto> vote(
//            @RequestBody(description = "투표 요청 데이터", required = true, content = @Content(
//                    mediaType = "application/json",
//                    schema = @Schema(implementation = VoteRequestDto.class),
//                    examples = @ExampleObject(value = """
//                        {
//                            "matchId": 1,
//                            "userId": 42
//                        }
//                        """)
//            )) VoteRequestDto voteRequestDto
//    );
//}
