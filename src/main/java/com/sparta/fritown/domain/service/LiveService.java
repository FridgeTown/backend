package com.sparta.fritown.domain.service;

import com.sparta.fritown.domain.dto.live.LiveResponseDto;
import com.sparta.fritown.domain.dto.live.LiveStartRequestDto;
import com.sparta.fritown.domain.entity.Matches;
import com.sparta.fritown.domain.entity.enums.Status;
import com.sparta.fritown.domain.repository.MatchesRepository;
import com.sparta.fritown.global.exception.ErrorCode;
import com.sparta.fritown.global.exception.custom.ServiceException;
import com.sparta.fritown.global.s3.service.S3Service;
import lombok.extern.slf4j.Slf4j;
import com.sparta.fritown.domain.entity.Matches;
import com.sparta.fritown.domain.repository.MatchesRepository;
import com.sparta.fritown.global.exception.ErrorCode;
import com.sparta.fritown.global.exception.custom.ServiceException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class LiveService {

    private final MatchesRepository matchesRepository;
    private final S3Service s3Service;

    public LiveService(MatchesRepository matchesRepository, S3Service s3Service) {
        this.matchesRepository = matchesRepository;
        this.s3Service = s3Service;
    }

    public void liveStart(LiveStartRequestDto liveStartRequestDto) {
        Matches matches = matchesRepository.findById(liveStartRequestDto.getMatchId())
                .orElseThrow(() -> ServiceException.of(ErrorCode.MATCH_NOT_FOUND));

        matches.setPlace(liveStartRequestDto.getPlace());
        matches.setStatus(Status.PROGRESS);
        matchesRepository.save(matches);
    }

    public void liveEnd(Long matchId) {
        Matches matches = matchesRepository.findById(matchId)
                .orElseThrow(() -> ServiceException.of(ErrorCode.MATCH_NOT_FOUND));

        matches.setStatus(Status.DONE);
        matchesRepository.save(matches);
    }

    public void liveWatchStart(Long matchId) {

        Matches matches = matchesRepository.findById(matchId).orElseThrow(() -> ServiceException.of(ErrorCode.MATCH_NOT_FOUND));
        matches.incrementViewNum();

        // 변경 사항 저장
        matchesRepository.save(matches);
    }

    public void liveWatchEnd(Long matchId) {
        Matches matches = matchesRepository.findById(matchId).orElseThrow(() -> ServiceException.of(ErrorCode.MATCH_NOT_FOUND));
        matches.decrementViewNum();

        // 변경 사항 저장
        matchesRepository.save(matches);

    }

    public List<LiveResponseDto> getLiveList() {
        List<Matches> matches = matchesRepository.findByStatus(Status.PROGRESS);
        List<LiveResponseDto> liveResponseDtos = new ArrayList<>();
        for (Matches matche : matches) {
            String fileUrl = s3Service.getFileUrl(matche.getThumbNail());
            LiveResponseDto liveResponseDto = new LiveResponseDto(matche, fileUrl);
            liveResponseDtos.add(liveResponseDto);
        }

        return liveResponseDtos;
    }

    public void updateThumbNail(Long matchId, String imageFileName) {
        Matches matches = matchesRepository.findById(matchId)
                .orElseThrow(() -> ServiceException.of(ErrorCode.IMAGE_UPLOAD_FAIL));
        matches.setThumbNail(imageFileName);

        matchesRepository.save(matches);
    }
}
