package plub.plubserver.domain.feed.service;

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
import plub.plubserver.domain.feed.dto.FeedDto.*;
import plub.plubserver.domain.feed.exception.FeedException;
import plub.plubserver.domain.feed.model.Feed;
import plub.plubserver.domain.feed.model.FeedComment;
import plub.plubserver.domain.feed.model.FeedLike;
import plub.plubserver.domain.feed.model.ViewType;
import plub.plubserver.domain.feed.repository.FeedCommentRepository;
import plub.plubserver.domain.feed.repository.FeedLikeRepository;
import plub.plubserver.domain.feed.repository.FeedRepository;
import plub.plubserver.domain.notification.model.NotificationType;
import plub.plubserver.domain.notification.service.NotificationService;
import plub.plubserver.domain.plubbing.model.Plubbing;
import plub.plubserver.domain.plubbing.service.PlubbingService;
import plub.plubserver.util.CursorUtils;

import java.util.List;
import java.util.Optional;

import static plub.plubserver.common.exception.StatusCode.NOT_FOUND_COMMENT;
import static plub.plubserver.domain.notification.dto.NotificationDto.NotifyParams;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class FeedService {

    private final PlubbingService plubbingService;
    private final FeedRepository feedRepository;
    private final FeedCommentRepository feedCommentRepository;
    private final FeedLikeRepository feedLikeRepository;
    private final NotificationService notificationService;

    public Feed getFeed(Long feedId) {
        return feedRepository.findById(feedId).orElseThrow(() -> new FeedException(StatusCode.NOT_FOUND_FEED));
    }

    public FeedComment getFeedComment(Long commentId) {
        return feedCommentRepository.findById(commentId)
                .orElseThrow(() -> new FeedException(NOT_FOUND_COMMENT));
    }

    @Transactional
    public FeedIdResponse createFeed(Long plubbingId, Account account, CreateFeedRequest createFeedRequest) {
        Plubbing plubbing = plubbingService.getPlubbing(plubbingId);
        plubbingService.checkMember(account, plubbing);
        Feed feed = createFeedRequest.toEntity(plubbing, account);
        feedRepository.save(feed);
        return new FeedIdResponse(feed.getId());
    }

    public PageResponse<FeedCardResponse> getFeedList(Account account, Long plubbingId, Pageable pageable, Long cursorId) {
        Plubbing plubbing = plubbingService.getPlubbing(plubbingId);
        plubbingService.checkMember(account, plubbing);
        Boolean isHost = plubbingService.isHost(account, plubbing);
        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<FeedCardResponse> feedCardList = feedRepository.findAllByPlubbingAndPinAndVisibilityCursor(plubbing, false, true, sortedPageable, cursorId)
                .map(it -> FeedCardResponse.of(it, isFeedAuthor(account, it), isHost, getLikeCount(it), getCommentCount(it)));
        Long totalElements = feedRepository.countAllByPlubbingAndVisibility(plubbing, true);
        return PageResponse.ofCursor(feedCardList, totalElements);
    }

    public FeedListResponse getPinedFeedList(Account account, Long plubbingId) {
        Plubbing plubbing = plubbingService.getPlubbing(plubbingId);
        plubbingService.checkMember(account, plubbing);
        Boolean isHost = plubbingService.isHost(account, plubbing);
        List<FeedCardResponse> pinedFeedCardList = feedRepository.findAllByPlubbingAndPinAndVisibility(plubbing, true, true, Sort.by(Sort.Direction.DESC, "pinedAt"))
                .stream().map((Feed feed) -> FeedCardResponse.of(feed, isFeedAuthor(account, feed), isHost, getLikeCount(feed), getCommentCount(feed))).toList();
        return FeedListResponse.of(pinedFeedCardList);
    }

    @Transactional
    public FeedResponse updateFeed(Account account, Long plubbingId, Long feedId, UpdateFeedRequest updateFeedRequest) {
        plubbingService.getPlubbing(plubbingId);
        Feed feed = getFeed(feedId);
        checkFeedStatus(feed);
        if (feed.getViewType().equals(ViewType.SYSTEM))
            throw new FeedException(StatusCode.CANNOT_DELETED_FEED);
        checkFeedAuthor(account, feed);
        feed.updateFeed(updateFeedRequest);
        Boolean isHost = plubbingService.isHost(account, feed.getPlubbing());
        return FeedResponse.of(feed, true, isHost, getLikeCount(feed), getCommentCount(feed));
    }

    @Transactional
    public FeedMessage softDeleteFeed(Account account, Long plubbingId, Long feedId) {
        plubbingService.getPlubbing(plubbingId);
        Feed feed = getFeed(feedId);
        checkFeedStatus(feed);
        if (feed.getViewType().equals(ViewType.SYSTEM))
            throw new FeedException(StatusCode.CANNOT_DELETED_FEED);
        checkFeedAuthor(account, feed);
        feed.softDelete();
        return new FeedMessage("soft delete feed");
    }

    public FeedResponse getFeed(Account account, Long plubbingId, Long feedId) {
        plubbingService.getPlubbing(plubbingId);
        Feed feed = getFeed(feedId);
        checkFeedStatus(feed);
        plubbingService.checkMember(account, feed.getPlubbing());
        Boolean isHost = plubbingService.isHost(account, feed.getPlubbing());
        return FeedResponse.of(feed, isFeedAuthor(account, feed), isHost, getLikeCount(feed), getCommentCount(feed));
    }

    @Transactional
    public FeedIdResponse pinFeed(Account account, Long plubbingId, Long feedId) {
        plubbingService.getPlubbing(plubbingId);
        Feed feed = getFeed(feedId);
        checkFeedStatus(feed);
        if (feedRepository.countByPin(true) > 20)
            throw new FeedException(StatusCode.MAX_FEED_PIN);
        plubbingService.checkHost(account, feed.getPlubbing());
        feed.pin();
        return new FeedIdResponse(feedId);
    }

    @Transactional
    public FeedMessage likeFeed(Account account, Long plubbingId, Long feedId) {
        plubbingService.getPlubbing(plubbingId);
        Feed feed = getFeed(feedId);
        checkFeedStatus(feed);
        plubbingService.checkMember(account, feed.getPlubbing());
        if (!feedLikeRepository.existsByAccountAndFeed(account, feed)) {
            feedLikeRepository.save(FeedLike.builder().feed(feed).account(account).build());
            return new FeedMessage(feedId + ", Like Success.");
        } else {
            feedLikeRepository.deleteByAccountAndFeed(account, feed);
            return new FeedMessage(feedId + ", Like Cancel.");
        }
    }

    public PageResponse<FeedCommentResponse> getFeedCommentList(Account account, Long plubbingId, Long feedId, Pageable pageable, Long cursorId) {
        plubbingService.getPlubbing(plubbingId);
        Feed feed = getFeed(feedId);
        checkFeedStatus(feed);
        plubbingService.checkMember(account, feed.getPlubbing());
        Long nextCursorId = cursorId;
        if (cursorId != null && cursorId == 0) {
            Optional<FeedComment> first = feedCommentRepository.findFirstByVisibilityAndFeedId(true, feedId);
            nextCursorId = first.map(FeedComment::getId).orElse(null);
        }
        Long commentGroupId = nextCursorId == null ? null : getFeedComment(nextCursorId).getCommentGroupId();
        Page<FeedCommentResponse> feedCommentList = feedCommentRepository.findAllByFeed(feed, pageable, commentGroupId, cursorId)
                .map(it -> FeedCommentResponse.of(it, isCommentAuthor(account, it), isFeedAuthor(account, feed)));
        Long totalElements = getCommentCount(feed);
        return PageResponse.ofCursor(feedCommentList, totalElements);
    }

    @Transactional
    public FeedCommentResponse createFeedComment(
            Account commentAuthor,
            Long plubbingId,
            Long feedId,
            CreateCommentRequest createCommentRequest
    ) {
        Feed feed = getFeed(feedId);
        checkFeedStatus(feed);
        plubbingService.checkMember(commentAuthor, feed.getPlubbing());

        FeedComment parentComment = null;
        if (createCommentRequest.parentCommentId() != null) {
            parentComment = getFeedComment(createCommentRequest.parentCommentId());
            if (!parentComment.getFeed().getId().equals(feed.getId()))
                throw new FeedException(StatusCode.NOT_FOUND_FEED);
        }

        FeedComment comment = feedCommentRepository.save(createCommentRequest.toFeedComment(feed, commentAuthor));
        if (parentComment != null) {
            parentComment.addChildComment(comment);
            comment.setCommentGroupId(parentComment.getCommentGroupId());
            feedCommentRepository.save(parentComment);
            feedCommentRepository.save(comment);
        } else {
            comment.setCommentGroupId(comment.getId());
        }

        // 작성자에게 푸시 알림
        Plubbing plubbing = plubbingService.getPlubbing(plubbingId);
        Account author = feed.getAccount();
        NotifyParams params = NotifyParams.builder()
                .receiver(author)
                .type(NotificationType.CREATE_FEED_COMMENT)
                .redirectTargetId(feed.getId())
                .title(plubbing.getName())
                .content(commentAuthor.getNickname() + " 님이 " + author.getNickname() + " 님의 게시글에 댓글을 남겼어요\n : " + comment.getContent())
                .build();
        notificationService.pushMessage(params);

        // TODO : 대댓글 알림

        return FeedCommentResponse.of(comment, true, isFeedAuthor(commentAuthor, feed));
    }

    @Transactional
    public FeedCommentResponse updateFeedComment(Account account, Long plubbingId, Long feedId, Long commentId, UpdateCommentRequest updateCommentRequest) {
        plubbingService.getPlubbing(plubbingId);
        getFeed(feedId);
        FeedComment feedComment = getFeedComment(commentId);
        checkCommentStatus(feedComment);
        checkCommentAuthor(account, feedComment);
        feedComment.updateFeedComment(updateCommentRequest);
        return FeedCommentResponse.of(feedComment, true, isFeedAuthor(account, feedComment.getFeed()));
    }

    @Transactional
    public CommentMessage deleteFeedComment(Account account, Long plubbingId, Long feedId, Long commentId) {
        plubbingService.getPlubbing(plubbingId);
        getFeed(feedId);
        FeedComment feedComment = getFeedComment(commentId);
        checkCommentStatus(feedComment);

        if (!isFeedAuthor(account, feedComment.getFeed()) && !isCommentAuthor(account, feedComment))
            throw new FeedException(StatusCode.NOT_FEED_AUTHOR_ERROR);

        if (feedComment.getChildren().size() != 0)
            deleteChildComment(feedComment);

        feedComment.softDelete();
        return new CommentMessage("soft delete comment");
    }

    private void deleteChildComment(FeedComment feedComment) {
        if (feedComment.getChildren().size() == 0)
            return;
        feedComment.getChildren().forEach(it -> {
            it.softDelete();
            deleteChildComment(it);
            feedCommentRepository.save(it);
        });
    }

    public MyFeedListResponse getMyFeedList(Account account, Long plubbingId, Pageable pageable, Long cursorId) {
        Plubbing plubbing = plubbingService.getPlubbing(plubbingId);
        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<FeedCardResponse> myFeedCardList = feedRepository.findAllByPlubbingAndAccountAndVisibility(plubbing, account, true, sortedPageable, cursorId)
                .map((Feed feed) -> FeedCardResponse.of(feed, true, true, getLikeCount(feed), getCommentCount(feed)));
        Long totalElements = CursorUtils.getTotalElements(myFeedCardList.getTotalElements(), cursorId);
        PageResponse<FeedCardResponse> response = PageResponse.ofCursor(myFeedCardList, totalElements);
        return MyFeedListResponse.of(plubbing, response);
    }

    //TODO
    public CommentIdResponse reportFeedComment(Account account, Long plubbingId, Long feedId, Long commentId) {
        return new CommentIdResponse(commentId);
    }

    public void checkFeedAuthor(Account account, Feed feed) {
        if (!feed.getAccount().getId().equals(account.getId()))
            throw new FeedException(StatusCode.NOT_FEED_AUTHOR_ERROR);
    }

    public void checkCommentAuthor(Account account, FeedComment feedComment) {
        if (!feedComment.getAccount().getId().equals(account.getId())) {
            throw new FeedException(StatusCode.NOT_FEED_AUTHOR_ERROR);
        }
    }

    private void checkFeedStatus(Feed feed) {
        if (!feed.isVisibility())
            throw new FeedException(StatusCode.DELETED_STATUS_FEED);
    }

    private void checkCommentStatus(FeedComment feedComment) {
        if (!feedComment.isVisibility())
            throw new FeedException(StatusCode.DELETED_STATUS_COMMENT);
    }

    public Boolean isFeedAuthor(Account account, Feed feed) {
        return feed.getAccount().getId().equals(account.getId());
    }

    public Boolean isCommentAuthor(Account account, FeedComment feedComment) {
        return feedComment.getAccount().getId().equals(account.getId());
    }

    public Long getCommentCount(Feed feed) {
        return feedCommentRepository.countAllByVisibilityAndFeed(true, feed);
    }

    public Long getLikeCount(Feed feed) {
        return feedLikeRepository.countAllByVisibilityAndFeed(true, feed);
    }

    // 더미용
    @Transactional
    public void makeSystem(long feedId) {
        Feed feed = getFeed(feedId);
        feed.makeSystem();
    }

    @Transactional
    public void createSystemFeed(Plubbing plubbing, String nickname) {
        String title = plubbing.getCurAccountNum() + "번째 멤버와 함께 갑니다.";
        String content = "<b>"+ nickname +"</b> 님이 <b>"+ plubbing.getName() +"</b> 에 들어왔어요";
        Feed feed = Feed.createSystemFeed(plubbing, title, content);
        feedRepository.save(feed);
    }
}