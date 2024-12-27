package com.sparta.fritown;

import com.sparta.fritown.domain.user.entity.Round;
import com.sparta.fritown.domain.user.entity.UserMatch;
import com.sparta.fritown.domain.user.repository.RoundRepository;
import com.sparta.fritown.domain.user.repository.UserMatchRepository;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Commit;
import org.springframework.test.annotation.Rollback;

import java.util.List;
import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
public class RoundTest {

    @Autowired
    private RoundRepository roundRepository;

    @Autowired
    private UserMatchRepository userMatchRepository;

    @Autowired
    private EntityManager entityManager;

    @BeforeEach
    @Rollback(false)
    public void setup() {

        // 데이터 베이스 초기화
        roundRepository.deleteAll();
        userMatchRepository.deleteAll();

        // 테스트 데이터 삽입
        UserMatch userMatch = new UserMatch();
        userMatchRepository.save(userMatch);
        System.out.println("Saved UserMatch ID:" + userMatch.getId());

        Round round1 = new Round();
        round1.setUserMatch(userMatch);
        round1.setRoundNum(1);
        round1.setKcal(150);
        round1.setHeartBeat(80);
        round1.setPunchNum(20);

        Round round2 = new Round();
        round2.setUserMatch(userMatch);
        round2.setRoundNum(2);
        round2.setKcal(200);
        round2.setHeartBeat(90);
        round2.setPunchNum(25);


        roundRepository.save(round1);
        roundRepository.save(round2);

        entityManager.flush();
        entityManager.clear();
    }

    @Test
    @Commit
    public void testFindAllRounds() {
        // 데이터베이스에서 Round 조회
        List<Round> rounds = roundRepository.findAll();

        // 검증
        assertThat(rounds).isNotEmpty();
        assertThat(rounds.size()).isEqualTo(2);

        // 첫 번째 Round 검증
        assertThat(rounds.get(0).getRoundNum()).isEqualTo(1);
        assertThat(rounds.get(0).getKcal()).isEqualTo(150);

        // 두 번째 Round 검증
        assertThat(rounds.get(1).getRoundNum()).isEqualTo(2);
        assertThat(rounds.get(1).getKcal()).isEqualTo(200);


    }

    @Test
    public void testFindRoundsByMatchId() {
        // 특정 UserMatch ID로 Round 조회
        UserMatch userMatch = userMatchRepository.findAll().get(0);
        Long matchId = userMatch.getId();

        List<Round> rounds = roundRepository.findByUserMatchId(matchId);

        // 검증
        Assertions.assertThat(rounds).isNotEmpty();
        Assertions.assertThat(rounds.size()).isEqualTo(2);

        // 라운드 데이터 검증
        Assertions.assertThat(rounds.get(0).getRoundNum()).isEqualTo(1);
        Assertions.assertThat(rounds.get(1).getRoundNum()).isEqualTo(2);


    }
}

