package plub.plubserver.domain.plubbing.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;
import plub.plubserver.common.dto.ApiResponse;
import plub.plubserver.domain.plubbing.dto.PlubbingDto.*;
import plub.plubserver.domain.plubbing.service.PlubbingService;

import javax.validation.Valid;
import java.util.List;

import static plub.plubserver.common.dto.ApiResponse.success;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/plubbings")
@Slf4j
@Api(tags = "플러빙 API", hidden = true)
public class PlubbingController {
    private final PlubbingService plubbingService;

    @ApiOperation(value = "모임 생성")
    @PostMapping
    public ApiResponse<Long> createPlubbing(
            @Valid @RequestBody CreatePlubbingRequest createPlubbingRequest) {
        return success(plubbingService.createPlubbing(createPlubbingRequest));
    }

    @ApiOperation(value = "내 모임 조회")
    @GetMapping("/my")
    public ApiResponse<MyPlubbingListResponse> getMyPlubbing(@RequestParam(required = false) Boolean isHost) {
        return success(plubbingService.getMyPlubbing(isHost));
    }

    // TODO 타임라인, 투두리스트 따로 조회
    @ApiOperation(value = "모임 메인페이지 조회")
    @GetMapping("/{plubbingId}/main")
    public ApiResponse<MainPlubbingResponse> getMainPlubbing(@PathVariable Long plubbingId) {
        return success(plubbingService.getMainPlubbing(plubbingId));
    }

    @ApiOperation(value = "모임 삭제")
    @DeleteMapping("/{plubbingId}")
    public ApiResponse<PlubbingMessage> deletePlubbing(@PathVariable Long plubbingId) {
        return success(plubbingService.deletePlubbing(plubbingId));
    }

    @ApiOperation(value = "모임 종료하기")
    @PutMapping("/{plubbingId}/end")
    public ApiResponse<PlubbingMessage> endPlubbing(@PathVariable Long plubbingId) {
        return success(plubbingService.endPlubbing(plubbingId));
    }

    @ApiOperation(value = "모임 수정")
    @PutMapping("/{plubbingId}")
    public ApiResponse<PlubbingResponse> updatePlubbing(@PathVariable Long plubbingId,
                                                        @Valid @RequestBody UpdatePlubbingRequest updatePlubbingRequest) {
        return success(plubbingService.updatePlubbing(plubbingId, updatePlubbingRequest));
    }

    @ApiOperation(value = "추천 모임")
    @GetMapping("/recommendation")
    public ApiResponse<Page<PlubbingCardResponse>> getRecommendation(@PageableDefault(size = 10) Pageable pageable) {
        Page<PlubbingCardResponse> plubbingCardResponses = plubbingService.getRecommendation(pageable);
        return success(plubbingCardResponses);
    }

    @ApiOperation(value = "카테고리별 모임 조회")
    @GetMapping("/categories/{categoryId}")
    public ApiResponse<Page<PlubbingCardResponse>> getPlubbingByCatergory(@PathVariable Long categoryId,
                                                                          @PageableDefault(size = 10) Pageable pageable) {
        Page<PlubbingCardResponse> plubbingCardResponses = plubbingService.getPlubbingByCatergory(categoryId, pageable);
        return success(plubbingCardResponses);
    }
}
