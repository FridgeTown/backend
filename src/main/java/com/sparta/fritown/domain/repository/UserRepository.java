package com.sparta.fritown.domain.repository;

import com.sparta.fritown.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);

    @Query(value = "SELECT * FROM users WHERE id != :userId ORDER BY RAND() LIMIT :count", nativeQuery = true)
    List<User> findRandomUsersExcluding(@Param("userId") Long userId, @Param("count") int count);
}