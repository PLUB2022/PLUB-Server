package plub.plubserver.domain.recruit.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import plub.plubserver.common.dto.ApiResponse;
import plub.plubserver.domain.plubbing.dto.PlubbingDto.JoinedAccountsInfoResponse;
import plub.plubserver.domain.plubbing.dto.PlubbingDto.PlubbingIdResponse;
import plub.plubserver.domain.recruit.dto.QuestionDto.QuestionListResponse;
import plub.plubserver.domain.recruit.dto.RecruitDto.*;
import plub.plubserver.domain.recruit.service.RecruitService;

import static plub.plubserver.common.dto.ApiResponse.success;

@Slf4j
@RestController
@RequestMapping("/api/plubbings/{plubbingId}/recruit")
@RequiredArgsConstructor
public class RecruitController {
    private final RecruitService recruitService;

    @GetMapping
    public ApiResponse<RecruitResponse> getRecruit(@PathVariable Long plubbingId) {
        return success(recruitService.getRecruit(plubbingId));
    }

    @GetMapping("/questions")
    public ApiResponse<QuestionListResponse> getRecruitQuestions(
            @PathVariable Long plubbingId
    ) {
        return success(recruitService.getRecruitQuestions(plubbingId));
    }

    @PostMapping("/bookmarks")
    public ApiResponse<BookmarkResponse> bookmark(@PathVariable Long plubbingId) {
        return success(recruitService.bookmark(plubbingId));
    }

    @PutMapping("/end")
    public ApiResponse<RecruitStatusResponse> endRecruit(@PathVariable Long plubbingId) {
        return success(recruitService.endRecruit(plubbingId));
    }

    @PostMapping("/applicants")
    public ApiResponse<PlubbingIdResponse> applyRecruit(
            @PathVariable Long plubbingId,
            @RequestBody ApplyRecruitRequest applyRecruitRequest) {
        return success(recruitService.applyRecruit(plubbingId, applyRecruitRequest));
    }

    @GetMapping("/applicants")
    public ApiResponse<AppliedAccountListResponse> getApplicants(
            @PathVariable Long plubbingId
    ) {
        return success(recruitService.getAppliedAccounts(plubbingId));
    }

    @PostMapping("/applicants/{applicantId}/approval")
    public ApiResponse<JoinedAccountsInfoResponse> acceptApplicant(
            @PathVariable("plubbingId") Long plubbingId,
            @PathVariable("applicantId") Long applicantId
    ) {
        return success(recruitService.acceptApplicant(plubbingId, applicantId));
    }

    @PostMapping("/applicants/{applicantId}/refuse")
    public ApiResponse<JoinedAccountsInfoResponse> rejectApplicant(
            @PathVariable("plubbingId") Long plubbingId,
            @PathVariable("applicantId") Long applicantId
    ) {
        return success(recruitService.rejectApplicant(plubbingId, applicantId));
    }
}
