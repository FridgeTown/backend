package com.sparta.fritown.domain.repository;

import com.sparta.fritown.domain.entity.Matches;
import com.sparta.fritown.domain.entity.User;
import com.sparta.fritown.domain.entity.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MatchesRepository extends JpaRepository<Matches, Long> {
    List<Matches> findByChallengedToAndChallengedBy(User challengedTo, User challengedBy);
    List<Matches> findByStatusIn(List<Status> statuses);
    List<Matches> findByChallengedToIdAndStatus(Long challengedById, Status status);
    List<Matches> findByStatus(Status status);
    Matches findByChannelId(String channelId);
}
