package plub.plubserver.domain.notice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import plub.plubserver.common.dto.CommentDto.*;
import plub.plubserver.common.dto.PageResponse;
import plub.plubserver.common.exception.StatusCode;
import plub.plubserver.domain.account.model.Account;
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

import java.util.List;


@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class NoticeService {
    private final PlubbingService plubbingService;
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
                .orElseThrow(() -> new NoticeException(StatusCode.NOT_FOUND_NOTICE_COMMENT));
    }

    @Transactional
    public NoticeIdResponse createNotice(Long plubbingId, Account account, CreateNoticeRequest createNoticeRequest) {
        Plubbing plubbing = plubbingService.getPlubbing(plubbingId);
        plubbingService.checkHost(plubbing);
        Notice notice = noticeRepository.save(createNoticeRequest.toEntity(plubbing, account));
        account.addNotice(notice);

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
        Plubbing plubbing = plubbingService.getPlubbing(plubbingId);
        plubbingService.checkMember(account, plubbing);
        List<NoticeCardResponse> noticeCardResponses = noticeRepository
                .findAllByPlubbingAndVisibility(plubbing, true, Sort.by(Sort.Direction.DESC, "createdAt"))
                .stream()
                .map(NoticeCardResponse::of)
                .toList();
        return PageResponse.of(pageable, noticeCardResponses);
    }

    public NoticeResponse getNotice(Account account, Long noticeId) {
        Notice notice = getNotice(noticeId);
        checkNoticeStatus(notice);
        plubbingService.checkMember(account, notice.getPlubbing());
        return NoticeResponse.of(notice, isNoticeAuthor(account, notice));
    }


    @Transactional
    public NoticeIdResponse updateNotice(Account account, Long plubbingId, Long noticeId, UpdateNoticeRequest updateNoticeRequest) {
        Notice notice = getNotice(noticeId);
        plubbingService.checkHost(notice.getPlubbing());
        notice.updateFeed(updateNoticeRequest);

        notifyToMembers(plubbingService.getPlubbing(plubbingId), notice);

        return new NoticeIdResponse(notice.getId());
    }

    @Transactional
    public NoticeMessage softDeleteNotice(Account account, Long noticeId) {
        Notice notice = getNotice(noticeId);
        plubbingService.checkHost(notice.getPlubbing());
        notice.softDelete();
        return new NoticeMessage("soft delete notice");
    }

    @Transactional
    public NoticeMessage likeNotice(Account account, Long noticeId) {
        Notice notice = getNotice(noticeId);
        checkNoticeStatus(notice);
        plubbingService.checkMember(account, notice.getPlubbing());
        if (!noticeLikeRepository.existsByAccountAndNotice(account, notice)) {
            noticeLikeRepository.save(NoticeLike.builder().notice(notice).account(account).build());
            notice.addLike();
            return new NoticeMessage(noticeId + ", Like Success.");
        }
        else {
            noticeLikeRepository.deleteByAccountAndNotice(account, notice);
            notice.subLike();
            return new NoticeMessage(noticeId + ", Like Cancel.");
        }
    }

    public PageResponse<NoticeCommentResponse> getNoticeCommentList(Account account, Long noticeId, Pageable pageable) {
        Notice notice = getNotice(noticeId);
        plubbingService.checkMember(account, notice.getPlubbing());
        List<NoticeCommentResponse> noticeCommentList = noticeCommentRepository.findAllByNoticeAndVisibility(notice, true)
                .stream()
                .map(it -> NoticeCommentResponse.of(it, isCommentAuthor(account, it), isNoticeAuthor(account, notice)))
                .toList();
        return PageResponse.of(pageable, noticeCommentList);
    }

    @Transactional
    public CommentIdResponse createNoticeComment(Account account, Long noticeId, CreateCommentRequest createCommentRequest) {
        Notice notice = getNotice(noticeId);
        checkNoticeStatus(notice);
        plubbingService.checkMember(account, notice.getPlubbing());
        NoticeComment comment = noticeCommentRepository.save(createCommentRequest.toNoticeComment(notice, account));
        account.addNoticeComment(comment);
        notice.addComment();
        
        // 작성자에게 푸시 알림
        notificationService.pushMessage(
                comment.getAccount(),
                notice.getTitle() + "에 새로운 댓글이 달렸습니다.",
                account.getNickname() + ":" + comment.getContent()
        );

        // TODO : 대댓글 알림
        
        return new CommentIdResponse(comment.getId());
    }

    @Transactional
    public CommentIdResponse updateNoticeComment(Account account, Long commentId, UpdateCommentRequest updateCommentRequest) {
        NoticeComment noticeComment = getNoticeComment(commentId);
        checkCommentAuthor(account, noticeComment);
        noticeComment.updateNoticeComment(updateCommentRequest);
        return new CommentIdResponse(commentId);
    }

    @Transactional
    public CommentMessage deleteNoticeComment(Account account, Long commentId) {
        NoticeComment noticeComment = getNoticeComment(commentId);
        checkCommentStatus(noticeComment);
        if (!noticeComment.getNotice().getAccount().equals(account) && !noticeComment.getAccount().equals(account))
            throw new NoticeException(StatusCode.NOT_NOTICE_AUTHOR_ERROR);
        noticeComment.getNotice().subComment();
        noticeComment.softDelete();
        return new CommentMessage("soft delete comment");
    }

    // TODO
    public CommentIdResponse reportNoticeComment(Account account, Long commentId) {
        return new CommentIdResponse(1L);
    }

    private void checkNoticeStatus(Notice notice) {
        if (!notice.isVisibility())
            throw new NoticeException(StatusCode.DELETED_STATUS_NOTICE);
    }

    private void checkCommentStatus(NoticeComment noticeComment) {
        if (!noticeComment.isVisibility())
            throw new NoticeException(StatusCode.DELETED_STATUS_NOTICE_COMMENT);
    }

    public void checkCommentAuthor(Account account, NoticeComment noticeComment) {
        checkCommentStatus(noticeComment);
        if (!noticeComment.getAccount().equals(account)) {
            throw new NoticeException(StatusCode.NOT_FOUND_FEED_COMMENT);
        }
    }

    public Boolean isNoticeAuthor(Account account, Notice notice) {
        return notice.getAccount().equals(account);
    }

    public Boolean isCommentAuthor(Account account, NoticeComment noticeComment) {
        return noticeComment.getAccount().equals(account);
    }
}
