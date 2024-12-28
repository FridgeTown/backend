package com.sparta.fritown.domain.service;

import com.sparta.fritown.domain.dto.RegisterRequestDto;
import com.sparta.fritown.domain.dto.user.OpponentDto;
import com.sparta.fritown.domain.repository.UserRepository;
import com.sparta.fritown.domain.entity.User;
import com.sparta.fritown.global.exception.ErrorCode;
import com.sparta.fritown.global.exception.custom.ServiceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }


    public User register(RegisterRequestDto requestDto) {
        if (userRepository.existsByEmail(requestDto.getEmail())) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }
        System.out.println(requestDto.getRole());
        User user = new User(
                requestDto.getEmail(),
                requestDto.getRole(),
                requestDto.getName(),
                requestDto.getProfileImage(),
                requestDto.getProvider()
        );

//        this.email = email;
//        this.role = role;
//        this.nickname = nickname;
//        this.profileImage = profileImage;
//        this.provider = provider;
//        this.password = password;
        log.info("userService_register called");
        return userRepository.save(user);
    }

    public List<OpponentDto> getRandomUsers(Long userId) {
        // 데이터베이스에서 랜덤 사용자 가져오기
        int count = 20;
        List<User> users = userRepository.findRandomUsersExcluding(userId, count);

        if (users.isEmpty())
        {
            // throw ServiceException.of(ErrorCode.RECOMMENDED_USERS_NOT_FOUND);
            return new ArrayList<>();
        }

        // User 엔티티를 OpponentDto로 변환
        return users.stream()
                .map(user -> new OpponentDto(
                        user.getId(),
                        user.getNickname(),         // 닉네임
                        user.getHeight(),           // 키
                        user.getWeight(),           // 몸무게
                        user.getBio(),              // 소개글
                        user.getGender().toString(),// 성별 (Gender Enum -> String 변환)
                        user.getProfileImg()        // 프로필 이미지
                ))
                .toList();
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> ServiceException.of(ErrorCode.USER_NOT_FOUND));
    }

}
