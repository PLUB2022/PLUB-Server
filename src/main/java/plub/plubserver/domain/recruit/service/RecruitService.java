package plub.plubserver.domain.recruit.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import plub.plubserver.common.dto.PageResponse;
import plub.plubserver.common.exception.StatusCode;
import plub.plubserver.common.model.SortType;
import plub.plubserver.domain.account.model.Account;
import plub.plubserver.domain.account.service.AccountService;
import plub.plubserver.domain.feed.service.FeedService;
import plub.plubserver.domain.notification.dto.NotificationDto.NotifyParams;
import plub.plubserver.domain.notification.model.NotificationType;
import plub.plubserver.domain.notification.service.NotificationService;
import plub.plubserver.domain.plubbing.dto.PlubbingDto.JoinedAccountsInfoResponse;
import plub.plubserver.domain.plubbing.dto.PlubbingDto.PlubbingIdResponse;
import plub.plubserver.domain.plubbing.exception.PlubbingException;
import plub.plubserver.domain.plubbing.model.AccountPlubbing;
import plub.plubserver.domain.plubbing.model.AccountPlubbingStatus;
import plub.plubserver.domain.plubbing.model.Plubbing;
import plub.plubserver.domain.plubbing.model.PlubbingStatus;
import plub.plubserver.domain.plubbing.repository.AccountPlubbingRepository;
import plub.plubserver.domain.plubbing.service.PlubbingService;
import plub.plubserver.domain.recruit.dto.QuestionDto.AnswerRequest;
import plub.plubserver.domain.recruit.dto.QuestionDto.QuestionListResponse;
import plub.plubserver.domain.recruit.dto.QuestionDto.QuestionResponse;
import plub.plubserver.domain.recruit.dto.RecruitDto.*;
import plub.plubserver.domain.recruit.exception.RecruitException;
import plub.plubserver.domain.recruit.model.*;
import plub.plubserver.domain.recruit.repository.AppliedAccountRepository;
import plub.plubserver.domain.recruit.repository.BookmarkRepository;
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
    private final NotificationService notificationService;
    private final FeedService feedService;
    private final BookmarkRepository bookmarkRepository;

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
                appliedAccountRepository.findAllWaitings(plubbingId).stream()
                        .map(AppliedAccountResponse::of)
                        .toList()
        );
    }

    // 가져올때 상태값 체크
    public AppliedAccountResponse getMyAppliedAccount(Account account, Long plubbingId) {
        Recruit recruit = getRecruitByPlubbingId(plubbingId);
        return AppliedAccountResponse.of(
                appliedAccountRepository.findByAccountAndRecruit(account, recruit)
                        .orElseThrow(() -> new RecruitException(StatusCode.NOT_APPLIED_RECRUIT))
        );
    }

    // 내 지원서 글 조회
    public RecruitMyApplicationResponse getMyRecruitApplication(Account account, Long plubbingId) {
        Recruit recruit = getRecruitByPlubbingId(plubbingId);
        AppliedAccount appliedAccount = appliedAccountRepository
                .findByAccountIdAndRecruitId(account.getId(), recruit.getId())
                .orElseThrow(() -> new RecruitException(StatusCode.NOT_APPLIED_RECRUIT));
        return RecruitMyApplicationResponse.of(appliedAccount);
    }

    /**
     * 모집글 검색
     */
    public PageResponse<RecruitCardResponse> search(
            Long cursorId,
            Pageable pageable,
            String sort,
            RecruitSearchType type,
            String keyword
    ) {
        Account account = accountService.getCurrentAccount();
        // 북마크 여부 체크해서 DTO로 반환
        List<Long> bookmarkedRecruitIds = recruitRepository
                .findAllBookmarkedRecruitIdByAccountId(account.getId());
        Page<RecruitCardResponse> searchResult = recruitRepository.search(
                cursorId,
                pageable,
                SortType.of(sort),
                type,
                keyword
        ).map(it -> {
            boolean isBookmarked = bookmarkedRecruitIds.contains(it.getPlubbing().getId());
            return RecruitCardResponse.of(it, isBookmarked);
        });
        Long totalElements = recruitRepository.countAllBySearch(type, keyword);
        return PageResponse.ofCursor(searchResult, totalElements);
    }

    /**
     * 북마크
     */
    // 등록, 취소
    @Transactional
    public BookmarkResponse bookmark(Account loginAccount, Long plubbingId) {
        // 영속성 컨텍스트로 다시 불러오기 - no session lazy 예외 핸들용
        Account account = accountService.getAccount(loginAccount.getId());
        Recruit recruit = getRecruitByPlubbingId(plubbingId);

        // 모임 상태 체크
        if (!recruit.getPlubbing().getStatus().equals(PlubbingStatus.ACTIVE))
            throw new PlubbingException(StatusCode.DELETED_STATUS_PLUBBING);

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
    public PageResponse<RecruitCardResponse> getMyBookmarks(Pageable pageable, Long cursorId) {
        Account account = accountService.getCurrentAccount();
        Page<RecruitCardResponse> page = bookmarkRepository
                .findAllByAccountId(account.getId(), cursorId, pageable)
                .map(it -> RecruitCardResponse.of(it.getRecruit(), true));
        Long totalElements = bookmarkRepository.countAllByAccountId(account.getId());
        return PageResponse.ofCursor(page, totalElements);
    }


    /**
     * 모집 종료
     */
    @Transactional
    public RecruitStatusResponse endRecruit(Long plubbingId) {
        Recruit recruit = getRecruitByPlubbingId(plubbingId);
        plubbingService.checkHost(recruit.getPlubbing());
        recruit.done();
        // 지원했던 지원자들 정보 초기화
        //appliedAccountRepository.deleteAllByRecruitId(recruit.getId());
        // 북마크 전체 삭제
        bookmarkRepository.deleteByRecruit(recruit);
        return RecruitStatusResponse.of(recruit);
    }

    /**
     * 모집 지원
     */
    @Transactional
    public PlubbingIdResponse applyRecruit(
            Account loginAccount,
            Long plubbingId,
            ApplyRecruitRequest applyRecruitRequest
    ) {
        Recruit recruit = getRecruitByPlubbingId(plubbingId);

        // TODO : 모집글 상태 체크

        // 호스트 본인은 지원 불가 처리
        accountPlubbingRepository.findByAccountAndPlubbing(loginAccount, recruit.getPlubbing())
                .ifPresent(accountPlubbing -> {
                    if (accountPlubbing.isHost())
                        throw new RecruitException(StatusCode.HOST_RECRUIT_ERROR);
                });

        // 이미 지원했는지 확인
        if (appliedAccountRepository.existsByAccountAndRecruit(loginAccount, recruit))
            throw new RecruitException(StatusCode.ALREADY_APPLIED_RECRUIT);

        // TODO : 내가 모임 세개인지아닌지 체크해서 예외던지는 로직 추가

        // 모임 인원이 꽉 찼는지 확인
        Plubbing plubbing = recruit.getPlubbing();
        //if (plubbing.getMaxAccountNum() < plubbing.getCurAccountNum() + 1)
        //    throw new RecruitException(StatusCode.PLUBBING_MEMBER_FULL);

        // 지원자 생성
        AppliedAccount appliedAccount = AppliedAccount.builder()
                .account(loginAccount)
                .recruit(recruit)
                .status(ApplicantStatus.WAITING)
                .build();

        // 질문 답변 매핑
        setRecruitQuestionAnswers(applyRecruitRequest, recruit, appliedAccount);

        // 지원자 추가 및 모집글 지원자 수 증가
        recruit.addAppliedAccount(appliedAccount);

        // 호스트에게 푸시 알림
        NotifyParams params = NotifyParams.builder()
                .receiver(plubbing.getHost())
                .type(NotificationType.APPLY_RECRUIT)
                .redirectTargetId(0L)
                .title(plubbing.getName())
                .content(plubbing.getName() + "에 새로운 지원자가 있어요! \n지원서를 확인하러 가볼까요? \uD83E\uDD29") // 별눈 이모지
                .build();
        notificationService.pushMessage(params);
        return PlubbingIdResponse.of(plubbingId);
    }

    /**
     * 질문 답변 매핑
     * 사용처 : 모집 지원, 지원 수정 (모집글 질문 답변 수정)
     */
    private static void setRecruitQuestionAnswers(
            ApplyRecruitRequest applyRecruitRequest,
            Recruit recruit,
            AppliedAccount appliedAccount
    ) {
        List<RecruitQuestion> questions = recruit.getRecruitQuestionList();
        List<RecruitQuestionAnswer> answers = new ArrayList<>();
        for (AnswerRequest ar : applyRecruitRequest.answers()) {
            RecruitQuestion question = questions.stream()
                    .filter(it -> it.getId().equals(ar.questionId()))
                    .findFirst()
                    .orElseThrow(() -> new RecruitException(StatusCode.NOT_FOUND_QUESTION));

            answers.add(RecruitQuestionAnswer.builder()
                    .recruitQuestion(question)
                    .appliedAccount(appliedAccount)
                    .answer(ar.answer())
                    .build());
        }
        appliedAccount.addAnswerList(answers);
    }

    /**
     * 지원 수정 (질문글 답변 수정)
     */
    @Transactional
    public PlubbingIdResponse updateRecruitQuestionAnswers(
            Long plubbingId,
            ApplyRecruitRequest newApplyRecruitRequest
    ) {
        Account loginAccount = accountService.getCurrentAccount();
        Recruit recruit = getRecruitByPlubbingId(plubbingId);
        // 질문에 새로운 답변 다시 매핑
        setRecruitQuestionAnswers(
                newApplyRecruitRequest,
                recruit,
                getAppliedAccount(loginAccount, recruit)
        );
        return PlubbingIdResponse.of(plubbingId);
    }

    /**
     * 지원 취소 (지원 삭제)
     */
    @Transactional
    public CancelApplyResponse cancelApply(Account account, Long plubbingId) {
        Recruit recruit = getRecruitByPlubbingId(plubbingId);
        AppliedAccount appliedAccount = getAppliedAccount(account, recruit);
        appliedAccountRepository.delete(appliedAccount);
        return CancelApplyResponse.of(appliedAccount);
    }

    // 호스트 여부 체크하면서 지원자 찾기
    private AppliedAccount getAppliedAccountWithCheckHost(
            Account loginAccount, Long plubbingId, Long accountId) {
        // loginAccount : 현재 로그인된 사용자 (API 호출 주체)
        // accountId : 지원자인지 검사하고싶은 회원 ID
        Recruit recruit = getRecruitByPlubbingId(plubbingId);

        // 로그인한 사용자가 호스트인지 검사
        plubbingService.checkHost(loginAccount, recruit.getPlubbing());

        // 지원자 찾기
        return getAppliedAccount(accountService.getAccount(accountId), recruit);
    }

    // 지원자 찾기
    private AppliedAccount getAppliedAccount(Account account, Recruit recruit) {
        return appliedAccountRepository
                .findByAccountIdAndRecruitId(account.getId(), recruit.getId())
                .orElseThrow(() -> new RecruitException(StatusCode.NOT_APPLIED_RECRUIT));
    }


    /**
     * 지원자 승낙
     */
    @Transactional
    public JoinedAccountsInfoResponse acceptApplicant(Account loginAccount, Long plubbingId, Long accountId) {
        Plubbing plubbing = plubbingService.getPlubbing(plubbingId);
        AppliedAccount appliedAccount = getAppliedAccountWithCheckHost(loginAccount, plubbingId, accountId);

        if (appliedAccount.getStatus().equals(ApplicantStatus.ACCEPTED) ||
                appliedAccount.getStatus().equals(ApplicantStatus.REJECTED))
            throw new RecruitException(StatusCode.ALREADY_ACCEPTED);
        appliedAccount.accept();

        // 모임에 해당 지원자 추가
        AccountPlubbing accountPlubbing = AccountPlubbing.builder()
                .account(appliedAccount.getAccount())
                .plubbing(plubbing)
                .accountPlubbingStatus(AccountPlubbingStatus.ACTIVE)
                .isHost(false)
                .build();

        plubbing.addAccountPlubbing(accountPlubbing);

        // 명시적 호출을 해야지만 반영 됨
        plubbing.updateCurAccountNum();

        feedService.createSystemFeed(plubbing, appliedAccount.getAccount().getNickname());

        // 지원자에게 푸시 알림
        NotifyParams params = NotifyParams.builder()
                .receiver(accountService.getAccount(accountId))
                .type(NotificationType.APPROVE_RECRUIT)
                .redirectTargetId(plubbingId)
                .title(plubbing.getName())
                .content(plubbing.getName() + "과 함께하게 되었어요! \n멤버들과 함께 즐겁고 유익한 시간 보내시길 바라요 \uD83D\uDE42") // 웃음 이모지
                .build();
        notificationService.pushMessage(params);
        return JoinedAccountsInfoResponse.of(plubbing);
    }

    /**
     * 지원자 거절
     */
    @Transactional
    public JoinedAccountsInfoResponse rejectApplicant(Account loginAccount, Long plubbingId, Long accountId) {
        AppliedAccount appliedAccount = getAppliedAccountWithCheckHost(loginAccount, plubbingId, accountId);

        if (appliedAccount.getStatus().equals(ApplicantStatus.REJECTED) ||
                appliedAccount.getStatus().equals(ApplicantStatus.ACCEPTED))
            throw new RecruitException(StatusCode.ALREADY_REJECTED);
        appliedAccount.reject();

        Plubbing plubbing = plubbingService.getPlubbing(plubbingId);
        plubbing.updateCurAccountNum();

        return JoinedAccountsInfoResponse.of(plubbing);
    }
}
