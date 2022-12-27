package plub.plubserver.domain.recruit.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import plub.plubserver.domain.account.model.Account;
import plub.plubserver.domain.account.service.AccountService;
import plub.plubserver.domain.recruit.dto.RecruitDto.AnswerRequest;
import plub.plubserver.domain.recruit.dto.RecruitDto.ApplyRecruitRequest;
import plub.plubserver.domain.recruit.dto.RecruitDto.QuestionResponse;
import plub.plubserver.domain.recruit.dto.RecruitDto.RecruitResponse;
import plub.plubserver.domain.recruit.model.*;
import plub.plubserver.domain.recruit.repository.RecruitRepository;

import java.util.ArrayList;
import java.util.List;


@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RecruitService {
    private final RecruitRepository recruitRepository;
    private final AccountService accountService;

    private Recruit findById(Long recruitId) {
        return recruitRepository.findById(recruitId).orElseThrow();
    }

    public RecruitResponse getRecruit(Long recruitId) {
        return RecruitResponse.of(findById(recruitId));
    }

    public List<QuestionResponse> getRecruitQuestions(Long recruitId) {
        return QuestionResponse.ofList(findById(recruitId).getRecruitQuestionList());
    }

    @Transactional
    public void doneRecruit(Long recruitId) {
        Recruit recruit = findById(recruitId);
        recruit.done();
    }

    // 모집 지원
    @Transactional
    public Long applyRecruit(Long recruitId, ApplyRecruitRequest applyRecruitRequest) {
        Account account = accountService.getCurrentAccount();
        Recruit recruit = findById(recruitId);
        List<RecruitQuestion> questions = recruit.getRecruitQuestionList();

        List<RecruitQuestionAnswer> answers = new ArrayList<>();

        AppliedAccount appliedAccount = AppliedAccount.builder()
                .account(account)
                .recruit(recruit)
                .status(ApplicantStatus.WAITING)
                .build();

        for (AnswerRequest ar : applyRecruitRequest.answers()) {
            RecruitQuestion question = questions.stream()
                    .filter(it -> it.getId().equals(ar.questionId()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("해당 질문이 없습니다."));

            answers.add(RecruitQuestionAnswer.builder()
                    .recruitQuestion(question)
                    .appliedAccount(appliedAccount)
                    .answer(ar.answer())
                    .build());
        }

        appliedAccount.addAnswerList(answers);
        recruit.addAppliedAccount(appliedAccount);
        return recruit.getId();
    }

}
