package com.sparta.fritown.domain.service;

import com.sparta.fritown.domain.dto.vote.SimpleVoteRequestDto;
import com.sparta.fritown.domain.dto.vote.SimpleVoteResponseDto;
import com.sparta.fritown.domain.entity.Vote;
import com.sparta.fritown.domain.repository.SimpleVotingRepository;
import com.sparta.fritown.global.exception.ErrorCode;
import com.sparta.fritown.global.exception.custom.ServiceException;
import org.springframework.stereotype.Service;

@Service
public class SimpleVotingService {
    private final SimpleVotingRepository simpleVotingRepository;

    public SimpleVotingService(SimpleVotingRepository simpleVotingRepository) {
        this.simpleVotingRepository = simpleVotingRepository;
    }
    public void simpleVoting(SimpleVoteRequestDto simpleVoteRequestDto) {
        Vote vote = simpleVotingRepository.findByChannelId(simpleVoteRequestDto.getChannelId())
                .orElseThrow(() ->ServiceException.of(ErrorCode.CHANNEL_NOT_FOUND));

        vote.voting(simpleVoteRequestDto.getVotes());
        simpleVotingRepository.save(vote);
    }

    public SimpleVoteResponseDto getVoteResult(String channelId) {
        Vote vote = simpleVotingRepository.findByChannelId(channelId)
                .orElseThrow(() ->ServiceException.of(ErrorCode.CHANNEL_NOT_FOUND));

        SimpleVoteResponseDto simpleVoteResponseDto = new SimpleVoteResponseDto(vote.getBlueCnt(), vote.getRedCnt());
        return simpleVoteResponseDto;
    }
}
