package com.sparta.fritown.domain.service;

import com.sparta.fritown.domain.dto.user.RegisterRequestDto;
import com.sparta.fritown.domain.dto.user.OpponentDto;
import com.sparta.fritown.domain.entity.enums.WeightClass;
import com.sparta.fritown.domain.repository.UserRepository;
import com.sparta.fritown.domain.entity.User;
import com.sparta.fritown.global.exception.ErrorCode;
import com.sparta.fritown.global.exception.custom.ServiceException;
import com.sparta.fritown.global.s3.service.S3Service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Slf4j
@Service
public class UserService {
    private final UserRepository userRepository;
    private final S3Service s3Service;
    private static final Pattern NICKNAME_PATTERN = Pattern.compile("^[a-zA-Z가-힣]{2,7}$");

    public UserService(UserRepository userRepository, S3Service s3Service) {
        this.userRepository = userRepository;
        //this.passwordEncoder = passwordEncoder;
        this.s3Service = s3Service;
    }


    public User register(RegisterRequestDto requestDto) {
        // 이메일 유효성 검사
        if (userRepository.existsByEmail(requestDto.getEmail())) {
            throw ServiceException.of(ErrorCode.USER_EMAIL_DUPLICATE);
        }

        // 닉네임 유효성 검사
        if (!NICKNAME_PATTERN.matcher(requestDto.getNickname()).matches()) {
           throw ServiceException.of(ErrorCode.USER_NICKNAME_INVALID);
        }

        // 유저 생성 로직
        WeightClass weightClass = WeightClass.fromWeight(requestDto.getWeight());
        User user = new User(requestDto, weightClass);

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
                        s3Service.getFileUrl(user.getProfileImg())        // 프로필 이미지
                ))
                .toList();
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> ServiceException.of(ErrorCode.USER_NOT_FOUND));
    }

    public void updateProfileImage(Long userId, String imageFileName) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> ServiceException.of(ErrorCode.IMAGE_UPLOAD_FAIL));

        user.setProfileImg(imageFileName);
        userRepository.save(user);
    }

    public User getUserInfo(Long id) {
        return userRepository.findById(id).orElseThrow(() -> ServiceException.of(ErrorCode.USER_NOT_FOUND));
    }
}
