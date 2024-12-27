package com.sparta.fritown.domain.service;

import com.sparta.fritown.domain.dto.RoundsDto;
import com.sparta.fritown.domain.entity.Round;
import com.sparta.fritown.domain.repository.RoundRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MatchService {

    private final RoundRepository roundRepository;

    public List<RoundsDto> getRoundsByMatchId(Long matchId)
    {
        List<Round> rounds = roundRepository.findByUserMatchId(matchId);

        return rounds.stream()
                .map(round -> new RoundsDto(
                        round.getRoundNum(),
                        round.getKcal(),
                        round.getHeartBeat()
                ))
                .collect(Collectors.toList());
    }
}
