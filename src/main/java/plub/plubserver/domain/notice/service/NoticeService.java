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
import plub.plubserver.domain.notification.dto.NotificationDto.NotifyParams;
import plub.plubserver.domain.notification.model.NotificationType;
import plub.plubserver.domain.notification.service.NotificationService;
import plub.plubserver.domain.plubbing.model.Plubbing;
import plub.plubserver.domain.plubbing.service.PlubbingService;

import java.util.Optional;

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
        return noticeRepository.findByIdAndVisibility(noticeId, true)
                .orElseThrow(() -> new NoticeException(StatusCode.NOT_FOUND_NOTICE));
    }

    public NoticeComment getNoticeComment(Long commentId) {
        return noticeCommentRepository.findByIdAndVisibility(commentId, true)
                .orElseThrow(() -> new NoticeException(StatusCode.NOT_FOUND_COMMENT));
    }

    @Transactional
    public NoticeIdResponse createNotice(Long plubbingId, Account account, CreateNoticeRequest createNoticeRequest) {
        Account currentAccount = accountService.getAccount(account.getId());
        Plubbing plubbing = plubbingService.getPlubbing(plubbingId);
        plubbingService.checkHost(currentAccount, plubbing);
        Notice notice = noticeRepository.save(createNoticeRequest.toEntity(plubbing, currentAccount));
        currentAccount.addNotice(notice);

        notifyToMembers(plubbing, notice);

        return new NoticeIdResponse(notice.getId());
    }


    // 모임 멤버들에게 새 공지 알림 전체 발송
    private void notifyToMembers(Plubbing plubbing, Notice notice) {
        plubbing.getMembers().forEach(member -> {
            NotifyParams params = NotifyParams.builder()
                    .receiver(member)
                    .type(NotificationType.CREATE_NOTICE)
                    .redirectTargetId(notice.getId())
                    .title("공지")
                    .content(plubbing.getName() + "에 새로운 공지가 등록되었어요. : " + notice.getTitle())
                    .build();
            notificationService.pushMessage(params);
        });
    }

    public NoticeListResponse getNoticeList(Account account, Long plubbingId, Long cursorId, Pageable pageable) {
        Account currentAccount = accountService.getAccount(account.getId());
        Plubbing plubbing = plubbingService.getPlubbing(plubbingId);
        plubbingService.checkMemberAndActive(currentAccount, plubbing);
        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<NoticeCardResponse> noticeCardResponses = noticeRepository.findAllByPlubbingAndVisibilityCursor(plubbing, true, sortedPageable, cursorId)
                .map(it -> NoticeCardResponse.of(it, isNoticeAuthor(currentAccount, it)));
        Long totalElements = noticeRepository.countAllByPlubbingAndVisibility(plubbing, true);
        return new NoticeListResponse(PageResponse.ofCursor(noticeCardResponses, totalElements));
    }

    public NoticeResponse getNotice(Account account, Long plubbingId, Long noticeId) {
        plubbingService.getPlubbing(plubbingId);
        Account currentAccount = accountService.getAccount(account.getId());
        Notice notice = getNotice(noticeId);
        checkNoticeStatus(notice);
        plubbingService.checkMemberAndActive(currentAccount, notice.getPlubbing());
        return NoticeResponse.of(notice, isNoticeAuthor(currentAccount, notice), getLikeCount(notice), getCommentCount(notice));
    }

    @Transactional
    public NoticeResponse updateNotice(Long plubbingId, Long noticeId, UpdateNoticeRequest updateNoticeRequest) {
        Notice notice = getNotice(noticeId);
        plubbingService.checkHost(notice.getPlubbing());
        notice.updateFeed(updateNoticeRequest);
        notifyToMembers(plubbingService.getPlubbing(plubbingId), notice);
        return NoticeResponse.of(notice, true, getLikeCount(notice), getCommentCount(notice));
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
        plubbingService.checkMemberAndActive(currentAccount, notice.getPlubbing());
        if (!noticeLikeRepository.existsByAccountAndNotice(currentAccount, notice)) {
            noticeLikeRepository.save(NoticeLike.builder().notice(notice).account(currentAccount).build());
            return new NoticeMessage(noticeId + ", Like Success.");
        } else {
            noticeLikeRepository.deleteByAccountAndNotice(currentAccount, notice);
            return new NoticeMessage(noticeId + ", Like Cancel.");
        }
    }

    public PageResponse<NoticeCommentResponse> getNoticeCommentList(Account account, Long plubbingId, Long noticeId, Pageable pageable, Long cursorId) {
        plubbingService.getPlubbing(plubbingId);
        Account currentAccount = accountService.getAccount(account.getId());
        Notice notice = getNotice(noticeId);
        plubbingService.checkMemberAndActive(account, notice.getPlubbing());
        Long nextCursorId = cursorId;
        if (cursorId != null && cursorId == 0) {
            Optional<NoticeComment> first = noticeCommentRepository.findFirstByVisibilityAndNoticeId(true, noticeId);
            nextCursorId = first.map(NoticeComment::getId).orElse(null);
        }
        Long commentGroupId = nextCursorId == null ? null : getNoticeComment(nextCursorId).getCommentGroupId();
        Page<NoticeCommentResponse> noticeCommentList = noticeCommentRepository.findAllByNotice(notice, pageable, commentGroupId, cursorId)
                .map(it -> NoticeCommentResponse.of(it, isCommentAuthor(currentAccount, it), isNoticeAuthor(currentAccount, notice), isAuthorComment(it)));
        Long totalElements = noticeCommentRepository.countAllByVisibilityAndNotice(true, notice);
        boolean pageLast = noticeCommentList.getNumber() == noticeCommentList.getTotalPages() - 1
                || noticeCommentList.getNumber() * noticeCommentList.getSize() + noticeCommentList.getNumberOfElements() >= totalElements;
        return PageResponse.ofCursor(noticeCommentList, totalElements, pageLast);
    }

    @Transactional
    public NoticeCommentResponse createNoticeComment(Account account, Long plubbingId, Long noticeId, CreateCommentRequest createCommentRequest) {
        plubbingService.getPlubbing(plubbingId);
        Account currentAccount = accountService.getAccount(account.getId());
        Notice notice = getNotice(noticeId);
        checkNoticeStatus(notice);
        plubbingService.checkMemberAndActive(currentAccount, notice.getPlubbing());

        NoticeComment parentComment = null;
        if (createCommentRequest.parentCommentId() != null) {
            parentComment = getNoticeComment(createCommentRequest.parentCommentId());
            if (!parentComment.getNotice().getId().equals(notice.getId()))
                throw new NoticeException(StatusCode.NOT_FOUND_NOTICE);
        }

        NoticeComment noticeComment = noticeCommentRepository.save(createCommentRequest.toNoticeComment(notice, currentAccount));
        if (parentComment != null) {
            parentComment.addChildComment(noticeComment);
            noticeComment.setCommentGroupId(parentComment.getCommentGroupId());
            noticeCommentRepository.save(parentComment);
            noticeCommentRepository.save(noticeComment);
        } else {
            noticeComment.setCommentGroupId(noticeComment.getId());
        }

        currentAccount.addNoticeComment(noticeComment);

        return NoticeCommentResponse.of(noticeComment, isCommentAuthor(currentAccount, noticeComment), isNoticeAuthor(currentAccount, notice), isAuthorComment(noticeComment));
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
            throw new NoticeException(StatusCode.NOT_COMMENT_AUTHOR_ERROR);
        }
    }

    public Boolean isNoticeAuthor(Account account, Notice notice) {
        return notice.getAccount().getId().equals(account.getId());
    }

    private Boolean isAuthorComment(NoticeComment noticeComment) {
        return noticeComment.getNotice().getAccount().getId().equals(noticeComment.getAccount().getId());
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
