package com.sparta.fritown.domain.service;

import com.sparta.fritown.domain.dto.user.KlatCreateUserRequestDto;
import com.sparta.fritown.domain.dto.user.KlatImageRequestDto;
import com.sparta.fritown.domain.dto.user.KlatLoginRequestDto;
import com.sparta.fritown.domain.dto.user.KlatResponseDto;
import com.sparta.fritown.domain.entity.User;
import com.sparta.fritown.domain.repository.UserRepository;
import com.sparta.fritown.global.exception.ErrorCode;
import com.sparta.fritown.global.exception.custom.ServiceException;
import com.sparta.fritown.global.s3.service.S3Service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.File;

@Slf4j
@Service
public class KlatService {

    private final RestTemplate restTemplate;
    private final UserRepository userRepository;
    private final S3Service s3Service;

    @Value("${klat.id}")
    private String klatId;

    @Value("${klat.key}")
    private String klatKey;

    @Value("${klat.user.password}")
    private String klatUserPassword;

    public KlatService(RestTemplate restTemplate, UserRepository userRepository, S3Service s3Service) {
        this.restTemplate = restTemplate;
        this.userRepository = userRepository;
        this.s3Service = s3Service;
    }


    public String signup(Long longUserId, String username, String profileImageUrl) {

        String userId = String.valueOf(longUserId);
        String externalApiUrl = "https://api.talkplus.io/v1.4/api/users/create";

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("app-id", klatId);
            headers.set("api-key", klatKey);
            headers.setContentType(MediaType.APPLICATION_JSON);

            KlatCreateUserRequestDto requestDto = new KlatCreateUserRequestDto(userId, username, profileImageUrl, klatUserPassword);

            HttpEntity<KlatCreateUserRequestDto> requestEntity = new HttpEntity<>(requestDto, headers);


            ResponseEntity<KlatResponseDto> response = restTemplate.exchange(
                    externalApiUrl,
                    HttpMethod.POST,
                    requestEntity,
                    KlatResponseDto.class
            );

            log.info("response: {}", response);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return response.getBody().getLoginToken();
            } else {
                log.error("외부 API 호출 실패. 상태 코드 : {}", response.getStatusCode());
                return null;
            }

        } catch (Exception e) {
            log.error("외부 API 호출 실패 : {}", e.getMessage());
            return null;
        }
    }

    public String login(Long longUserId) {
        String userId = String.valueOf(longUserId);
        String externalApiUrl = "https://api.talkplus.io/v1.4/api/users/login";

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("app-id", klatId);
            headers.set("api-key", klatKey);
            headers.setContentType(MediaType.APPLICATION_JSON);

            KlatLoginRequestDto requestDto = new KlatLoginRequestDto(userId, klatUserPassword);
            HttpEntity<KlatLoginRequestDto> requestEntity = new HttpEntity<>(requestDto, headers);

            ResponseEntity<KlatResponseDto> response = restTemplate.exchange(
                    externalApiUrl,
                    HttpMethod.POST,
                    requestEntity,
                    KlatResponseDto.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return response.getBody().getLoginToken();
            } else {
                log.error("외부 API 호출 실패. 상태 코드 : {}", response.getStatusCode());
                return null;
            }
        } catch (Exception e) {
            log.error("외부 API 호출 실패 : {}", e.getMessage());
            return null;
        }
    }

    public void updateProfileImage(Long longUserId, String fileName) {
        User user = userRepository.findById(longUserId).orElseThrow(() -> ServiceException.of(ErrorCode.USER_NOT_FOUND));

        String userId = String.valueOf(longUserId);
        String url = "https://api.talkplus.io/v1.4/api/users/" + userId;
        String fileUrl = s3Service.getFileUrl(fileName);

        log.info("fileUrl : {}", fileUrl);

        try {

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("app-id", klatId);
            headers.set("api-key", klatKey);

            KlatImageRequestDto requestDto = new KlatImageRequestDto(user.getNickname(), fileUrl);
            HttpEntity<KlatImageRequestDto> requestEntity = new HttpEntity<>(requestDto, headers);

            log.info("username: {}", user.getNickname());
            log.info("fileURL: {}", fileUrl);

            ResponseEntity<KlatResponseDto> response = restTemplate.exchange(
                    url,
                    HttpMethod.PUT,
                    requestEntity,
                    KlatResponseDto.class
            );

        } catch (Exception e) {
            log.error("Klat 이미지 업로드 실패 : {}", e.getMessage());
        }

    }

}
