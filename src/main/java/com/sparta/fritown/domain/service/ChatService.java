package com.sparta.fritown.domain.service;
import com.sparta.fritown.domain.dto.chat.KlatAddChannelMemberDto;
import com.sparta.fritown.domain.dto.chat.KlatDeleteChannelRequestDto;
import com.sparta.fritown.domain.dto.live.LiveChatRoomResponse;
import com.sparta.fritown.domain.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.*;
import com.sparta.fritown.domain.dto.chat.KlatCreateChannelRequestDto;

@Slf4j
@Service
public class ChatService {

    private final RestTemplate restTemplate = new RestTemplate();
    @Value("${klat.id}")
    private String klatId;

    @Value("${klat.key}")
    private String klatKey;

    // 채널 생성
    // MatchService 에서 사용
    public String createChannel(List<User> userList, String chatRoomName, String type, String category)
    {
        // 채팅방에 참여하고 있는 유저 Id의 리스트
        List<String> userIdList = new ArrayList<>();
        for (User user: userList)
        {
            userIdList.add(String.valueOf(user.getId()));
        }
        String ownerId = userIdList.get(0);

        KlatCreateChannelRequestDto request = new KlatCreateChannelRequestDto(chatRoomName,ownerId,type,userIdList,category);

        return callCreateChannelApi(request);
    }

    public String createLiveChatChannel(User user, String chatRoomName, String type, String category)
    {
        String ownerId = String.valueOf(user.getId());
        List<String> userIdList = new ArrayList<>();

        userIdList.add(ownerId);

        KlatCreateChannelRequestDto request = new KlatCreateChannelRequestDto(chatRoomName,ownerId,type,userIdList,category);

        return callCreateChannelApi(request);
    }

    public void addChannelMember(KlatAddChannelMemberDto addChannelMemberRequestDto) {
        String externalApiUrl = "https://api.talkplus.io/v1.4/api/channels/:channelId/members/add";

        try {
            // HTTP 헤더 설정
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("app-id", klatId);
            headers.set("api-key", klatKey);

            // HTTP Entity 생성
            HttpEntity<KlatAddChannelMemberDto> request = new HttpEntity<>(addChannelMemberRequestDto, headers);

            // API 호출
            ResponseEntity<KlatAddChannelMemberDto> response = restTemplate.exchange(
                    externalApiUrl,
                    HttpMethod.POST,
                    request,
                    KlatAddChannelMemberDto.class
            );
            log.info("response: {}", response);

            if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null) {
                log.error("외부 API 호출 실패. 상태 코드 : {}", response.getStatusCode());
            }

        } catch (Exception e) {
            log.error("외부 API 호출 실패 : {}", e.getMessage());
        }
    }

    public void callDeleteChannelApi(KlatDeleteChannelRequestDto deleteChannelRequestDto) {
        // API URL 설정
        String externalApiUrl = "https://api.talkplus.io/v1.4/api/channels/" + deleteChannelRequestDto.getChannelId();

        try {
            // HTTP 헤더 설정
            HttpHeaders headers = new HttpHeaders();
            headers.set("app-id", klatId);
            headers.set("api-key", klatKey);

            // HTTP Entity 생성
            HttpEntity<KlatDeleteChannelRequestDto> request = new HttpEntity<>(null, headers);

            // GET CHANNEL 이후 OwnerId 판단 필요?

            // API 호출
            ResponseEntity<KlatCreateChannelRequestDto> response = restTemplate.exchange(
                    externalApiUrl,
                    HttpMethod.DELETE,
                    request,
                    KlatCreateChannelRequestDto.class
            );
            log.info("response: {}", response);

            if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null) {
                log.error("외부 API 호출 실패. 상태 코드 : {}", response.getStatusCode());
            }

        } catch (Exception e)
        {
            log.error("외부 API 호출 실패 : {}", e.getMessage());
        }
    }

    private String callCreateChannelApi(KlatCreateChannelRequestDto createChannelRequestDto) {
        // API URL 설정
        String externalApiUrl = "https://api.talkplus.io/v1.4/api/channels/create";

        try {
            // HTTP 헤더 설정
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("app-id", klatId);
            headers.set("api-key", klatKey);

            // HTTP Entity 생성
            HttpEntity<KlatCreateChannelRequestDto> request = new HttpEntity<>(createChannelRequestDto, headers);

            // API 호출
            ResponseEntity<LiveChatRoomResponse> response = restTemplate.exchange(
                    externalApiUrl,
                    HttpMethod.POST,
                    request,
                    LiveChatRoomResponse.class
            );
            log.info("response: {}", response);

            if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null) {
                log.error("외부 API 호출 실패. 상태 코드 : {}", response.getStatusCode());
            }

            return response.getBody().getChannel().getId();

        } catch (Exception e)
        {
            log.error("외부 API 호출 실패 : {}", e.getMessage());
            throw new IllegalArgumentException("");
        }

    }

}
