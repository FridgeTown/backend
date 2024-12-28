package com.sparta.fritown.domain.service;

import com.sparta.fritown.domain.dto.match.MatchInfo;
import com.sparta.fritown.domain.dto.match.MatchSummaryDto;
import com.sparta.fritown.domain.entity.Matches;
import com.sparta.fritown.domain.entity.Round;
import com.sparta.fritown.domain.entity.User;
import com.sparta.fritown.domain.entity.UserMatch;
import com.sparta.fritown.domain.entity.enums.Status;
import com.sparta.fritown.domain.repository.MatchesRepository;
import com.sparta.fritown.domain.repository.UserRepository;
import com.sparta.fritown.global.exception.ErrorCode;
import com.sparta.fritown.global.exception.custom.ServiceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MatchService {

    private final UserRepository userRepository;

    public List<MatchSummaryDto> getMatchHistory(Long userId) {

        // 현재 유저 정보 가져오기
        User user = userRepository.findById(userId)
                .orElseThrow(() -> ServiceException.of(ErrorCode.USER_NOT_FOUND));

        LocalDate todayDate = LocalDate.now();

        return user.getUserMatches().stream()
                .filter(userMatch -> isValidMatch(userMatch, todayDate))
                .map(userMatch -> createMatchSummaryDto(userMatch, user))
                .collect(Collectors.toList());
    }

    private boolean isValidMatch(UserMatch userMatch, LocalDate todayDate) {
        Matches matches = userMatch.getMatches();
        return matches.getDate().isBefore(todayDate) && matches.getStatus().equals(Status.DONE);
    }

    // MatchSummaryDto 생성 메서드
    private MatchSummaryDto createMatchSummaryDto(UserMatch userMatch, User currentUser) {
        Matches matches = userMatch.getMatches();
        List<Round> rounds = userMatch.getRounds();

        // 라운드 데이터 계산
        MatchInfo matchInfo = calculateMatchInfo(rounds, matches.getDate());

        // 상대 유저 찾기
        User opponent = getOpponent(matches, currentUser);

        // MatchSummaryDto 생성
        return new MatchSummaryDto(matches.getId(), matchInfo, opponent.getNickname());
    }

    // MatchInfo 계산 로직 분리
    private MatchInfo calculateMatchInfo(List<Round> rounds, LocalDate matchDate) {
        int totalKcal = 0;
        int totalHeartBeat = 0;
        int totalPunchNum = 0;
        int roundNums = rounds.size(); // 이걸로 리스트 길이 확인 가능

        for (Round round : rounds) {
            totalKcal += round.getKcal();
            totalHeartBeat += round.getHeartBeat();
            totalPunchNum += round.getPunchNum();
        }

        int avgHeartBeat = roundNums > 0 ? totalHeartBeat / roundNums : 0;
        return new MatchInfo(totalKcal, avgHeartBeat, totalPunchNum, roundNums, matchDate);
    }

    // 상대 유저 찾기
    private User getOpponent(Matches matches, User currentUser) {
        return matches.getChallengedBy().equals(currentUser)
                ? matches.getChallengedTo()
                : matches.getChallengedBy();
    }
}