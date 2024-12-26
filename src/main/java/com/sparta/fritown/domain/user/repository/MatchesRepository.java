package com.sparta.fritown.domain.user.repository;

import com.sparta.fritown.domain.user.entity.Matches;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MatchesRepository extends JpaRepository<Matches, Long> {

}
