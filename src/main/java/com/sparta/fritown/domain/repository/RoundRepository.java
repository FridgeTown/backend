package com.sparta.fritown.domain.repository;

import com.sparta.fritown.domain.entity.Round;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoundRepository extends JpaRepository<Round, Long> {
    List<Round> findByUserMatchId(Long userMatchId);
}
