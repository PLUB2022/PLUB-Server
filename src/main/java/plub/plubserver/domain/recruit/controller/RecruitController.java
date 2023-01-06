package plub.plubserver.domain.recruit.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import plub.plubserver.common.dto.ApiResponse;
import plub.plubserver.domain.plubbing.dto.PlubbingDto.JoinedAccountsInfoResponse;
import plub.plubserver.domain.recruit.dto.QuestionDto.QuestionListResponse;
import plub.plubserver.domain.recruit.dto.RecruitDto.AppliedAccountListResponse;
import plub.plubserver.domain.recruit.dto.RecruitDto.ApplyRecruitRequest;
import plub.plubserver.domain.recruit.dto.RecruitDto.BookmarkResponse;
import plub.plubserver.domain.recruit.dto.RecruitDto.RecruitResponse;
import plub.plubserver.domain.recruit.service.RecruitService;

import static plub.plubserver.common.dto.ApiResponse.success;

@Slf4j
@RestController
@RequestMapping("/api/recruits")
@RequiredArgsConstructor
public class RecruitController {
    private final RecruitService recruitService;

//    @GetMapping
//    public ApiResponse<?> search(
//            @RequestParam("keyword") String keyword,
//            @PageableDefault
//            @SortDefault.SortDefaults({
//                    @SortDefault(sort = "category"),
//                    @SortDefault(sort = "title"),
//                    @SortDefault(sort = "introduce")
//            })
//            Pageable pageable) {
//        return success(recruitService.search(pageable, keyword));
//    }

    @GetMapping("/{recruitId}")
    public ApiResponse<RecruitResponse> getRecruit(@PathVariable("recruitId") Long recruitId) {
        return success(recruitService.getRecruit(recruitId));
    }

    @GetMapping("/{recruitId}/questions")
    public ApiResponse<QuestionListResponse> getRecruitQuestions(
            @PathVariable("recruitId") Long recruitId
    ) {
        return success(recruitService.getRecruitQuestions(recruitId));
    }

    @PostMapping("/{recruitId}/bookmarks")
    public ApiResponse<BookmarkResponse> bookmarkRecruit(
            @PathVariable("recruitId") Long recruitId
    ) {
        return success(recruitService.bookmark(recruitId));
    }

    // TODO : 모집 글 수정

    @PutMapping("/{recruitId}/done")
    public void doneRecruit(@PathVariable("recruitId") Long recruitId) {
        recruitService.doneRecruit(recruitId);
    }

    @PostMapping("/{recruitId}/applicants")
    public ApiResponse<Long> applyRecruit(
            @PathVariable("recruitId") Long recruitId,
            @RequestBody ApplyRecruitRequest applyRecruitRequest) {
        return success(recruitService.applyRecruit(recruitId, applyRecruitRequest));
    }

    @GetMapping("/{recruitId}/applicants")
    public ApiResponse<AppliedAccountListResponse> getApplicants(
            @PathVariable("recruitId") Long recruitId
    ) {
        return success(recruitService.getAppliedAccounts(recruitId));
    }

    @PostMapping("/{recruitId}/applicants/{applicantId}/approval")
    public ApiResponse<JoinedAccountsInfoResponse> acceptApplicant(
            @PathVariable("recruitId") Long recruitId,
            @PathVariable("applicantId") Long applicantId
    ) {
        return success(recruitService.acceptApplicant(recruitId, applicantId));
    }

    @PostMapping("/{recruitId}/applicants/{applicantId}/refuse")
    public ApiResponse<JoinedAccountsInfoResponse> rejectApplicant(
            @PathVariable("recruitId") Long recruitId,
            @PathVariable("applicantId") Long applicantId
    ) {
        return success(recruitService.rejectApplicant(recruitId, applicantId));
    }
}
