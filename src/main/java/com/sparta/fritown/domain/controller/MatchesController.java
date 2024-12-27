package com.sparta.fritown.domain.controller;

import com.sparta.fritown.domain.entity.Matches;
import com.sparta.fritown.domain.service.MatchesService;
import com.sparta.fritown.global.exception.dto.ResponseDto;
import com.sparta.fritown.global.security.dto.StatusResponseDto;
import com.sparta.fritown.global.security.dto.UserDetailsImpl;
import com.sparta.fritown.global.security.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class MatchesController {
    private final MatchesService matchesService;
    private final JwtUtil jwtUtil;

    @PostMapping("/match/accept/{matchId}")
    public ResponseEntity<StatusResponseDto> acceptMatch(@PathVariable Long matchId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        System.out.println("test: " + userDetails.getEmail());
        Matches match = matchesService.matchAccept(matchId, userDetails.getEmail());
        return ResponseEntity.ok(StatusResponseDto.success(ResponseDto.success("매치 수락", match))); // ResponseDto 가 머임?
    }

    @PostMapping("/match/reject/{matchId}")
    public ResponseEntity<StatusResponseDto> rejectMatch(@PathVariable Long matchId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Matches match = matchesService.matchReject(matchId, userDetails.getEmail());
        return ResponseEntity.ok(StatusResponseDto.success(ResponseDto.success("매치 거절", match))); // ResponseDto 가 머임?
    }
}
