package com.sparta.fritown.domain.controller;

import com.sparta.fritown.domain.dto.match.MatchFutureDto;
import com.sparta.fritown.domain.dto.match.MatchSummaryDto;
import com.sparta.fritown.domain.dto.rounds.RoundsDto;
import com.sparta.fritown.domain.entity.Matches;
import com.sparta.fritown.domain.service.MatchService;
import com.sparta.fritown.global.docs.MatchControllerDocs;
import com.sparta.fritown.global.exception.SuccessCode;
import com.sparta.fritown.global.exception.dto.ResponseDto;
import com.sparta.fritown.global.security.dto.StatusResponseDto;
import com.sparta.fritown.global.security.dto.UserDetailsImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/match")
public class MatchController implements MatchControllerDocs {

    private final MatchService matchService;

    public MatchController(MatchService matchService) {
        this.matchService = matchService;
    }

    @Override
    @GetMapping("/{matchId}/round")
    public ResponseDto<List<RoundsDto>> getRoundsByMatchId(@PathVariable Long matchId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        List<RoundsDto> rounds = matchService.getRoundsByMatchId(matchId, userDetails.getId());
        return ResponseDto.success(SuccessCode.OK, rounds);
    }

    @Override
    @GetMapping("/history")
    public ResponseDto<List<MatchSummaryDto>> getMatchHistory(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        List<MatchSummaryDto> matchSummaryDtos = matchService.getMatchHistory(userDetails.getId());
        return ResponseDto.success(SuccessCode.MATCHED_USERS, matchSummaryDtos);
    }

    @Override
    @GetMapping("/future")
    public ResponseDto<List<MatchFutureDto>> getMatchFuture(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        List<MatchFutureDto> matchFutureDtos = matchService.getMatchFuture(userDetails.getId());
        return ResponseDto.success(SuccessCode.MATCHING_USERS, matchFutureDtos);
    }

    @Override
    @PostMapping("/{opponentId}")
    public ResponseDto<Void> requestMatch(@PathVariable Long opponentId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        matchService.requestMatch(opponentId, userDetails.getId());
        return ResponseDto.success(SuccessCode.MATCH_REQUEST);
    }


    @Override
    @PostMapping("/accept/{matchId}")
    public ResponseDto<Void> acceptMatch(@PathVariable Long matchId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        matchService.matchAccept(matchId, userDetails.getEmail());
        return ResponseDto.success(SuccessCode.MATCHING_ACCEPT);
    }

    @Override
    @PostMapping("/reject/{matchId}")
    public ResponseDto<Void> rejectMatch(@PathVariable Long matchId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        matchService.matchReject(matchId, userDetails.getEmail());
        return ResponseDto.success(SuccessCode.MATCHING_REJECT);
    }
}
