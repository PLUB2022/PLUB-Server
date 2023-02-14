package plub.plubserver.domain.notice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import plub.plubserver.common.dto.CommentDto.*;
import plub.plubserver.common.dto.PageResponse;
import plub.plubserver.domain.account.model.Account;
import plub.plubserver.domain.notice.dto.NoticeDto.*;
import plub.plubserver.domain.notice.exception.NoticeException;
import plub.plubserver.domain.notice.model.Notice;
import plub.plubserver.domain.notice.model.NoticeComment;
import plub.plubserver.domain.notice.model.NoticeLike;
import plub.plubserver.domain.notice.repository.NoticeCommentRepository;
import plub.plubserver.domain.notice.repository.NoticeLikeRepository;
import plub.plubserver.domain.notice.repository.NoticeRepository;
import plub.plubserver.domain.plubbing.model.Plubbing;
import plub.plubserver.domain.plubbing.service.PlubbingService;

import java.util.List;

import static plub.plubserver.domain.notice.config.NoticeCode.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class NoticeService {
    private final PlubbingService plubbingService;
    private final NoticeRepository noticeRepository;
    private final NoticeLikeRepository noticeLikeRepository;
    private final NoticeCommentRepository noticeCommentRepository;

    public Notice getNotice(Long noticeId) {
        return noticeRepository.findById(noticeId).orElseThrow(() -> new NoticeException(NOT_FOUND_NOTICE));
    }

    public NoticeComment getNoticeComment(Long commentId) {
        return noticeCommentRepository.findById(commentId).orElseThrow(() -> new NoticeException(NOT_FOUND_COMMENT));
    }

    @Transactional
    public NoticeIdResponse createNotice(Long plubbingId, Account account, CreateNoticeRequest createNoticeRequest) {
        Plubbing plubbing = plubbingService.getPlubbing(plubbingId);
        plubbingService.checkHost(plubbing);
        Notice notice = createNoticeRequest.toEntity(plubbing, account);
        noticeRepository.save(notice);
        return new NoticeIdResponse(notice.getId());
    }

    public PageResponse<NoticeCardResponse> getNoticeList(Account account, Long plubbingId, Pageable pageable) {
        Plubbing plubbing = plubbingService.getPlubbing(plubbingId);
        plubbingService.checkMember(account, plubbing);
        List<NoticeCardResponse> noticeCardResponses = noticeRepository.findAllByPlubbingAndVisibility(plubbing, true, Sort.by(Sort.Direction.DESC, "createdAt"))
                .stream().map(NoticeCardResponse::of).toList();
        return PageResponse.of(pageable, noticeCardResponses);
    }

    public NoticeResponse getNotice(Account account, Long noticeId) {
        Notice notice = getNotice(noticeId);
        checkNoticeStatus(notice);
        plubbingService.checkMember(account, notice.getPlubbing());
        return NoticeResponse.of(notice, isNoticeAuthor(account, notice));
    }

    @Transactional
    public NoticeIdResponse updateNotice(Account account, Long noticeId, UpdateNoticeRequest updateNoticeRequest) {
        Notice notice = getNotice(noticeId);
        plubbingService.checkHost(notice.getPlubbing());
        notice.updateFeed(updateNoticeRequest);
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
                .stream().map((NoticeComment noticeComment) -> NoticeCommentResponse.of(noticeComment, isCommentAuthor(account, noticeComment), isNoticeAuthor(account, notice))).toList();
        return PageResponse.of(pageable, noticeCommentList);
    }

    @Transactional
    public CommentIdResponse createNoticeComment(Account account, Long noticeId, CreateCommentRequest createCommentRequest) {
        Notice notice = getNotice(noticeId);
        checkNoticeStatus(notice);
        plubbingService.checkMember(account, notice.getPlubbing());
        NoticeComment noticeComment = createCommentRequest.toNoticeComment(notice, account);
        noticeCommentRepository.save(noticeComment);
        notice.addComment();
        return new CommentIdResponse(noticeComment.getId());
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
            throw new NoticeException(NOT_AUTHOR_ERROR);
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
            throw new NoticeException(DELETED_STATUS_NOTICE);
    }

    private void checkCommentStatus(NoticeComment noticeComment) {
        if (!noticeComment.isVisibility())
            throw new NoticeException(DELETED_STATUS_COMMENT);
    }

    public void checkCommentAuthor(Account account, NoticeComment noticeComment) {
        checkCommentStatus(noticeComment);
        if (!noticeComment.getAccount().equals(account)) {
            throw new NoticeException(NOT_FOUND_COMMENT);
        }
    }

    public Boolean isNoticeAuthor(Account account, Notice notice) {
        return notice.getAccount().equals(account);
    }

    public Boolean isCommentAuthor(Account account, NoticeComment noticeComment) {
        return noticeComment.getAccount().equals(account);
    }
}
