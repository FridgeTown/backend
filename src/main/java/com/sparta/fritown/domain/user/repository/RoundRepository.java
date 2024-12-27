package com.sparta.fritown.domain.user.repository;

import com.sparta.fritown.domain.user.entity.Round;
import com.sparta.fritown.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoundRepository extends JpaRepository<Round, Long> {
    List<Round> findByUserMatchId(Long matchId);
}