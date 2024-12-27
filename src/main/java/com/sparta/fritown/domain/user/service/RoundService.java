package com.sparta.fritown.domain.user.service;


import com.sparta.fritown.domain.user.dto.RoundDto;
import com.sparta.fritown.domain.user.entity.Round;
import com.sparta.fritown.domain.user.repository.RoundRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class RoundService {

    private final RoundRepository roundRepository;

    public List<RoundDto> getRoundsByMatchId(Long matchId)
    {
        List<Round> rounds = roundRepository.findByUserMatchId(matchId);

        return rounds.stream()
                .map(round -> new RoundDto(
                        round.getRoundNum(),
                        round.getKcal(),
                        round.getHeartBeat()
                ))
                .collect(Collectors.toList());
    }

}
