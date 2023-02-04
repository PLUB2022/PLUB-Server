package plub.plubserver.domain.plubbing.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;
import plub.plubserver.common.dto.ApiResponse;
import plub.plubserver.common.dto.PageResponse;
import plub.plubserver.domain.account.model.Account;
import plub.plubserver.domain.account.service.AccountService;
import plub.plubserver.domain.plubbing.dto.PlubbingDto.*;
import plub.plubserver.domain.plubbing.service.PlubbingService;
import plub.plubserver.domain.recruit.dto.RecruitDto.UpdateRecruitQuestionRequest;
import plub.plubserver.domain.recruit.dto.RecruitDto.UpdateRecruitRequest;

import javax.validation.Valid;

import static plub.plubserver.common.dto.ApiResponse.success;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/plubbings")
@Slf4j
@Api(tags = "플러빙 API", hidden = true)
public class PlubbingController {
    private final PlubbingService plubbingService;
    private final AccountService accountService;

    @ApiOperation(value = "모임 생성")
    @PostMapping
    public ApiResponse<PlubbingIdResponse> createPlubbing(
            @Valid @RequestBody CreatePlubbingRequest createPlubbingRequest
    ) {
        Account loginAccount = accountService.getCurrentAccount();
        return success(plubbingService.createPlubbing(loginAccount, createPlubbingRequest));
    }

    @ApiOperation(value = "내 모임 조회")
    @GetMapping("/my")
    public ApiResponse<MyPlubbingListResponse> getMyPlubbing(@RequestParam(required = false) Boolean isHost) {
        return success(plubbingService.getMyPlubbing(isHost));
    }

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
    @PutMapping("/{plubbingId}/status")
    public ApiResponse<PlubbingMessage> endPlubbing(@PathVariable Long plubbingId) {
        return success(plubbingService.endPlubbing(plubbingId));
    }

    @ApiOperation(value = "모집글 수정")
    @PutMapping("/{plubbingId}/recruit")
    public ApiResponse<PlubbingIdResponse> updateRecruit(
            @PathVariable Long plubbingId,
            @Valid @RequestBody UpdateRecruitRequest updateRecruitRequest
    ) {
        return success(plubbingService.updateRecruit(plubbingId, updateRecruitRequest));
    }

    @ApiOperation(value = "모임 정보 수정")
    @PutMapping("/{plubbingId}")
    public ApiResponse<PlubbingIdResponse> updatePlubbing(
            @PathVariable Long plubbingId,
            @Valid @RequestBody UpdatePlubbingRequest updatePlubbingRequest
    ) {
        return success(plubbingService.updatePlubbing(plubbingId, updatePlubbingRequest));
    }

    @ApiOperation(value = "게스트 질문 수정")
    @PutMapping("/{plubbingId}/recruit/questions")
    public ApiResponse<PlubbingIdResponse> updateRecruitQuestions(
            @PathVariable Long plubbingId,
            @Valid @RequestBody UpdateRecruitQuestionRequest updateRecruitQuestionRequest
    ) {
        return success(plubbingService.updateRecruitQuestion(plubbingId, updateRecruitQuestionRequest));
    }

    @ApiOperation(value = "추천 모임")
    @GetMapping("/recommendation")
    public ApiResponse<PageResponse<PlubbingCardResponse>> getRecommendation(
            @PageableDefault Pageable pageable
    ) {
        return success(plubbingService.getRecommendation(pageable));
    }

    @ApiOperation(value = "카테고리별 모임 조회")
    @GetMapping("/categories/{categoryId}")
    public ApiResponse<PageResponse<PlubbingCardResponse>> getPlubbingByCategory(
            @PathVariable Long categoryId,
            @PageableDefault Pageable pageable,
            @RequestParam("sort") String sort
    ) {
        return success(plubbingService.getPlubbingByCategory(categoryId, pageable, sort));
    }
}