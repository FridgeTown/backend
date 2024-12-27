package com.sparta.fritown.global.docs;

import com.sparta.fritown.domain.dto.rounds.RoundsDto;
import com.sparta.fritown.global.exception.dto.ResponseDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Tag(name = "Test", description = "API ")
public interface MatchControllerDocs {
    public ResponseDto<List<RoundsDto>> getRoundsByMatchId(@PathVariable Long matchId);
}
