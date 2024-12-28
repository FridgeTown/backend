package com.sparta.fritown.domain.service;

import com.sparta.fritown.domain.dto.RegisterRequestDto;
import com.sparta.fritown.domain.entity.Matches;
import com.sparta.fritown.domain.entity.User;
import com.sparta.fritown.domain.entity.enums.Status;
import com.sparta.fritown.domain.repository.MatchesRepository;
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
class MatchServiceTest {

    @Autowired
    MatchesRepository matchesRepository;

    @Autowired
    UserService userService;
    @Autowired
    MatchService matchService;

    @Test
    void 존재하지_않는_매치수락() {
        User user = userService.register(new RegisterRequestDto("test1@gmail.com", "google", "test1", "ROLE_USER"));

        ServiceException exception = assertThrows(ServiceException.class, () -> matchService.matchAccept(0L, user.getEmail()));
        assertThat(exception.getCode()).isEqualTo(ErrorCode.MATCH_NOT_FOUND.getCode());
    }

    @Test
    void 매치수락() {
        User user1 = userService.register(new RegisterRequestDto("test1@gmail.com", "google", "test1", "ROLE_USER"));
        User user2 = userService.register(new RegisterRequestDto("test2@gmail.com", "google", "test2", "ROLE_USER"));

        Matches matches = matchesRepository.save(Matches.builder()
                .challengedBy(user1)
                .challengedTo(user2)
                .date(LocalDate.now())
                .place("place")
                .title("title")
                .build());

        // 매치 신청한 사람
        boolean isAccepted = matchService.matchAccept(matches.getId(), user2.getEmail());
        assertThat(isAccepted).isTrue();
    }

    @Test
    void 대기상태_아닌_매치수락() {
        User user1 = userService.register(new RegisterRequestDto("test1@gmail.com", "google", "test1", "ROLE_USER"));
        User user2 = userService.register(new RegisterRequestDto("test2@gmail.com", "google", "test2", "ROLE_USER"));

        // status == PENDING
        Matches matches = matchesRepository.save(Matches.builder()
                .challengedBy(user1)
                .challengedTo(user2)
                .date(LocalDate.now())
                .place("place")
                .title("title")
                .build());

        boolean isAccepted = matchService.matchAccept(matches.getId(), user2.getEmail());

        // status == ACCEPTED
        assertThat(isAccepted).isTrue();

        ServiceException exception = assertThrows(ServiceException.class, () -> matchService.matchAccept(matches.getId(), user2.getEmail()));
        assertThat(exception.getCode()).isEqualTo(ErrorCode.MATCH_NOT_PENDING.getCode());
    }

    @Test
    void 수락권한_없는_유저_매치수락() {
        User user1 = userService.register(new RegisterRequestDto("test1@gmail.com", "google", "test1", "ROLE_USER"));
        User user2 = userService.register(new RegisterRequestDto("test2@gmail.com", "google", "test2", "ROLE_USER"));
        User user3 = userService.register(new RegisterRequestDto("test3@gmail.com", "google", "test3", "ROLE_USER"));

        Matches matches = matchesRepository.save(Matches.builder()
                .challengedBy(user1)
                .challengedTo(user2)
                .date(LocalDate.now())
                .place("place")
                .title("title")
                .build());

        ServiceException exception;
        // 신청한 사람
        exception = assertThrows(ServiceException.class, () -> matchService.matchAccept(matches.getId(), user1.getEmail()));
        assertThat(exception.getCode()).isEqualTo(ErrorCode.USER_NOT_CHALLENGED_TO.getCode());

        // 제 3자
        exception = assertThrows(ServiceException.class, () -> matchService.matchAccept(matches.getId(), user3.getEmail()));
        assertThat(exception.getCode()).isEqualTo(ErrorCode.USER_NOT_CHALLENGED_TO.getCode());
    }

    @Test
    void 매치거절() {
        User user1 = userService.register(new RegisterRequestDto("test1@gmail.com", "google", "test1", "ROLE_USER"));
        User user2 = userService.register(new RegisterRequestDto("test2@gmail.com", "google", "test2", "ROLE_USER"));
        Matches matches = matchesRepository.save(Matches.builder()
                .challengedBy(user1)
                .challengedTo(user2)
                .date(LocalDate.now())
                .place("place")
                .title("title")
                .build());
        boolean isRejected = matchService.matchReject(matches.getId(), user2.getEmail().toString());
        assertThat(isRejected).isTrue();

    }
}