package plub.plubserver.domain.recruit.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import plub.plubserver.domain.account.config.AccountCode;
import plub.plubserver.domain.account.exception.AccountException;
import plub.plubserver.domain.account.model.Account;
import plub.plubserver.domain.account.service.AccountService;
import plub.plubserver.domain.plubbing.config.PlubbingCode;
import plub.plubserver.domain.plubbing.dto.PlubbingDto.JoinedAccountsInfoResponse;
import plub.plubserver.domain.plubbing.exception.PlubbingException;
import plub.plubserver.domain.plubbing.model.AccountPlubbing;
import plub.plubserver.domain.plubbing.model.AccountPlubbingStatus;
import plub.plubserver.domain.plubbing.model.Plubbing;
import plub.plubserver.domain.plubbing.repository.AccountPlubbingRepository;
import plub.plubserver.domain.recruit.config.RecruitCode;
import plub.plubserver.domain.recruit.dto.RecruitDto.AnswerRequest;
import plub.plubserver.domain.recruit.dto.RecruitDto.ApplyRecruitRequest;
import plub.plubserver.domain.recruit.dto.RecruitDto.QuestionResponse;
import plub.plubserver.domain.recruit.dto.RecruitDto.RecruitResponse;
import plub.plubserver.domain.recruit.exception.RecruitException;
import plub.plubserver.domain.recruit.model.*;
import plub.plubserver.domain.recruit.repository.AppliedAccountRepository;
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
    private final AccountPlubbingRepository accountPlubbingRepository;
    private final AppliedAccountRepository appliedAccountRepository;

    private Recruit findById(Long recruitId) {
        return recruitRepository.findById(recruitId)
                .orElseThrow(() -> new RecruitException(RecruitCode.NOT_FOUND_RECRUIT));
    }

    public RecruitResponse getRecruit(Long recruitId) {
        return RecruitResponse.of(findById(recruitId));
    }

    public List<QuestionResponse> getRecruitQuestions(Long recruitId) {
        return QuestionResponse.ofList(findById(recruitId).getRecruitQuestionList());
    }

    // 모집 종료
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

        // 호스트 본인은 지원 불가 처리
        accountPlubbingRepository.findByAccount(account).ifPresent(accountPlubbing -> {
            if (accountPlubbing.isHost())
                throw new RecruitException(RecruitCode.HOST_RECRUIT_ERROR);
        });
//        account.getAccountPlubbingList().stream()
//                .filter(it -> it.getPlubbing().getId().equals(recruit.getPlubbing().getId()))
//                .filter(AccountPlubbing::isHost)
//                .findAny()
//                .ifPresent(accountPlubbing -> {
//                    throw new RecruitException(RecruitCode.HOST_RECRUIT_ERROR);
//                });

        // 이미 지원했는지 확인
        if (appliedAccountRepository.existsByAccountAndRecruit(account, recruit))
            throw new RecruitException(RecruitCode.ALREADY_APPLIED_RECRUIT);
//        recruit.getAppliedAccountList().stream()
//                .filter(it -> it.getAccount().getId().equals(account.getId()))
//                .findAny()
//                .ifPresent(appliedAccount -> {
//                    throw new RecruitException(RecruitCode.ALREADY_APPLIED_RECRUIT);
//                });

        // 지원자 생성
        AppliedAccount appliedAccount = AppliedAccount.builder()
                .account(account)
                .recruit(recruit)
                .status(ApplicantStatus.WAITING)
                .build();

        // 질문 답변 매핑
        List<RecruitQuestion> questions = recruit.getRecruitQuestionList();
        List<RecruitQuestionAnswer> answers = new ArrayList<>();
        for (AnswerRequest ar : applyRecruitRequest.answers()) {
            RecruitQuestion question = questions.stream()
                    .filter(it -> it.getId().equals(ar.questionId()))
                    .findFirst()
                    .orElseThrow(() -> new RecruitException(RecruitCode.NOT_FOUND_QUESTION));

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

    /**
     * 지원자 승낙
     */
    @Transactional
    public JoinedAccountsInfoResponse acceptApplicant(Long recruitId, Long accountId) {
        Account account = accountService.getCurrentAccount();
        Recruit recruit = findById(recruitId);

        // host 가 아니면 예외 처리
        accountPlubbingRepository.findByAccount(account).ifPresent(accountPlubbing -> {
            if (!accountPlubbing.isHost())
                throw new PlubbingException(PlubbingCode.NOT_HOST);
        });

        // recruit 에서 지원자를 찾기
        AppliedAccount appliedAccount = appliedAccountRepository
                .findByAccountIdAndRecruitId(accountId, recruitId)
                .orElseThrow(() -> new AccountException(AccountCode.NOT_FOUND_ACCOUNT));
//        AppliedAccount appliedAccount = recruit.getAppliedAccountList().stream()
//                .filter(it -> it.getAccount().getId().equals(accountId))
//                .findFirst()
//                .orElseThrow(() -> new AccountException(AccountCode.NOT_FOUND_ACCOUNT));

        // 지원자 승낙
        if (appliedAccount.getStatus().equals(ApplicantStatus.ACCEPTED))
            throw new RecruitException(RecruitCode.ALREADY_ACCEPTED_APPLICANT);
        appliedAccount.accept();

        // 모임에 해당 지원자 추가
        AccountPlubbing accountPlubbing = AccountPlubbing.builder()
                .account(appliedAccount.getAccount())
                .plubbing(recruit.getPlubbing())
                .accountPlubbingStatus(AccountPlubbingStatus.ACTIVE)
                .isHost(false)
                .build();

        Plubbing plubbing = recruit.getPlubbing();
        plubbing.addAccountPlubbing(accountPlubbing);

        // 명시적 호출을 해야지만 반영 됨...
        plubbing.updateCurAccountNum();

        return JoinedAccountsInfoResponse.builder()
                .maxAccountNum(plubbing.getMaxAccountNum())
                .curAccountNum(plubbing.getCurAccountNum())
                .build();
    }

}
