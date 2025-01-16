package com.sparta.fritown.global.docs;

import com.sparta.fritown.domain.dto.vote.SimpleVoteRequestDto;
import com.sparta.fritown.domain.dto.vote.SimpleVoteResponseDto;
import com.sparta.fritown.global.exception.dto.ResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Tag(name = "Simple Voting", description = "간단한 투표 관련 API")
public interface SimpleVotingControllerDocs {

    @Operation(
            summary = "투표 생성 또는 업데이트",
            description = "특정 채널에 대해 투표를 생성하거나 업데이트합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "투표가 성공적으로 처리되었습니다.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDto.class),
                            examples = @ExampleObject(value = """
                            {
                                "status": "success",
                                "message": "투표가 성공적으로 완료되었습니다."
                            }
                            """))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터입니다.",
                    content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류.",
                    content = @Content(schema = @Schema(implementation = ResponseDto.class)))
    })
    @PostMapping("/vote")
    void simpleVoting(
            @RequestBody(description = "투표 요청 데이터", required = true,
                    content = @Content(schema = @Schema(implementation = SimpleVoteRequestDto.class),
                            examples = @ExampleObject(value = """
                            {
                                "channelId": "1234",
                                "votes": "BLUE"
                            }
                            """))) SimpleVoteRequestDto simpleVoteRequestDto
    );

    @Operation(
            summary = "투표 결과 조회",
            description = "특정 채널의 투표 결과를 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "투표 결과를 성공적으로 반환하였습니다.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = SimpleVoteResponseDto.class),
                            examples = @ExampleObject(value = """
                            {
                                "blueCnt": 10,
                                "redCnt": 5
                            }
                            """))),
            @ApiResponse(responseCode = "404", description = "해당 채널을 찾을 수 없습니다.",
                    content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류.",
                    content = @Content(schema = @Schema(implementation = ResponseDto.class)))
    })
    @GetMapping("/vote/{channelId}")
    ResponseDto<SimpleVoteResponseDto> getVoteResult(
            @PathVariable(name = "channelId"
                    ) String channelId
    );
}