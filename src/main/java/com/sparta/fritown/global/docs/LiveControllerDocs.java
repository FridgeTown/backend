package com.sparta.fritown.global.docs;

import com.sparta.fritown.domain.dto.live.LiveResponseDto;
import com.sparta.fritown.domain.dto.live.LiveStartRequestDto;
import com.sparta.fritown.global.exception.dto.ResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name="Live", description = "실시간 방송 관련 API")
public interface LiveControllerDocs {

    @Operation(summary = "방송 시작", description = "매치 ID와 장소를 받아 방송을 시작합니다. match 상태를 Progress 로 변경")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "매치 상태를 PROGRESS 로 성공적으로 설정하였습니다.",content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResponseDto.class),
                    examples = @ExampleObject(
                        name = "성공 응답 예시",
                            value = """
                        {
                            "status": 200,
                            "message": "매치 상태를 PROGRESS 로 성공적으로 설정하였습니다.",
                            "data": null
                        }
                        """
                    )
            )),
            @ApiResponse(responseCode = "404",description = "매치를 찾지 못했습니다.",content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(type = "string", example = "매치를 찾지 못했습니다.")
            ))
        }
    )
    ResponseDto<Void> liveStart(
            @RequestBody(description = "방송 시작 요청 데이터", required = true,content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = LiveStartRequestDto.class),
                    examples = @ExampleObject(
                            name = "요청 데이터 예시",
                            value = """
                                    {
                                        "matchId": 1,
                                        "place" : "Main Arena"
                                    }
                                    """
                    )
            )) LiveStartRequestDto liveStartRequestDto
    );

    @Operation(summary = "방송 종료", description = "매치 ID를 받아 매치 상태를 DONE 으로 변경합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "매치 상태를 DONE 으로 성공적으로 설정하였습니다.", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResponseDto.class),
                    examples = @ExampleObject(
                            name = "성공 응답 예시",
                            value = """
                        {
                            "status": 200,
                            "message": "매치 상태를 DONE으로 성공적으로 설정하였습니다.",
                            "data": null
                        }
                        """
                    )
            )),
            @ApiResponse(responseCode = "404", description = "매치를 찾을 수 없음", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(type = "string", example = "매치를 찾지 못했습니다.")
            ))
    })
    ResponseDto<Void> liveEnd(@PathVariable Long matchId);


    @Operation(summary = "시청 시작", description = "매치 ID를 받아 해당 매치의 viewNum을 1 증가시킵니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "매치의 viewNum을 성공적으로 1 증가시켰습니다.", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResponseDto.class),
                    examples = @ExampleObject(
                            name = "성공 응답 예시",
                            value = """
                        {
                            "status": 200,
                            "message": "매치의 viewNum을 성공적으로 1 증가시켰습니다.",
                            "data": null
                        }
                        """
                    )
            )),
            @ApiResponse(responseCode = "404", description = "매치를 찾을 수 없음", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(type = "string", example = "매치를 찾지 못했습니다.")
            ))
    })
    public ResponseDto<Void> liveWatchStart(@PathVariable Long matchId);

    @Operation(summary = "시청 종료", description = "매치 ID를 받아 해당 매치의 viewNum을 1 감소시킵니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "매치의 viewNum을 성공적으로 1 감소시켰습니다.", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResponseDto.class),
                    examples = @ExampleObject(
                            name = "성공 응답 예시",
                            value = """
                        {
                            "status": 200,
                            "message": "매치의 viewNum을 성공적으로 1 감소시켰습니다.",
                            "data": null
                        }
                        """
                    )
            )),
            @ApiResponse(responseCode = "404", description = "매치를 찾을 수 없음", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(type = "string", example = "매치를 찾지 못했습니다.")
            )),
            @ApiResponse(responseCode = "400", description = "viewNum이 음수가 될 수 없음", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(type = "string", example = "매치의 viewNum이 0보다 작을 수 없습니다.")
            ))
    })
    ResponseDto<Void> liveWatchEnd(@PathVariable Long matchId);

    @Operation(
            summary = "방송 목록 조회",
            description = "현재 방송 중인 매치 목록을 반환합니다. Match의 상태가 PROGRESS인 것을 반환합니다. 매치가 없을 경우 빈 리스트를 반환합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "방송 목록 조회 성공 (매치가 없으면 빈 리스트 반환)",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDto.class),
                            examples = @ExampleObject(
                                    name = "성공 응답 예시 (빈 리스트 포함)",
                                    value = """
                                    {
                                        "status": 200,
                                        "message": "실시간 방송중인 리스트를 성공적으로 반환하였습니다.",
                                        "data": [
                                              {
                                                 "matchId": 1,
                                                 "title": "Boxing Championship 2025",
                                                 "thumbNail": "https://example.com/thumbnail1.jpg",
                                                 "place": "Madison Square Garden"
                                              },
                                              {
                                                 "matchId": 2,
                                                 "title": "MMA World Cup",
                                                 "thumbNail": "https://example.com/thumbnail2.jpg",
                                                 "place": "Staples Center"
                                              }
                                           ]
                                        }
                                    """
                            )
                    )
            )
    })
    ResponseDto<List<LiveResponseDto>> getLiveList();

    @Operation(
            summary = "썸네일 업로드",
            description = "매치 ID와 이미지 파일을 받아 매치의 썸네일을 설정합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "썸네일 업로드 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDto.class),
                            examples = @ExampleObject(
                                    name = "성공 응답 예시",
                                    value = """
                                {
                                    "status": 200,
                                    "message": "이미지를 성공적으로 업로드 하였습니다.",
                                    "data": null
                                }
                                """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "매치를 찾을 수 없음",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(type = "string", example = """
                        {
                            "status": "error",
                            "code": "M001",
                            "message": "매치를 찾지 못했습니다."
                        }
                        """)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "이미지 업로드 실패",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(type = "string", example = """
                        {
                            "status": "error",
                            "code": "I001",
                            "message": "이미지 업로드에 실패하였습니다."
                        }
                        """)
                    )
            )
    })

    ResponseDto<Void> setThumbNail(
            @Parameter(
                    description = "썸네일을 설정할 매치의 ID",
                    required = true,
                    example = "1"
            )
            @PathVariable Long matchId,

            @Parameter(
                    description = "업로드할 이미지 파일",
                    required = true,
                    content = @Content(mediaType = "multipart/form-data")
            )
            @RequestParam("file") MultipartFile file
    );

}
