package plub.plubserver.domain.recruit.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;
import plub.plubserver.common.dto.ApiResponse;
import plub.plubserver.common.dto.PageResponse;
import plub.plubserver.domain.account.model.Account;
import plub.plubserver.domain.account.service.AccountService;
import plub.plubserver.domain.plubbing.dto.PlubbingDto.JoinedAccountsInfoResponse;
import plub.plubserver.domain.plubbing.dto.PlubbingDto.PlubbingIdResponse;
import plub.plubserver.domain.recruit.dto.QuestionDto.QuestionListResponse;
import plub.plubserver.domain.recruit.dto.RecruitDto.*;
import plub.plubserver.domain.recruit.model.RecruitSearchType;
import plub.plubserver.domain.recruit.service.RecruitService;

import static plub.plubserver.common.dto.ApiResponse.success;

@Slf4j
@RestController
@RequestMapping("/api/plubbings")
@RequiredArgsConstructor
public class RecruitController {
    private final RecruitService recruitService;
    private final AccountService accountService;

    @GetMapping("/recruit")
    public ApiResponse<PageResponse<RecruitCardResponse>> searchRecruit(
            @PageableDefault(sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
            @RequestParam("keyword") String keyword,
            @RequestParam("type") String type,
            @RequestParam("sort") String sort
    ) {
        return success(recruitService.search(pageable, sort, RecruitSearchType.toType(type), keyword));
    }

    @GetMapping("/{plubbingId}/recruit")
    public ApiResponse<RecruitResponse> getRecruit(@PathVariable Long plubbingId) {
        return success(recruitService.getRecruit(plubbingId));
    }

    @GetMapping("/{plubbingId}/recruit/questions")
    public ApiResponse<QuestionListResponse> getRecruitQuestions(
            @PathVariable Long plubbingId
    ) {
        return success(recruitService.getRecruitQuestions(plubbingId));
    }

    @PostMapping("/{plubbingId}/recruit/bookmarks")
    public ApiResponse<BookmarkResponse> bookmark(@PathVariable Long plubbingId) {
        Account loginAccount = accountService.getCurrentAccount();
        return success(recruitService.bookmark(loginAccount, plubbingId));
    }

    @PutMapping("/{plubbingId}/recruit/end")
    public ApiResponse<RecruitStatusResponse> endRecruit(@PathVariable Long plubbingId) {
        return success(recruitService.endRecruit(plubbingId));
    }

    @PostMapping("/{plubbingId}/recruit/applicants")
    public ApiResponse<PlubbingIdResponse> applyRecruit(
            @PathVariable Long plubbingId,
            @RequestBody ApplyRecruitRequest applyRecruitRequest) {
        return success(recruitService.applyRecruit(plubbingId, applyRecruitRequest));
    }

    @GetMapping("/{plubbingId}/recruit/applicants")
    public ApiResponse<AppliedAccountListResponse> getApplicants(
            @PathVariable Long plubbingId
    ) {
        return success(recruitService.getAppliedAccounts(plubbingId));
    }

    @PostMapping("/{plubbingId}/recruit/applicants/{applicantId}/approval")
    public ApiResponse<JoinedAccountsInfoResponse> acceptApplicant(
            @PathVariable("plubbingId") Long plubbingId,
            @PathVariable("applicantId") Long applicantId
    ) {
        return success(recruitService.acceptApplicant(plubbingId, applicantId));
    }

    @PostMapping("/{plubbingId}/recruit/applicants/{applicantId}/refuse")
    public ApiResponse<JoinedAccountsInfoResponse> rejectApplicant(
            @PathVariable("plubbingId") Long plubbingId,
            @PathVariable("applicantId") Long applicantId
    ) {
        return success(recruitService.rejectApplicant(plubbingId, applicantId));
    }
}
