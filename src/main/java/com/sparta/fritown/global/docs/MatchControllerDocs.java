package com.sparta.fritown.global.docs;

import com.sparta.fritown.domain.dto.rounds.RoundsDto;
import com.sparta.fritown.global.exception.dto.ErrorResponseDto;
import com.sparta.fritown.global.exception.dto.ResponseDto;
import com.sparta.fritown.global.security.dto.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Tag(name = "Test", description =  "APIs for match-related operations")
public interface MatchControllerDocs {
    @Operation(
            summary = "Get round information for a specific match",
            description = "Fetches detailed round information including calories burned and heart rate for a given MatchId."
    )

    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved round information",
                    content = @Content(schema = @Schema(implementation = RoundsDto.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "MatchId not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))
            )
    })

    @Parameters({
            @Parameter(
                    name = "matchId",
                    description = "Unique identifier for the match",
                    example = "1",
                    required = true
            )
    })

    public ResponseDto<List<RoundsDto>> getRoundsByMatchId(@PathVariable Long matchId, @AuthenticationPrincipal UserDetailsImpl userDetails);

}
