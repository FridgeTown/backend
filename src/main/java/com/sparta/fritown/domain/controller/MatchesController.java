package com.sparta.fritown.domain.user.controller;

import com.sparta.fritown.domain.user.entity.Matches;
import com.sparta.fritown.domain.user.service.MatchesService;
import com.sparta.fritown.global.exception.dto.ResponseDto;
import com.sparta.fritown.global.security.dto.StatusResponseDto;
import com.sparta.fritown.global.security.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class MatchesController {
    private final MatchesService matchesService;
    private final JwtUtil jwtUtil;

    @PostMapping("/match/accept/{matchId}")
    public ResponseEntity<StatusResponseDto> acceptMatch(@PathVariable Long matchId, @RequestHeader("Authorization") final String accessToken) {
        Matches match = matchesService.matchAccept(matchId, jwtUtil.getUid(accessToken));
        return ResponseEntity.ok(StatusResponseDto.success(ResponseDto.success("매치 수락", match))); // ResponseDto 가 머임?
    }

    @PostMapping("/match/reject/{matchId}")
    public ResponseEntity<StatusResponseDto> rejectMatch(@PathVariable Long matchId, @RequestHeader("Authorization") final String accessToken) {
        Matches match = matchesService.matchReject(matchId, jwtUtil.getUid(accessToken));
        return ResponseEntity.ok(StatusResponseDto.success(ResponseDto.success("매치 거절", match))); // ResponseDto 가 머임?
    }
}
