package com.sparta.fritown.domain.controller;

import com.sparta.fritown.domain.dto.match.MatchSummaryDto;
import com.sparta.fritown.domain.service.MatchService;
import com.sparta.fritown.global.docs.MatchControllerDocs;
import com.sparta.fritown.global.exception.SuccessCode;
import com.sparta.fritown.global.exception.dto.ResponseDto;
import com.sparta.fritown.global.security.dto.UserDetailsImpl;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/match")
public class MatchController implements MatchControllerDocs {

    private final MatchService matchService;

    public MatchController(MatchService matchService) {
        this.matchService = matchService;
    }

    @GetMapping("/history")
    public ResponseDto<List<MatchSummaryDto>> getMatchHistory(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        List<MatchSummaryDto> matchSummaryDtos = matchService.getMatchHistory(userDetails.getId());
        return ResponseDto.success(SuccessCode.MATCHED_USERS, matchSummaryDtos);
    }
}
