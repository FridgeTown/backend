package com.sparta.fritown.global.docs;

import com.sparta.fritown.domain.dto.user.OpponentDto;
import com.sparta.fritown.global.exception.dto.ResponseDto;
import com.sparta.fritown.global.security.dto.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
            @ApiResponse(responseCode = "200", description = "로그인 성공")
    })
    String loginSuccess(@RequestParam("accessToken") String accessToken);

    @Operation(
            summary = "로그인 실패 페이지",
            description = "로그인 실패 시 호출되는 페이지입니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인 실패 페이지 반환")
    })
    String failureHealthCheck();

    @Operation(
            summary = "OAuth 인증 실패 페이지",
            description = "OAuth 인증 실패 시 호출되는 페이지입니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OAuth 인증 실패 페이지 반환")
    })
    String errorHealthCheck();

    @Operation(
            summary = "추천 사용자 조회",
            description = "현재 로그인한 사용자를 제외한 랜덤 추천 사용자 리스트를 반환합니다. 만약 사용자가 아예 없으면 빈 리스트를 반환합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "추천 사용자 리스트 반환 성공"),
            @ApiResponse(responseCode = "404", description = "추천할 사용자를 찾을 수 없음")
    })
    ResponseDto<List<OpponentDto>> getRecommendedOpponents(UserDetailsImpl userDetails);

    @Operation(
            summary = "프로필 이미지 업데이트",
            description = "현재 로그인한 유저의 프로필 이미지를 업데이트합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "프로필 이미지 업데이트 성공"),
            @ApiResponse(responseCode = "404", description = "유저를 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "이미지 업로드 실패")
    })
    ResponseDto<Void> updateProfileImg(UserDetailsImpl userDetails, MultipartFile file);
}