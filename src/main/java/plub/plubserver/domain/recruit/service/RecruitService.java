package plub.plubserver.domain.recruit.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import plub.plubserver.common.dto.PageResponse;
import plub.plubserver.common.model.SortType;
import plub.plubserver.domain.account.config.AccountCode;
import plub.plubserver.domain.account.exception.AccountException;
import plub.plubserver.domain.account.model.Account;
import plub.plubserver.domain.account.service.AccountService;
import plub.plubserver.domain.plubbing.dto.PlubbingDto.JoinedAccountsInfoResponse;
import plub.plubserver.domain.plubbing.dto.PlubbingDto.PlubbingIdResponse;
import plub.plubserver.domain.plubbing.model.AccountPlubbing;
import plub.plubserver.domain.plubbing.model.AccountPlubbingStatus;
import plub.plubserver.domain.plubbing.model.Plubbing;
import plub.plubserver.domain.plubbing.repository.AccountPlubbingRepository;
import plub.plubserver.domain.plubbing.service.PlubbingService;
import plub.plubserver.domain.recruit.config.RecruitCode;
import plub.plubserver.domain.recruit.dto.QuestionDto.AnswerRequest;
import plub.plubserver.domain.recruit.dto.QuestionDto.QuestionListResponse;
import plub.plubserver.domain.recruit.dto.QuestionDto.QuestionResponse;
import plub.plubserver.domain.recruit.dto.RecruitDto.*;
import plub.plubserver.domain.recruit.exception.RecruitException;
import plub.plubserver.domain.recruit.model.*;
import plub.plubserver.domain.recruit.repository.AppliedAccountRepository;
import plub.plubserver.domain.recruit.repository.RecruitRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RecruitService {
    private final AccountService accountService;
    private final AccountPlubbingRepository accountPlubbingRepository;
    private final AppliedAccountRepository appliedAccountRepository;
    private final PlubbingService plubbingService;
    private final RecruitRepository recruitRepository;

    private Recruit getRecruitByPlubbingId(Long plubbingId) {
        return plubbingService.getPlubbing(plubbingId).getRecruit();
    }

    /**
     * 조회
     */
    @Transactional
    public RecruitResponse getRecruit(Long plubbingId) {
        Account account = accountService.getCurrentAccount();
        Recruit recruit = getRecruitByPlubbingId(plubbingId);
        boolean isApplied = appliedAccountRepository.existsByAccountAndRecruit(account, recruit);
        boolean isBookmarked = account.getBookmarkList().stream()
                .anyMatch(it -> it.getRecruit().equals(recruit));
        recruit.plusView();
        return RecruitResponse.of(recruit, isApplied, isBookmarked);
    }

    public QuestionListResponse getRecruitQuestions(Long plubbingId) {
        return new QuestionListResponse(
                QuestionResponse.listOf(getRecruitByPlubbingId(plubbingId).getRecruitQuestionList())
        );
    }

    public AppliedAccountListResponse getAppliedAccounts(Long plubbingId) {
        return new AppliedAccountListResponse(
                getRecruitByPlubbingId(plubbingId).getAppliedAccountList().stream()
                        .map(AppliedAccountResponse::of)
                        .toList()
        );
    }

    /**
     * 모집글 검색
     */
    public PageResponse<RecruitCardResponse> search(
            Pageable pageable,
            String sort,
            RecruitSearchType type,
            String keyword
    ) {

        Page<Recruit> recruitPage = recruitRepository.search(
                pageable,
                SortType.of(sort),
                type,
                keyword
        );
        Account account = accountService.getCurrentAccount();

        // 북마크 여부 체크해서 DTO로 반환
        List<Long> bookmarkedPlubbingIds = account.getBookmarkList().stream()
                .map(it -> it.getRecruit().getPlubbing().getId())
                .toList();

        List<RecruitCardResponse> cardList = recruitPage.map(it -> {
            boolean isBookmarked = bookmarkedPlubbingIds.contains(it.getPlubbing().getId());
            return RecruitCardResponse.of(it, isBookmarked);
        }).toList();

        return PageResponse.of(pageable, cardList);
    }

    /**
     * 북마크
     */
    // 등록, 취소
    @Transactional
    public BookmarkResponse bookmark(Account account, Long plubbingId) {
        Recruit recruit = getRecruitByPlubbingId(plubbingId);
        Optional<Bookmark> bookmark = account.getBookmarkList().stream()
                .filter(b -> b.getRecruit().equals(recruit))
                .findFirst();
        boolean isBookmarked;
        if (bookmark.isEmpty()) {
            // 북마크가 되어있지 않으면 등록
            account.addBookmark(Bookmark.builder()
                    .account(account).recruit(recruit)
                    .build());
            isBookmarked = true;
        } else {
            // 북마크 취소
            account.removeBookmark(bookmark.get());
            isBookmarked = false;
        }
        return BookmarkResponse.builder()
                .isBookmarked(isBookmarked)
                .plubbingId(plubbingId)
                .build();
    }

    // 내 북마크 전체 조회
    public PageResponse<RecruitCardResponse> getMyBookmarks(Pageable pageable) {
        Account account = accountService.getCurrentAccount();
        List<Bookmark> bookmarkList = account.getBookmarkList();
        List<RecruitCardResponse> cardList = bookmarkList.stream()
                .map(it -> RecruitCardResponse.of(it.getRecruit(), true))
                .toList();

        return PageResponse.of(pageable, cardList);
    }


    /**
     * 모집 종료
     */
    @Transactional
    public RecruitStatusResponse endRecruit(Long plubbingId) {
        Recruit recruit = getRecruitByPlubbingId(plubbingId);
        plubbingService.checkHost(recruit.getPlubbing());
        recruit.done();
        return RecruitStatusResponse.of(recruit);
    }

    /**
     * 모집 지원
     */
    @Transactional
    public PlubbingIdResponse applyRecruit(Long plubbingId, ApplyRecruitRequest applyRecruitRequest) {
        Account account = accountService.getCurrentAccount();
        Recruit recruit = getRecruitByPlubbingId(plubbingId);

        // 호스트 본인은 지원 불가 처리
        accountPlubbingRepository.findByAccount(account).ifPresent(accountPlubbing -> {
            if (accountPlubbing.isHost())
                throw new RecruitException(RecruitCode.HOST_RECRUIT_ERROR);
        });

        // 이미 지원했는지 확인
        if (appliedAccountRepository.existsByAccountAndRecruit(account, recruit))
            throw new RecruitException(RecruitCode.ALREADY_APPLIED_RECRUIT);

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
        return new PlubbingIdResponse(plubbingId);
    }

    // 지원자 찾기
    private AppliedAccount findAppliedAccount(Long plubbingId, Long accountId) {
        Recruit recruit = getRecruitByPlubbingId(plubbingId);

        plubbingService.checkHost(recruit.getPlubbing());

        // recruit 에서 지원자를 찾기
        return appliedAccountRepository
                .findByAccountIdAndRecruitId(accountId, plubbingId)
                .orElseThrow(() -> new AccountException(AccountCode.NOT_FOUND_ACCOUNT));
    }


    /**
     * 지원자 승낙
     */
    @Transactional
    public JoinedAccountsInfoResponse acceptApplicant(Long plubbingId, Long accountId) {
        Plubbing plubbing = plubbingService.getPlubbing(plubbingId);
        AppliedAccount appliedAccount = findAppliedAccount(plubbingId, accountId);

        if (appliedAccount.getStatus().equals(ApplicantStatus.ACCEPTED) ||
                appliedAccount.getStatus().equals(ApplicantStatus.REJECTED))
            throw new RecruitException(RecruitCode.ALREADY_ACCEPTED);
        appliedAccount.accept();

        // 모임에 해당 지원자 추가
        AccountPlubbing accountPlubbing = AccountPlubbing.builder()
                .account(appliedAccount.getAccount())
                .plubbing(plubbing)
                .accountPlubbingStatus(AccountPlubbingStatus.ACTIVE)
                .isHost(false)
                .build();

        plubbing.addAccountPlubbing(accountPlubbing);

        // 명시적 호출을 해야지만 반영 됨...
        plubbing.updateCurAccountNum();

        return JoinedAccountsInfoResponse.of(plubbing);
    }

    /**
     * 지원자 거절
     */
    @Transactional
    public JoinedAccountsInfoResponse rejectApplicant(Long plubbingId, Long accountId) {
        AppliedAccount appliedAccount = findAppliedAccount(plubbingId, accountId);

        if (appliedAccount.getStatus().equals(ApplicantStatus.REJECTED) ||
                appliedAccount.getStatus().equals(ApplicantStatus.ACCEPTED))
            throw new RecruitException(RecruitCode.ALREADY_REJECTED);
        appliedAccount.reject();

        Plubbing plubbing = plubbingService.getPlubbing(plubbingId);
        plubbing.updateCurAccountNum();

        return JoinedAccountsInfoResponse.of(plubbing);
    }
}
