package com.sparta.fritown.global.docs;

import com.sparta.fritown.domain.dto.user.BioUpdateRequestDto;
import com.sparta.fritown.domain.dto.user.OpponentDto;
import com.sparta.fritown.domain.dto.user.UserInfoResponseDto;
import com.sparta.fritown.domain.dto.user.WeightUpdateRequestDto;
import com.sparta.fritown.global.exception.dto.ResponseDto;
import com.sparta.fritown.global.security.dto.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "User", description = "유저 관련 API")
public interface UserControllerDocs {

    @Operation(
            summary = "로그인 성공 페이지",
            description = "로그인 성공 시, AccessToken을 반환합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인 성공", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(type = "string", example = "Your AccessToken: <access_token>")
            ))
    })
    String loginSuccess(@RequestParam("accessToken") String accessToken);

    @Operation(
            summary = "로그인 실패 페이지",
            description = "로그인 실패 시 호출되는 페이지입니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인 실패 페이지 반환", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(type = "string", example = "MyAuthentication Failed; sign up page should be shown")
            ))
    })
    String failureHealthCheck();

    @Operation(
            summary = "OAuth 인증 실패 페이지",
            description = "OAuth 인증 실패 시 호출되는 페이지입니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OAuth 인증 실패 페이지 반환", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(type = "string", example = "OAuth just failed")
            ))
    })
    String errorHealthCheck();

    @Operation(
            summary = "추천 사용자 조회",
            description = "현재 로그인한 사용자를 제외한 랜덤 추천 사용자 리스트를 반환합니다. 만약 사용자가 아예 없으면 빈 리스트를 반환합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "추천 사용자 리스트 반환 성공", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = OpponentDto.class),
                    examples = @ExampleObject(value = "[{ \"id\": 1, \"nickname\": \"JohnDoe\", \"height\": 180, \"weight\": 75, \"bio\": \"I love sparring!\", \"gender\": \"MALE\", \"profileImage\": \"https://example.com/profile.jpg\" }]")
            )),
            @ApiResponse(responseCode = "404", description = "추천할 사용자를 찾을 수 없음", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(type = "string", example = "추천할 사용자를 찾을 수 없습니다.")
            ))
    })
    ResponseDto<List<OpponentDto>> getRecommendedOpponents(UserDetailsImpl userDetails);

    @Operation(
            summary = "프로필 이미지 업데이트",
            description = "현재 로그인한 유저의 프로필 이미지를 업데이트합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "프로필 이미지 업데이트 성공", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(type = "string", example = "프로필 이미지가 성공적으로 업데이트되었습니다.")
            )),
            @ApiResponse(responseCode = "404", description = "유저를 찾을 수 없음", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(type = "string", example = "유저를 찾을 수 없습니다.")
            )),
            @ApiResponse(responseCode = "500", description = "이미지 업로드 실패", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(type = "string", example = "이미지 업로드에 실패했습니다.")
            ))
    })
    ResponseDto<Void> updateProfileImg(
            @RequestBody(description = "이미지 파일", required = true, content = @Content(
                    mediaType = "multipart/form-data",
                    schema = @Schema(type = "string", format = "binary")
            )) UserDetailsImpl userDetails,
            @RequestParam("file") MultipartFile file
    );

    @Operation(
            summary = "유저 정보 조회",
            description = "현재 로그인한 사용자의 정보를 조회합니다. 이 API는 인증이 필요하며, Authorization 헤더에 AccessToken을 포함해야 합니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "유저 정보 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserInfoResponseDto.class),
                            examples = @ExampleObject(value = """
                                {
                                    "status": "success",
                                    "data": {
                                        "id": 1,
                                        "email": "user@example.com",
                                        "provider": "google",
                                        "profileImg": "https://example.com/profile.jpg",
                                        "gender": "FEMALE",
                                        "age": 25,
                                        "weight": 55,
                                        "height": 165,
                                        "bio": "I am passionate about fitness and coding.",
                                        "points": 1200,
                                        "heartBeat": 75,
                                        "punchSpeed": 15,
                                        "kcal": 300,
                                        "weightClass": "LIGHTWEIGHT",
                                        "role": "USER",
                                        "nickname": "FitCoder",
                                        "chatToken": "<chat_token>"
                                    }
                                }
                                """)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "사용자를 찾을 수 없음",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(type = "string", example = """
                                {
                                    "status": "error",
                                    "message": "사용자를 찾을 수 없습니다.",
                                    "code": "USER_NOT_FOUND"
                                }
                                """)


                    )
            )
    })
    public ResponseDto<UserInfoResponseDto> getUserInfo(@AuthenticationPrincipal UserDetailsImpl userDetails);

    @Operation(
            summary = "유저 정보 조회",
            description = "userId를 Path Parameter로 받아 유저 정보를 조회합니다."
    )

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "유저 정보 조회 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserInfoResponseDto.class),
                            examples = @ExampleObject(value = """
                                {
                                    "status": "success",
                                    "data": {
                                        "id": 1,
                                        "email": "user@example.com",
                                        "provider": "google",
                                        "profileImg": "https://example.com/profile.jpg",
                                        "gender": "FEMALE",
                                        "age": 25,
                                        "weight": 55,
                                        "height": 165,
                                        "bio": "I am passionate about fitness and coding.",
                                        "points": 1200,
                                        "heartBeat": 75,
                                        "punchSpeed": 15,
                                        "kcal": 300,
                                        "weightClass": "LIGHTWEIGHT",
                                        "role": "USER",
                                        "nickname": "FitCoder",
                                        "chatToken": "<chat_token>"
                                    }
                                }
                                """)
                    )),
            @ApiResponse(responseCode = "404", description = "유저를 찾을 수 없음",
                    content = @Content(mediaType = "application/json"))
    })
    public ResponseDto<UserInfoResponseDto> getUserInfoByUserId(@PathVariable Long userId);


    @Operation(
            summary = "회원 탈퇴",
            description = "현재 로그인한 사용자가 자신의 계정을 탈퇴합니다. 탈퇴한 계정은 복구할 수 없습니다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "회원 탈퇴 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDto.class),
                            examples = @ExampleObject(value = """
                {
                    "status": "success",
                    "message": "User successfully deleted",
                    "data": null
                }
                """)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "사용자를 찾을 수 없음",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(type = "string"),
                            examples = @ExampleObject(value = """
                {
                    "status": "error",
                    "message": "유저를 찾지 못했습니다.",
                    "code": "USER_NOT_FOUND"
                }
                """)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 에러",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(type = "string"),
                            examples = @ExampleObject(value = """
                {
                    "status": "error",
                    "message": "Internal Server Error",
                    "code": "SERVER_ERROR"
                }
                """)
                    )
            )
    })
    public ResponseDto<Void> resignateUser(@AuthenticationPrincipal UserDetailsImpl userDetails);

    @Operation(
            summary = "유저 Bio 업데이트",
            description = "현재 로그인한 유저의 Bio를 업데이트합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "유저 Bio 업데이트 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserInfoResponseDto.class),
                            examples = @ExampleObject(value = """
                {
                    "status": "success",
                    "code": "U002",
                    "message": "성공적으로 유저 Bio가 수정되었습니다.",
                    "data": {
                        "id": 1,
                        "email": "user@example.com",
                        "nickname": "FitCoder",
                        "bio": "I love coding and sparring!"
                    }
                }
                """)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "유저를 찾을 수 없음",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(type = "string"),
                            examples = @ExampleObject(value = """
                {
                    "status": "error",
                    "code": "USER_NOT_FOUND",
                    "message": "유저를 찾지 못했습니다."
                }
                """)
                    )
            )
    })
    @PatchMapping("/user/bio")
    ResponseDto<UserInfoResponseDto> updateBio(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "유저 Bio 업데이트 요청",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = BioUpdateRequestDto.class)
                    )
            ) BioUpdateRequestDto bioUpdateRequestDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails);

    @Operation(
            summary = "유저 몸무게 업데이트",
            description = "현재 로그인한 유저의 몸무게를 업데이트합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "유저 몸무게 업데이트 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserInfoResponseDto.class),
                            examples = @ExampleObject(value = """
                {
                    "status": "success",
                    "code": "U003",
                    "message": "성공적으로 유저 몸무게가 수정되었습니다.",
                    "data": {
                        "id": 1,
                        "email": "user@example.com",
                        "nickname": "FitCoder",
                        "weight": 75
                    }
                }
                """)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "유저를 찾을 수 없음",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(type = "string"),
                            examples = @ExampleObject(value = """
                {
                    "status": "error",
                    "code": "USER_NOT_FOUND",
                    "message": "유저를 찾지 못했습니다."
                }
                """)
                    )
            )
    })
    @PatchMapping("/user/weight")
    ResponseDto<UserInfoResponseDto> updateWeight(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "유저 몸무게 업데이트 요청",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = WeightUpdateRequestDto.class)
                    )
            ) WeightUpdateRequestDto weightUpdateRequestDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails);

}