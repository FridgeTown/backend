package com.sparta.fritown.domain.service;

import com.sparta.fritown.domain.dto.live.LiveResponseDto;
import com.sparta.fritown.domain.entity.Matches;
import com.sparta.fritown.domain.entity.enums.Status;
import com.sparta.fritown.domain.repository.MatchesRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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

    public void liveWatchStart() {
    }

    public void liveWatchEnd() {
    }

    public List<LiveResponseDto> getLiveList() {
        List<Matches> matches = matchesRepository.findByStatus(Status.PROGRESS);
        List<LiveResponseDto> liveResponseDtos = new ArrayList<>();
        for (Matches matche : matches) {
            LiveResponseDto liveResponseDto = new LiveResponseDto(matche);
        }

        return liveResponseDtos;
    }
}
