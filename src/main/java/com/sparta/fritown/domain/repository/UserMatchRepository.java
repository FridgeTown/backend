package com.sparta.fritown.domain.repository;

import com.sparta.fritown.domain.entity.UserMatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserMatchRepository extends JpaRepository<UserMatch, Long> {
}
