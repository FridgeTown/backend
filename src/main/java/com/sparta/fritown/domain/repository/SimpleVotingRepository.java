package com.sparta.fritown.domain.repository;

import com.sparta.fritown.domain.entity.Matches;
import com.sparta.fritown.domain.entity.Vote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SimpleVotingRepository extends JpaRepository<Vote, Long> {
    Optional<Vote> findByChannelId(String channelId);
}
