package plub.plubserver.domain.recruit.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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
import plub.plubserver.domain.report.dto.ReportDto;
import plub.plubserver.domain.report.dto.ReportDto.ReportResponse;

import javax.validation.Valid;

import static plub.plubserver.common.dto.ApiResponse.success;

@Slf4j
@RestController
@RequestMapping("/api/plubbings")
@RequiredArgsConstructor
@Api(tags = "모집 API", hidden = true)
public class RecruitController {
    private final RecruitService recruitService;
    private final AccountService accountService;

    @ApiOperation(value = "모집 검색")
    @GetMapping("/recruit")
    public ApiResponse<PageResponse<RecruitCardResponse>> searchRecruit(
            @PageableDefault(sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
            @RequestParam(required = false) Long cursorId,
            @RequestParam String keyword,
            @RequestParam String type,
            @RequestParam String sort
    ) {
        return success(recruitService.search(cursorId, pageable, sort, RecruitSearchType.toType(type), keyword));
    }

    @ApiOperation(value = "모집 상세 조회")
    @GetMapping("/{plubbingId}/recruit")
    public ApiResponse<RecruitResponse> getRecruit(@PathVariable Long plubbingId) {
        return success(recruitService.getRecruit(plubbingId));
    }

    @ApiOperation(value = "질문 전체 조회")
    @GetMapping("/{plubbingId}/recruit/questions")
    public ApiResponse<QuestionListResponse> getRecruitQuestions(
            @PathVariable Long plubbingId
    ) {
        return success(recruitService.getRecruitQuestions(plubbingId));
    }

    @ApiOperation(value = "내 북마크 전체 조회")
    @GetMapping("/recruit/bookmarks/me")
    public ApiResponse<PageResponse<RecruitCardResponse>> getMyBookmarks(
            @PageableDefault(sort = "id", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return success(recruitService.getMyBookmarks(pageable));
    }

    @ApiOperation(value = "북마크 생성")
    @PostMapping("/{plubbingId}/recruit/bookmarks")
    public ApiResponse<BookmarkResponse> bookmark(@PathVariable Long plubbingId) {
        Account loginAccount = accountService.getCurrentAccount();
        return success(recruitService.bookmark(loginAccount, plubbingId));
    }

    @ApiOperation(value = "모집 종료")
    @PutMapping("/{plubbingId}/recruit/end")
    public ApiResponse<RecruitStatusResponse> endRecruit(@PathVariable Long plubbingId) {
        return success(recruitService.endRecruit(plubbingId));
    }

    @ApiOperation(value = "모집 지원")
    @PostMapping("/{plubbingId}/recruit/applicants")
    public ApiResponse<PlubbingIdResponse> applyRecruit(
            @PathVariable Long plubbingId,
            @RequestBody ApplyRecruitRequest applyRecruitRequest
    ) {
        Account loginAccount = accountService.getCurrentAccount();
        return success(recruitService.applyRecruit(loginAccount, plubbingId, applyRecruitRequest));
    }

    @ApiOperation(value = "지원자 전체 조회")
    @GetMapping("/{plubbingId}/recruit/applicants")
    public ApiResponse<AppliedAccountListResponse> getApplicants(
            @PathVariable Long plubbingId
    ) {
        return success(recruitService.getAppliedAccounts(plubbingId));
    }

    @ApiOperation(value = "지원자 승낙")
    @PostMapping("/{plubbingId}/recruit/applicants/{applicantId}/approval")
    public ApiResponse<JoinedAccountsInfoResponse> acceptApplicant(
            @PathVariable("plubbingId") Long plubbingId,
            @PathVariable("applicantId") Long applicantId
    ) {
        Account loginAccount = accountService.getCurrentAccount();
        return success(recruitService.acceptApplicant(loginAccount, plubbingId, applicantId));
    }

    @ApiOperation(value = "지원자 거절")
    @PostMapping("/{plubbingId}/recruit/applicants/{applicantId}/refuse")
    public ApiResponse<JoinedAccountsInfoResponse> rejectApplicant(
            @PathVariable("plubbingId") Long plubbingId,
            @PathVariable("applicantId") Long applicantId
    ) {
        Account loginAccount = accountService.getCurrentAccount();
        return success(recruitService.rejectApplicant(loginAccount, plubbingId, applicantId));
    }
    @ApiOperation(value = "내 모임 지원자 전체 조회")
    @GetMapping("/{plubbingId}/recruit/applicants/my")
    public ApiResponse<AppliedAccountResponse> getMyApplicants(
            @PathVariable Long plubbingId
    ) {
        Account loginAccount = accountService.getCurrentAccount();
        return success(recruitService.getMyAppliedAccount(loginAccount, plubbingId));
    }

    @ApiOperation(value = " 모집 신고")
    @PostMapping("/{plubbingId}/recruit/reports")
    public ApiResponse<ReportResponse> reportRecruit(
            @PathVariable Long plubbingId,
            @Valid @RequestBody ReportDto.CreateReportRequest createReportRequest
    ) {
        Account loginAccount = accountService.getCurrentAccount();
        return success(recruitService.reportRecruit(createReportRequest, loginAccount));
    }

    @ApiOperation(value = "내 지원서 조회")
    @GetMapping("/{plubbingId}/recruit/applicants/me")
    public ApiResponse<RecruitMyApplicationResponse> getMyApplication(
            @PathVariable Long plubbingId
    ) {
        Account loginAccount = accountService.getCurrentAccount();
        return success(recruitService.getMyAppliedRecruits(loginAccount, plubbingId));
    }
}
