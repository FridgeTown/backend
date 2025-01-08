package com.sparta.fritown.domain.controller;

import com.sparta.fritown.domain.dto.vote.VoteRequestDto;
import com.sparta.fritown.domain.dto.vote.VoteResponseDto;
import com.sparta.fritown.domain.service.VotingService;
//import com.sparta.fritown.global.docs.VotingControllerDocs;
import com.sparta.fritown.global.exception.SuccessCode;
import com.sparta.fritown.global.exception.dto.ResponseDto;
import com.sparta.fritown.global.security.dto.UserDetailsImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Slf4j
@RestController
@RequestMapping("/voting")
public class VotingController {
    private final VotingService votingService;

    public VotingController(VotingService votingService) {
        this.votingService = votingService;
    }

    @GetMapping("/subscribe/{matchId}")
    public SseEmitter subscribe(@PathVariable Long matchId, @AuthenticationPrincipal UserDetailsImpl userDetails)
    {
        Long userId = userDetails.getId();

        // 유저 ID와 matchId 로 SSE 연결 생성
        return votingService.subscribe(matchId,userId);
    }

    @GetMapping("/unsubscribe/{matchId}")
    public ResponseDto<Void> unsubscribe(@PathVariable Long matchId, @AuthenticationPrincipal UserDetailsImpl userDetails)
    {
        // 인증된 유저 ID 가져오기
        Long userId = userDetails.getId();
        log.info("unsubscribe 요청: matchId = {}, userId = {}",matchId,userId);

        // VotingService 에서 구독 해지 처리
        votingService.unsubscribe(matchId,userId);

        return ResponseDto.success(SuccessCode.UNSUBSCRIBE_SUCCESS);
    }

    @PostMapping("/vote")
    public ResponseDto<VoteResponseDto> vote(@RequestBody VoteRequestDto voteRequestDto)
    {
        // DTO 에서 값 추출
        Long matchId = voteRequestDto.getMatchId();
        Long userId = voteRequestDto.getMatchId();

        // 투표 수행
        votingService.voteForUser(matchId,userId);

        // 응답 DTO 생성
        VoteResponseDto voteResponseDto = new VoteResponseDto(matchId,userId);

        return ResponseDto.success(SuccessCode.VOTE_SUCCESS,voteResponseDto);
    }

}
