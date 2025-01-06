package com.sparta.fritown.domain.service;

import com.sparta.fritown.domain.entity.Matches;
import com.sparta.fritown.domain.repository.MatchesRepository;
import com.sparta.fritown.global.exception.ErrorCode;
import com.sparta.fritown.global.exception.custom.ServiceException;
import org.springframework.stereotype.Service;

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
    }

    public void liveWatchEnd(Long matchId) {
        Matches matches = matchesRepository.findById(matchId).orElseThrow(() -> ServiceException.of(ErrorCode.MATCH_NOT_FOUND));
        matches.decrementViewNum();
    }

    public void getLiveList() {
    }
}
