package com.sparta.fritown.domain.repository;

import com.sparta.fritown.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);

//    @Query(value = "SELECT * FROM users WHERE id != :userId ORDER BY RAND() LIMIT :count", nativeQuery = true)
//    List<User> findRandomUsersExcluding(@Param("userId") Long userId, @Param("count") int count);


    @Query(value = """
        SELECT * FROM users\s
        WHERE id NOT IN :excludedUserIds\s
        ORDER BY RAND()\s
        LIMIT :count
   \s""", nativeQuery = true)
    List<User> findRandomUsersExcluding(
            @Param("excludedUserIds") List<Long> excludedUserIds,
            @Param("count") int count
    );

    // 닉네임으로 유저 검색
    Optional<User> findByNickname(String nickname);

}