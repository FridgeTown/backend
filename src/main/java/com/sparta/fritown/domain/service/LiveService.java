package com.sparta.fritown.domain.service;

import com.sparta.fritown.domain.dto.live.LiveResponseDto;
import com.sparta.fritown.domain.entity.Matches;
import com.sparta.fritown.domain.entity.enums.Status;
import com.sparta.fritown.domain.repository.MatchesRepository;
import lombok.extern.slf4j.Slf4j;
import com.sparta.fritown.domain.entity.Matches;
import com.sparta.fritown.domain.repository.MatchesRepository;
import com.sparta.fritown.global.exception.ErrorCode;
import com.sparta.fritown.global.exception.custom.ServiceException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class LiveService {

    private final MatchesRepository matchesRepository;

    public LiveService(MatchesRepository matchesRepository) {
        this.matchesRepository = matchesRepository;
    }

    public void liveStart() {
    }

    public void liveEnd() {
    }

    public void liveWatchStart(Long matchId) {

        Matches matches = matchesRepository.findById(matchId).orElseThrow(() -> ServiceException.of(ErrorCode.MATCH_NOT_FOUND));
        matches.incrementViewNum();

        // 변경 사항 저장
        matchesRepository.save(matches);
    }

    public void liveWatchEnd(Long matchId) {
        Matches matches = matchesRepository.findById(matchId).orElseThrow(() -> ServiceException.of(ErrorCode.MATCH_NOT_FOUND));
        matches.decrementViewNum();

        // 변경 사항 저장
        matchesRepository.save(matches);

    }

    public List<LiveResponseDto> getLiveList() {
        List<Matches> matches = matchesRepository.findByStatus(Status.PROGRESS);
        List<LiveResponseDto> liveResponseDtos = new ArrayList<>();
        for (Matches matche : matches) {
            LiveResponseDto liveResponseDto = new LiveResponseDto(matche);
            liveResponseDtos.add(liveResponseDto);
        }

        return liveResponseDtos;
    }
}
