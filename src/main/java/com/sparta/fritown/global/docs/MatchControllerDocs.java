package com.sparta.fritown.global.docs;

import com.sparta.fritown.domain.dto.match.MatchFutureDto;
import com.sparta.fritown.domain.dto.match.MatchSummaryDto;
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



    @Operation(
            summary = "Get match history for a user",
            description = "Fetches a list of completed match summaries for the authenticated user."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved match history",
                    content = @Content(schema = @Schema(implementation = MatchSummaryDto.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))
            )
    })
    @Parameter(
            name = "userDetails",
            description = "Details of the authenticated user (automatically injected)",
            required = true
    )
    public ResponseDto<List<MatchSummaryDto>> getMatchHistory(@AuthenticationPrincipal UserDetailsImpl userDetails);


    @Operation(
            summary = "Get future match information for a user",
            description = "Fetches a list of future match details (not yet completed matches) for the authenticated user."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved future match information",
                    content = @Content(schema = @Schema(implementation = MatchFutureDto.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "No future matches found",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))
            )
    })
    @Parameter(
            name = "userDetails",
            description = "Details of the authenticated user (automatically injected)",
            required = true
    )
    public ResponseDto<List<MatchFutureDto>> getMatchFuture(@AuthenticationPrincipal UserDetailsImpl userDetails);
}
