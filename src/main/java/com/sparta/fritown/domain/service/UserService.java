package com.sparta.fritown.domain.service;

import com.sparta.fritown.domain.dto.user.RegisterRequestDto;
import com.sparta.fritown.domain.dto.user.OpponentDto;
import com.sparta.fritown.domain.entity.Matches;
import com.sparta.fritown.domain.entity.enums.Status;
import com.sparta.fritown.domain.repository.MatchesRepository;
import com.sparta.fritown.domain.repository.UserRepository;
import com.sparta.fritown.domain.entity.User;
import com.sparta.fritown.global.exception.ErrorCode;
import com.sparta.fritown.global.exception.custom.ServiceException;
import com.sparta.fritown.global.s3.service.S3Service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class UserService {
    private final UserRepository userRepository;
    private final S3Service s3Service;
    private final MatchesRepository matchesRepository;

    public UserService(UserRepository userRepository, S3Service s3Service, MatchesRepository matchesRepository) {
        this.userRepository = userRepository;
        //this.passwordEncoder = passwordEncoder;
        this.s3Service = s3Service;
        this.matchesRepository = matchesRepository;
    }


    public User register(RegisterRequestDto requestDto) {
        if (userRepository.existsByEmail(requestDto.getEmail())) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }
        System.out.println(requestDto.getRole());
        User user = new User(requestDto);

        log.info("userService_register called");
        return userRepository.save(user);
    }

    public List<OpponentDto> getRandomUsers(Long userId) {
        // 데이터베이스에서 랜덤 사용자 가져오기
        int count = 20;
        
        // 1. Match 테이블에서 상태가 ACCEPTED/PROGRESS인 매칭 가져오기
        List<Matches> acceptedOrProgressMatches = matchesRepository.findByStatusIn(List.of(Status.ACCEPTED,Status.PROGRESS));

        // 2. 매칭된 사용자 ID 리스트 생성 즉, 제외되어야 할 사용자 ID, HashSet을 이용해 중복을 제거

        Set<Long> excludedUserIds = getExcludedUserIds(userId, acceptedOrProgressMatches);

        // 3. 제외된 사용자들을 제외하고 랜덤으로 사용자 가져오기
        List<User> users = userRepository.findRandomUsersExcluding(new ArrayList<>(excludedUserIds), 20);

        // 4. 만약 사용자 리스트가 비어있다면 빈 리스트를 반환
        if (users.isEmpty())
        {
            // throw ServiceException.of(ErrorCode.RECOMMENDED_USERS_NOT_FOUND);
            return new ArrayList<>();
        }

        // 5. User 엔티티를 OpponentDto로 변환
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

    private Set<Long> getExcludedUserIds(Long userId, List<Matches> acceptedOrProgressMatches) {
        Set<Long> excludedUserIds = new HashSet<>();
        for(Matches match : acceptedOrProgressMatches)
        {
            // ACCEPTED/PROGRESS 매치 중 getChallengedTo에 자신의 ID가 들어가있는 경우
            if(match.getChallengedTo().getId().equals(userId))
            {
                excludedUserIds.add(match.getChallengedBy().getId());
            }
            // ACCEPTED/PROGRESS 매치 중 getChallengedBy에 자신의 ID가 들어가있는 경우
            else if (match.getChallengedBy().getId().equals(userId))
            {
                excludedUserIds.add(match.getChallengedTo().getId());
            }


        }
        // 자기 자신도 제외
        excludedUserIds.add(userId);
        return excludedUserIds;
    }
}
