package plub.plubserver.domain.recruit.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import plub.plubserver.common.dto.ApiResponse;
import plub.plubserver.domain.plubbing.dto.PlubbingDto.JoinedAccountsInfoResponse;
import plub.plubserver.domain.recruit.dto.RecruitDto.AppliedAccountListResponse;
import plub.plubserver.domain.recruit.dto.RecruitDto.ApplyRecruitRequest;
import plub.plubserver.domain.recruit.dto.RecruitDto.QuestionListResponse;
import plub.plubserver.domain.recruit.dto.RecruitDto.RecruitResponse;
import plub.plubserver.domain.recruit.service.RecruitService;

import static plub.plubserver.common.dto.ApiResponse.success;

@Slf4j
@RestController
@RequestMapping("/api/recruits/{recruitId}")
@RequiredArgsConstructor
public class RecruitController {
    private final RecruitService recruitService;

    @GetMapping
    public ApiResponse<RecruitResponse> getRecruit(@PathVariable("recruitId") Long recruitId) {
        return success(recruitService.getRecruit(recruitId));
    }

    @GetMapping("/questions")
    public ApiResponse<QuestionListResponse> getRecruitQuestions(
            @PathVariable("recruitId") Long recruitId
    ) {
        return success(recruitService.getRecruitQuestions(recruitId));
    }

    // TODO : 모집 글 북마크
    // TODO : 모집 글 수정

    @PutMapping("/done")
    public void doneRecruit(@PathVariable("recruitId") Long recruitId) {
        recruitService.doneRecruit(recruitId);
    }

    @PostMapping("/applicants")
    public ApiResponse<Long> applyRecruit(
            @PathVariable("recruitId") Long recruitId,
            @RequestBody ApplyRecruitRequest applyRecruitRequest) {
        return success(recruitService.applyRecruit(recruitId, applyRecruitRequest));
    }

    @GetMapping("/applicants")
    public ApiResponse<AppliedAccountListResponse> getApplicants(
            @PathVariable("recruitId") Long recruitId
    ) {
        return success(recruitService.getAppliedAccounts(recruitId));
    }

    @PostMapping("/applicants/{applicantId}/approval")
    public ApiResponse<JoinedAccountsInfoResponse> acceptApplicant(
            @PathVariable("recruitId") Long recruitId,
            @PathVariable("applicantId") Long applicantId
    ) {
        return success(recruitService.acceptApplicant(recruitId, applicantId));
    }

    @PostMapping("/applicants/{applicantId}/refuse")
    public ApiResponse<JoinedAccountsInfoResponse> rejectApplicant(
            @PathVariable("recruitId") Long recruitId,
            @PathVariable("applicantId") Long applicantId
    ) {
        return success(recruitService.rejectApplicant(recruitId, applicantId));
    }
}
