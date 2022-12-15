package plub.plubserver.domain.plubbing.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import plub.plubserver.common.dto.ApiResponse;
import plub.plubserver.domain.plubbing.dto.PlubbingDto.*;
import plub.plubserver.domain.plubbing.service.PlubbingService;

import javax.validation.Valid;
import java.util.List;

import static plub.plubserver.common.dto.ApiResponse.success;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/plubbing")
@Slf4j
@Api(tags = "플러빙 API", hidden = true)
public class PlubbingController {
    private final PlubbingService plubbingService;

    @ApiOperation(value = "모임 생성")
    @PostMapping
    public ApiResponse<PlubbingResponse> createPlubbing(
            @RequestBody CreatePlubbingRequest createPlubbingRequest) {
        return ApiResponse.success(
                plubbingService.createPlubbing(createPlubbingRequest),
                "plubbing is successfully created."
        );
    }

    @ApiOperation(value = "내 모임 조회")
    @GetMapping("/my")
    public ApiResponse<List<MyPlubbingResponse>> getMyPlubbing(@RequestParam(required = false) Boolean isHost) {
        return success(
                plubbingService.getMyPlubbing(isHost),
                "get my plubbing."
        );
    }
    // TODO 타임라인, 투두리스트 따로 조회
    @ApiOperation(value = "모임 메인페이지 조회")
    @GetMapping("/main/{plubbingId}")
    public ApiResponse<MainPlubbingResponse> getMainPlubbing(@PathVariable Long plubbingId) {
        return success(
                plubbingService.getMainPlubbing(plubbingId),
                "get main plubbing."
        );
    }

    @ApiOperation(value = "모임 삭제")
    @PostMapping("/delete/{plubbingId}")
    public ApiResponse<PlubbingMessage> deletePlubbing(@PathVariable Long plubbingId) {
        return success(
                plubbingService.deletePlubbing(plubbingId),
                "plubbing is successfully deleted."
        );
    }

    @ApiOperation(value = "모임 종료하기")
    @PostMapping("/end/{plubbingId}")
    public ApiResponse<PlubbingMessage> endPlubbing(@PathVariable Long plubbingId) {
        return success(
                plubbingService.endPlubbing(plubbingId),
                "plubbing is successfully ended."
        );
    }

    @ApiOperation(value = "모임 수정")
    @PostMapping("/update/{plubbingId}")
    public ApiResponse<PlubbingResponse> updatePlubbing(@PathVariable Long plubbingId, @Valid @RequestBody UpdatePlubbingRequest updatePlubbingRequest) {
        return success(
                plubbingService.updatePlubbing(plubbingId, updatePlubbingRequest),
                "plubbing is successfully updated."
        );
    }
}
