package com.sparta.fritown.global.docs;

import com.sparta.fritown.domain.dto.user.OpponentDto;
import com.sparta.fritown.global.exception.dto.ResponseDto;
import com.sparta.fritown.global.security.dto.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.util.List;

@Tag(name = "User API", description =  "APIs for user-related operations")
public interface UserControllerDocs {
    @Operation(summary = "추천 사용자 조회",
            description = "현재 로그인한 사용자를 제외한 랜덤 추천 사용자 리스트를 반환합니다. 만약 사용자가 아예 없으면 빈 리스트를 반환")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "추천 사용자 리스트 반환 성공"),
    })
    ResponseDto<List<OpponentDto>> getRecommendedOpponents(@AuthenticationPrincipal UserDetailsImpl userDetails);
}
