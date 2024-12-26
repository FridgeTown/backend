package com.sparta.fritown.global.docs;

import com.sparta.fritown.global.exception.dto.ErrorResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Test", description = "APIs for test")
public interface TestControllerDocs {

//    @Operation(summary = "IO Error Response checker", description = "ServiceException Handler 작동 확인을 위한 API" )
//    @ApiResponses ({
//            @ApiResponse(responseCode = "400",
//                         description = "status, message, code가 출력 된다면 정상",
//                         content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
//    })
//    @Parameters({
//            @Parameter(name = "test",
//                       description = "actually this api does not have parameters",
//                       example = "i just wanted to show you an example")
//    })
//    public String healthCheck();
}
