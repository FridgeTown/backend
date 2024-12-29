package com.sparta.fritown.domain.service;
import com.sparta.fritown.domain.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.*;
import com.sparta.fritown.domain.dto.chat.KlatCreateChannelRequestDto;

@Slf4j
@Service
public class ChatService {

    private final RestTemplate restTemplate = new RestTemplate();

    // 채널 생성
    // MatchService 에서 사용
    public void createChannel(List<User> userList, String chatRoomName, String type, String category)
    {
        // 채팅방에 참여하고 있는 유저 Id의 리스트
        List<String> userIdList = new ArrayList<>();
        for (User user: userList)
        {
            userIdList.add(user.getNickname());
        }
        String ownerId = userIdList.get(0);

        KlatCreateChannelRequestDto request = new KlatCreateChannelRequestDto(chatRoomName,ownerId,type,userIdList,category);

        callCreateChannelApi(request);
    }


    private void callCreateChannelApi(KlatCreateChannelRequestDto createChannelRequestDto) {
        // API URL 설정
        String channelCreateApiUrl = "https://api.talkplus.io/v1.4/api/channels/create";
        String appId = "abc3aa8d-947b-4549-a793-7c79dcd57333";
        String apiKey = "ed4172e48a0c3ff202af330fd381f81cdd2c02f92fec9177b39a8f91b49c8d29";


        // HTTP 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("app-id", appId);
        headers.set("api-key", apiKey);

        // HTTP Entity 생성
        HttpEntity<KlatCreateChannelRequestDto> request = new HttpEntity<>(createChannelRequestDto, headers);

        // API 호출
        ResponseEntity<KlatCreateChannelRequestDto> response = restTemplate.exchange(
                channelCreateApiUrl,
                HttpMethod.POST,
                request,
                KlatCreateChannelRequestDto.class
        );

    }

}
