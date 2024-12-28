package com.sparta.fritown.domain.service;

import com.sparta.fritown.domain.dto.rounds.RoundsDto;
import com.sparta.fritown.domain.entity.Matches;
import com.sparta.fritown.domain.entity.Round;
import com.sparta.fritown.domain.entity.User;
import com.sparta.fritown.domain.entity.UserMatch;
import com.sparta.fritown.domain.repository.MatchesRepository;
import com.sparta.fritown.domain.repository.RoundRepository;
import com.sparta.fritown.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MatchService {

    private final RoundRepository roundRepository;
    private final MatchesRepository matchesRepository;
    private final UserRepository userRepository;

    public List<RoundsDto> getRoundsByMatchId(Long matchId, Long userId) {
        Matches match = matchesRepository.findById(matchId).orElseThrow(() -> new NoSuchElementException("Match with id " + matchId + " not found"));
        List<UserMatch> userMatches = match.getUserMatches();

        User me = userRepository.findById(userId).orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));

        List<Round> rounds = null;
        for (UserMatch userMatch : userMatches) {
            if (userMatch.getUser() == me) {
                rounds = userMatch.getRounds();
                break;
            }
        }
        if(rounds == null)
        {
            throw new NoSuchElementException("No matching UserMatch for user with id" + userId);
        }


        return rounds.stream()
                .map(round -> new RoundsDto(
                        round.getRoundNum(),
                        round.getKcal(),
                        round.getHeartBeat()
                ))
                .collect(Collectors.toList());
    }
}

