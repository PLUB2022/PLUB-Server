package plub.plubserver.domain.notice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import plub.plubserver.common.dto.CommentDto.*;
import plub.plubserver.common.dto.PageResponse;
import plub.plubserver.common.exception.StatusCode;
import plub.plubserver.domain.account.model.Account;
import plub.plubserver.domain.account.service.AccountService;
import plub.plubserver.domain.notice.dto.NoticeDto.*;
import plub.plubserver.domain.notice.exception.NoticeException;
import plub.plubserver.domain.notice.model.Notice;
import plub.plubserver.domain.notice.model.NoticeComment;
import plub.plubserver.domain.notice.model.NoticeLike;
import plub.plubserver.domain.notice.repository.NoticeCommentRepository;
import plub.plubserver.domain.notice.repository.NoticeLikeRepository;
import plub.plubserver.domain.notice.repository.NoticeRepository;
import plub.plubserver.domain.notification.service.NotificationService;
import plub.plubserver.domain.plubbing.model.Plubbing;
import plub.plubserver.domain.plubbing.service.PlubbingService;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class NoticeService {
    private final PlubbingService plubbingService;
    private final AccountService accountService;
    private final NoticeRepository noticeRepository;
    private final NoticeLikeRepository noticeLikeRepository;
    private final NoticeCommentRepository noticeCommentRepository;
    private final NotificationService notificationService; // TODO : AOP로 의존성을 줄일 방법 생각하기

    public Notice getNotice(Long noticeId) {
        return noticeRepository.findById(noticeId)
                .orElseThrow(() -> new NoticeException(StatusCode.NOT_FOUND_NOTICE));
    }

    public NoticeComment getNoticeComment(Long commentId) {
        return noticeCommentRepository.findById(commentId)
                .orElseThrow(() -> new NoticeException(StatusCode.NOT_FOUND_COMMENT));
    }

    @Transactional
    public NoticeIdResponse createNotice(Long plubbingId, Account account, CreateNoticeRequest createNoticeRequest) {
        Account currentAccount = accountService.getAccount(account.getId());
        Plubbing plubbing = plubbingService.getPlubbing(plubbingId);
        plubbingService.checkHost(plubbing);
        Notice notice = noticeRepository.save(createNoticeRequest.toEntity(plubbing, currentAccount));
        currentAccount.addNotice(notice);

        notifyToMembers(plubbing, notice);

        return new NoticeIdResponse(notice.getId());
    }


    // 모임 멤버들에게 새 공지 알림 전체 발송
    private void notifyToMembers(Plubbing plubbing, Notice notice) {
        plubbing.getMembers().forEach(member -> {
            notificationService.pushMessage(
                    member,
                    "공지",
                    plubbing.getName() + "에 새로운 공지가 등록되었어요. : " + notice.getTitle()
            );
        });
    }

    public PageResponse<NoticeCardResponse> getNoticeList(Account account, Long plubbingId, Pageable pageable) {
        Account currentAccount = accountService.getAccount(account.getId());
        Plubbing plubbing = plubbingService.getPlubbing(plubbingId);
        plubbingService.checkMember(currentAccount, plubbing);
        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<NoticeCardResponse> noticeCardResponses = noticeRepository.findAllByPlubbingAndVisibility(plubbing, true, sortedPageable).map(NoticeCardResponse::of);
        return PageResponse.of(noticeCardResponses);
    }

    public NoticeResponse getNotice(Account account, Long plubbingId, Long noticeId) {
        plubbingService.getPlubbing(plubbingId);
        Account currentAccount = accountService.getAccount(account.getId());
        Notice notice = getNotice(noticeId);
        checkNoticeStatus(notice);
        plubbingService.checkMember(currentAccount, notice.getPlubbing());
        return NoticeResponse.of(notice, isNoticeAuthor(currentAccount, notice), getLikeCount(notice), getCommentCount(notice));
    }

    @Transactional
    public NoticeIdResponse updateNotice(Long plubbingId, Long noticeId, UpdateNoticeRequest updateNoticeRequest) {
        Notice notice = getNotice(noticeId);
        plubbingService.checkHost(notice.getPlubbing());
        notice.updateFeed(updateNoticeRequest);

        notifyToMembers(plubbingService.getPlubbing(plubbingId), notice);

        return new NoticeIdResponse(notice.getId());
    }

    @Transactional
    public NoticeMessage softDeleteNotice(Long plubbingId, Long noticeId) {
        plubbingService.getPlubbing(plubbingId);
        Notice notice = getNotice(noticeId);
        plubbingService.checkHost(notice.getPlubbing());
        notice.softDelete();
        return new NoticeMessage("soft delete notice");
    }

    @Transactional
    public NoticeMessage likeNotice(Account account, Long plubbingId, Long noticeId) {
        plubbingService.getPlubbing(plubbingId);
        Account currentAccount = accountService.getAccount(account.getId());
        Notice notice = getNotice(noticeId);
        checkNoticeStatus(notice);
        plubbingService.checkMember(currentAccount, notice.getPlubbing());
        if (!noticeLikeRepository.existsByAccountAndNotice(currentAccount, notice)) {
            noticeLikeRepository.save(NoticeLike.builder().notice(notice).account(currentAccount).build());
            return new NoticeMessage(noticeId + ", Like Success.");
        } else {
            noticeLikeRepository.deleteByAccountAndNotice(currentAccount, notice);
            return new NoticeMessage(noticeId + ", Like Cancel.");
        }
    }

    public PageResponse<NoticeCommentResponse> getNoticeCommentList(Account account, Long plubbingId, Long noticeId, Pageable pageable) {
        plubbingService.getPlubbing(plubbingId);
        Account currentAccount = accountService.getAccount(account.getId());
        Notice notice = getNotice(noticeId);
        plubbingService.checkMember(account, notice.getPlubbing());
        Page<NoticeCommentResponse> noticeCommentList = noticeCommentRepository.findAllByNotice(notice, pageable)
                .map(it -> NoticeCommentResponse.of(it, isCommentAuthor(currentAccount, it), isNoticeAuthor(currentAccount, notice)));
        return PageResponse.of(noticeCommentList);
    }

    @Transactional
    public CommentIdResponse createNoticeComment(Account account, Long plubbingId, Long noticeId, CreateCommentRequest createCommentRequest) {
        plubbingService.getPlubbing(plubbingId);
        Account currentAccount = accountService.getAccount(account.getId());
        Notice notice = getNotice(noticeId);
        checkNoticeStatus(notice);
        plubbingService.checkMember(currentAccount, notice.getPlubbing());

        NoticeComment parentComment = null;
        if (createCommentRequest.parentCommentId() != null) {
            parentComment = getNoticeComment(createCommentRequest.parentCommentId());
            if (!parentComment.getNotice().getId().equals(notice.getId()))
                throw new NoticeException(StatusCode.NOT_FOUND_NOTICE);
        }

        NoticeComment comment = noticeCommentRepository.save(createCommentRequest.toNoticeComment(notice, currentAccount));
        if (parentComment != null) {
            parentComment.addChildComment(comment);
            comment.setCommentGroupId(parentComment.getCommentGroupId());
            noticeCommentRepository.save(parentComment);
            noticeCommentRepository.save(comment);
        } else {
            comment.setCommentGroupId(comment.getId());
        }

        currentAccount.addNoticeComment(comment);

        // 작성자에게 푸시 알림
        notificationService.pushMessage(
                comment.getAccount(),
                notice.getTitle() + "에 새로운 댓글이 달렸습니다.",
                currentAccount.getNickname() + ":" + comment.getContent()
        );

        // TODO : 대댓글 알림

        return new CommentIdResponse(comment.getId());
    }

    @Transactional
    public CommentIdResponse updateNoticeComment(Account account, Long plubbingId, Long noticeId, Long commentId, UpdateCommentRequest updateCommentRequest) {
        plubbingService.getPlubbing(plubbingId);
        getNotice(noticeId);
        Account currentAccount = accountService.getAccount(account.getId());
        NoticeComment noticeComment = getNoticeComment(commentId);
        checkCommentAuthor(currentAccount, noticeComment);
        noticeComment.updateNoticeComment(updateCommentRequest);
        return new CommentIdResponse(commentId);
    }

    @Transactional
    public CommentMessage deleteNoticeComment(Account account, Long plubbingId, Long noticeId, Long commentId) {
        plubbingService.getPlubbing(plubbingId);
        getNotice(noticeId);
        Account currentAccount = accountService.getAccount(account.getId());
        NoticeComment noticeComment = getNoticeComment(commentId);
        checkCommentStatus(noticeComment);

        if (!noticeComment.getNotice().getAccount().equals(currentAccount) && !noticeComment.getAccount().equals(currentAccount))
            throw new NoticeException(StatusCode.NOT_NOTICE_AUTHOR_ERROR);

        if (noticeComment.getChildren().size() != 0)
            deleteChildComment(noticeComment);

        noticeComment.softDelete();
        return new CommentMessage("soft delete comment");
    }

    private void deleteChildComment(NoticeComment noticeComment) {
        if (noticeComment.getChildren().size() == 0)
            return;
        noticeComment.getChildren().forEach(it -> {
            it.softDelete();
            deleteChildComment(it);
            noticeCommentRepository.save(it);
        });
    }

    // TODO
    public CommentIdResponse reportNoticeComment(Account account, Long plubbingId, Long noticeId, Long commentId) {
        return new CommentIdResponse(1L);
    }

    private void checkNoticeStatus(Notice notice) {
        if (!notice.isVisibility())
            throw new NoticeException(StatusCode.DELETED_STATUS_NOTICE);
    }

    private void checkCommentStatus(NoticeComment noticeComment) {
        if (!noticeComment.isVisibility())
            throw new NoticeException(StatusCode.DELETED_STATUS_COMMENT);
    }

    public void checkCommentAuthor(Account account, NoticeComment noticeComment) {
        checkCommentStatus(noticeComment);
        if (!noticeComment.getAccount().getId().equals(account.getId())) {
            throw new NoticeException(StatusCode.NOT_FOUND_COMMENT);
        }
    }

    public Boolean isNoticeAuthor(Account account, Notice notice) {
        return notice.getAccount().getId().equals(account.getId());
    }

    public Boolean isCommentAuthor(Account account, NoticeComment noticeComment) {
        return noticeComment.getAccount().getId().equals(account.getId());
    }

    public Long getCommentCount(Notice notice) {return noticeCommentRepository.countAllByVisibilityAndNotice(true, notice);
    }

    public Long getLikeCount(Notice notice) {
        return noticeLikeRepository.countAllByVisibilityAndNotice(true, notice);
    }
}
