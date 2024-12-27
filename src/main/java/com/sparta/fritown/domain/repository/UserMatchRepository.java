package com.sparta.fritown.domain.user.repository;

import com.sparta.fritown.domain.user.entity.User;
import com.sparta.fritown.domain.user.entity.UserMatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserMatchRepository extends JpaRepository<UserMatch, Long> {

}