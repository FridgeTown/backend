package com.sparta.fritown.domain.user.service;

import com.sparta.fritown.domain.user.dto.RegisterRequestDto;
import com.sparta.fritown.domain.user.entity.Matches;
import com.sparta.fritown.domain.user.entity.User;
import com.sparta.fritown.domain.user.entity.enums.Status;
import com.sparta.fritown.domain.user.repository.MatchesRepository;
import com.sparta.fritown.global.exception.ErrorCode;
import com.sparta.fritown.global.exception.custom.ServiceException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class MatchesServiceTest {

    @Autowired
    MatchesRepository matchesRepository;

    @Autowired
    UserService userService;
    @Autowired
    MatchesService matchesService;

    @Test
    void 존재하지_않는_매치수락() {
        User user = userService.register(new RegisterRequestDto("test1@gmail.com", "google", "test1", "ROLE_USER"));

        ServiceException exception = assertThrows(ServiceException.class, () -> matchesService.matchAccept(0L, user.getId().toString()));
        assertThat(exception.getCode()).isEqualTo(ErrorCode.MATCH_NOT_FOUND.getCode());
    }

    @Test
    void 매치수락() {
        User user1 = userService.register(new RegisterRequestDto("test1@gmail.com", "google", "test1", "ROLE_USER"));
        User user2 = userService.register(new RegisterRequestDto("test2@gmail.com", "google", "test2", "ROLE_USER"));

        Matches match = matchesRepository.save(Matches.builder()
                .challengedBy(user1)
                .challengedTo(user2)
                .date(LocalDate.now())
                .place("place")
                .title("title")
                .build());

        // 매치 신청한 사람
        Matches acceptedMatch = matchesService.matchAccept(match.getId(), user2.getId().toString());
        assertThat(acceptedMatch.getStatus()).isEqualTo(Status.ACCEPTED);
    }

    @Test
    void 대기상태_아닌_매치수락() {
        User user1 = userService.register(new RegisterRequestDto("test1@gmail.com", "google", "test1", "ROLE_USER"));
        User user2 = userService.register(new RegisterRequestDto("test2@gmail.com", "google", "test2", "ROLE_USER"));

        // status == PENDING
        Matches match = matchesRepository.save(Matches.builder()
                .challengedBy(user1)
                .challengedTo(user2)
                .date(LocalDate.now())
                .place("place")
                .title("title")
                .build());

        Matches acceptedMatch = matchesService.matchAccept(match.getId(), user2.getId().toString());

        // status == ACCEPTED
        assertThat(acceptedMatch.getStatus()).isEqualTo(Status.ACCEPTED);

        ServiceException exception = assertThrows(ServiceException.class, () -> matchesService.matchAccept(acceptedMatch.getId(), user2.getId().toString()));
        assertThat(exception.getCode()).isEqualTo(ErrorCode.MATCH_NOT_PENDING.getCode());
    }

    @Test
    void 수락권한_없는_유저_매치수락() {
        User user1 = userService.register(new RegisterRequestDto("test1@gmail.com", "google", "test1", "ROLE_USER"));
        User user2 = userService.register(new RegisterRequestDto("test2@gmail.com", "google", "test2", "ROLE_USER"));
        User user3 = userService.register(new RegisterRequestDto("test3@gmail.com", "google", "test3", "ROLE_USER"));

        Matches match = matchesRepository.save(Matches.builder()
                .challengedBy(user1)
                .challengedTo(user2)
                .date(LocalDate.now())
                .place("place")
                .title("title")
                .build());

        ServiceException exception;
        // 신청한 사람
        exception = assertThrows(ServiceException.class, () -> matchesService.matchAccept(match.getId(), user1.getId().toString()));
        assertThat(exception.getCode()).isEqualTo(ErrorCode.USER_NOT_PARTICIPANT.getCode());

        // 제 3자
        exception = assertThrows(ServiceException.class, () -> matchesService.matchAccept(match.getId(), user3.getId().toString()));
        assertThat(exception.getCode()).isEqualTo(ErrorCode.USER_NOT_PARTICIPANT.getCode());
    }

    @Test
    void 매치거절() {
        User user1 = userService.register(new RegisterRequestDto("test1@gmail.com", "google", "test1", "ROLE_USER"));
        User user2 = userService.register(new RegisterRequestDto("test2@gmail.com", "google", "test2", "ROLE_USER"));
        Matches match = matchesRepository.save(Matches.builder()
                .challengedBy(user1)
                .challengedTo(user2)
                .date(LocalDate.now())
                .place("place")
                .title("title")
                .build());
        Matches acceptedMatch = matchesService.matchReject(match.getId(), user2.getId().toString());
        assertThat(acceptedMatch.getStatus()).isEqualTo(Status.REJECTED);

    }
}