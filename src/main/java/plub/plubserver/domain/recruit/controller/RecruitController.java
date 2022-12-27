package plub.plubserver.domain.recruit.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import plub.plubserver.domain.recruit.dto.RecruitDto.ApplyRecruitRequest;
import plub.plubserver.domain.recruit.service.RecruitService;

@Slf4j
@RestController
@RequestMapping("/api/recruits/{recruitId}")
@RequiredArgsConstructor
public class RecruitController {
    private final RecruitService recruitService;

    @GetMapping
    public void getRecruit(@PathVariable("recruitId") Long recruitId) {
        recruitService.getRecruit(recruitId);
    }

    @GetMapping("/questions")
    public void getRecruitQuestions(@PathVariable("recruitId") Long recruitId) {
        recruitService.getRecruitQuestions(recruitId);
    }

    // TODO : 모집 글 북마크
    // TODO : 모집 글 수정

    @PutMapping("/done")
    public void doneRecruit(@PathVariable("recruitId") Long recruitId) {
        recruitService.doneRecruit(recruitId);
    }

    @PostMapping("/applicants")
    public Long applyRecruit(
            @PathVariable("recruitId") Long recruitId,
            @RequestBody ApplyRecruitRequest applyRecruitRequest) {
        return recruitService.applyRecruit(recruitId, applyRecruitRequest);
    }
//
//    @GetMapping("/applicants")
//    public void getApplicants(@PathVariable("recruitId") Long recruitId) {
//        recruitService.getApplicants(recruitId);
//    }
//
//    @PostMapping("/applicants/{applicantId}/approval")
//    public void acceptApplicant(@PathVariable("recruitId") Long recruitId, @PathVariable("applicantId") Long applicantId) {
//        recruitService.acceptApplicant(recruitId, applicantId);
//    }
//
//    @PostMapping("/applicants/{applicantId}/refuse")
//    public void rejectApplicant(@PathVariable("recruitId") Long recruitId, @PathVariable("applicantId") Long applicantId) {
//        recruitService.rejectApplicant(recruitId, applicantId);
//    }
}
