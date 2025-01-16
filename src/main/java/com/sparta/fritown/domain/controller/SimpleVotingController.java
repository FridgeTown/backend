package com.sparta.fritown.domain.controller;

import com.sparta.fritown.domain.dto.vote.SimpleVoteRequestDto;
import com.sparta.fritown.domain.dto.vote.SimpleVoteResponseDto;
import com.sparta.fritown.domain.service.SimpleVotingService;
import com.sparta.fritown.global.exception.SuccessCode;
import com.sparta.fritown.global.exception.dto.ResponseDto;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/simple")
public class SimpleVotingController {
    private final SimpleVotingService simpleVotingService;
    public SimpleVotingController(SimpleVotingService simpleVotingService) {
        this.simpleVotingService = simpleVotingService;
    }

    @PostMapping("/vote")
    public void simpleVoting(@RequestBody SimpleVoteRequestDto simpleVoteRequestDto) {
        simpleVotingService.simpleVoting(simpleVoteRequestDto);
    }

    @GetMapping("/vote/{channelId}")
    public ResponseDto<SimpleVoteResponseDto> getVoteResult(@PathVariable String channelId) {
        SimpleVoteResponseDto simpleVoteResponseDto = simpleVotingService.getVoteResult(channelId);
        return ResponseDto.success(SuccessCode.VOTE_RESULT, simpleVoteResponseDto);
    }
}
