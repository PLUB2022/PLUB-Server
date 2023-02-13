package plub.plubserver.domain.feed.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import plub.plubserver.common.dto.PageResponse;
import plub.plubserver.domain.account.model.Account;
import plub.plubserver.domain.feed.config.FeedCode;
import plub.plubserver.domain.feed.dto.FeedDto.*;
import plub.plubserver.common.dto.CommentDto.*;
import plub.plubserver.domain.feed.exception.FeedException;
import plub.plubserver.domain.feed.model.*;
import plub.plubserver.domain.feed.repository.FeedCommentRepository;
import plub.plubserver.domain.feed.repository.FeedLikeRepository;
import plub.plubserver.domain.feed.repository.FeedRepository;
import plub.plubserver.domain.plubbing.model.Plubbing;
import plub.plubserver.domain.plubbing.service.PlubbingService;

import java.util.List;

import static plub.plubserver.domain.feed.config.FeedCode.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class FeedService {

    private final PlubbingService plubbingService;
    private final FeedRepository feedRepository;
    private final FeedCommentRepository feedCommentRepository;
    private final FeedLikeRepository feedLikeRepository;

    public Feed getFeed(Long feedId) {
        return feedRepository.findById(feedId).orElseThrow(() -> new FeedException(NOT_FOUND_FEED));
    }

    public FeedComment getFeedComment(Long commentId) {
        return feedCommentRepository.findById(commentId).orElseThrow(() -> new FeedException(NOT_FOUND_COMMENT));
    }

    @Transactional
    public FeedIdResponse createFeed(Long plubbingId, Account account, CreateFeedRequest createFeedRequest) {
        Plubbing plubbing = plubbingService.getPlubbing(plubbingId);
        plubbingService.checkMember(account, plubbing);
        Feed feed = createFeedRequest.toEntity(plubbing, account);
        feedRepository.save(feed);
        return new FeedIdResponse(feed.getId());
    }

    public PageResponse<FeedCardResponse> getFeedList(Account account, Long plubbingId, Pageable pageable) {
        Plubbing plubbing = plubbingService.getPlubbing(plubbingId);
        Boolean isHost = plubbingService.isHost(account, plubbing);
        List<FeedCardResponse> feedCardList = feedRepository.findAllByPlubbingAndPinAndVisibility(plubbing, false, true, Sort.by(Sort.Direction.DESC,"createdAt"))
                .stream().map((Feed feed) -> FeedCardResponse.of(feed, isFeedAuthor(account, feed), isHost)).toList();
        return PageResponse.of(pageable, feedCardList);
    }

    public FeedListResponse getPinedFeedList(Account account, Long plubbingId) {
        Plubbing plubbing = plubbingService.getPlubbing(plubbingId);
        Boolean isHost = plubbingService.isHost(account, plubbing);
        List<FeedCardResponse> pinedFeedCardList = feedRepository.findAllByPlubbingAndPinAndVisibility(plubbing, true, true, Sort.by(Sort.Direction.DESC, "pinedAt"))
                .stream().map((Feed feed) -> FeedCardResponse.of(feed, isFeedAuthor(account, feed), isHost)).toList();
        return FeedListResponse.of(pinedFeedCardList);
    }

    @Transactional
    public FeedIdResponse updateFeed(Account account, Long feedId, UpdateFeedRequest updateFeedRequest) {
        Feed feed = getFeed(feedId);
        checkFeedStatus(feed);
        if (feed.getViewType().equals(ViewType.SYSTEM))
            throw new FeedException(CANNOT_DELETED_FEED);
        checkFeedAuthor(account, feed);
        feed.updateFeed(updateFeedRequest);
        return new FeedIdResponse(feedId);
    }

    @Transactional
    public FeedMessage softDeleteFeed(Account account, Long feedId) {
        Feed feed = getFeed(feedId);
        checkFeedStatus(feed);
        if (feed.getViewType().equals(ViewType.SYSTEM))
            throw new FeedException(CANNOT_DELETED_FEED);
        checkFeedAuthor(account, feed);
        feed.softDelete();
        return new FeedMessage("soft delete feed");
    }

    public FeedResponse getFeed(Account account, Long feedId) {
        Feed feed = getFeed(feedId);
        checkFeedStatus(feed);
        Boolean isHost = plubbingService.isHost(account, feed.getPlubbing());
        List<CommentResponse> commentResponses = feedCommentRepository.findAllByFeedAndVisibility(feed, true)
                .stream().map((FeedComment feedComment) -> CommentResponse.ofFeedComment(feedComment, isCommentAuthor(account, feedComment),isFeedAuthor(account, feed))).toList();
        return FeedResponse.of(feed, commentResponses, isFeedAuthor(account, feed), isHost);
    }

    @Transactional
    public FeedIdResponse pinFeed(Account account, Long feedId) {
        Feed feed = getFeed(feedId);
        checkFeedStatus(feed);
        if (feedRepository.countByPin(true) > 20)
            throw new FeedException(MAX_FEED_PIN);
        plubbingService.checkHost(account, feed.getPlubbing());
        feed.pin();
        return new FeedIdResponse(feedId);
    }

    @Transactional
    public FeedMessage likeFeed(Account account, Long feedId) {
        Feed feed = getFeed(feedId);
        checkFeedStatus(feed);
        plubbingService.checkMember(account, feed.getPlubbing());
        if (!feedLikeRepository.existsByAccountAndFeed(account, feed)) {
            feedLikeRepository.save(FeedLike.builder().feed(feed).account(account).build());
            feed.addLike();
            return new FeedMessage(feedId + ", Like Success.");
        }
        else {
            feedLikeRepository.deleteByAccountAndFeed(account, feed);
            feed.subLike();
            return new FeedMessage(feedId + ", Like Cancel.");
        }
    }

    @Transactional
    public CommentIdResponse createFeedComment(Account account, Long feedId, CreateCommentRequest createCommentRequest) {
        Feed feed = getFeed(feedId);
        checkFeedStatus(feed);
        plubbingService.checkMember(account, feed.getPlubbing());
        FeedComment feedComment = createCommentRequest.toEntity(feed, account);
        feedCommentRepository.save(feedComment);
        feed.addComment();
        return new CommentIdResponse(feedComment.getId());
    }

    @Transactional
    public CommentIdResponse updateFeedComment(Account account, Long commentId, UpdateCommentRequest updateCommentRequest) {
        FeedComment feedComment = getFeedComment(commentId);
        checkCommentStatus(feedComment);
        checkCommentAuthor(account, feedComment);
        feedComment.updateFeedComment(updateCommentRequest);
        return new CommentIdResponse(commentId);
    }

    @Transactional
    public CommentMessage deleteFeedComment(Account account, Long commentId) {
        FeedComment feedComment = getFeedComment(commentId);
        checkCommentStatus(feedComment);
        if (!isFeedAuthor(account, feedComment.getFeed()) && !isCommentAuthor(account, feedComment))
            throw new FeedException(NOT_AUTHOR_ERROR);
        feedComment.getFeed().subComment();
        feedComment.softDelete();
        return new CommentMessage("soft delete comment");
    }

    //TODO
    public CommentIdResponse reportFeedComment(Account account, Long feedId) {
        return new CommentIdResponse(feedId);
    }

    public void checkFeedAuthor(Account account, Feed feed) {
        if (!feed.getAccount().equals(account))
            throw new FeedException(NOT_AUTHOR_ERROR);
    }

    public void checkCommentAuthor(Account account, FeedComment feedComment) {
        if (!feedComment.getAccount().equals(account)) {
            throw new FeedException(NOT_AUTHOR_ERROR);
        }
    }

    private void checkFeedStatus(Feed feed) {
        if (!feed.isVisibility())
            throw new FeedException(FeedCode.DELETED_STATUS_FEED);
    }

    private void checkCommentStatus(FeedComment feedComment) {
        if (!feedComment.isVisibility())
            throw new FeedException(FeedCode.DELETED_STATUS_COMMENT);
    }

    public Boolean isFeedAuthor(Account account, Feed feed) {
        return feed.getAccount().equals(account);
    }

    public Boolean isCommentAuthor(Account account, FeedComment feedComment) {
        return feedComment.getAccount().equals(account);
    }

    // 더미용
    @Transactional
    public void makeSystem(long feedId) {
        Feed feed = getFeed(feedId);
        feed.makeSystem();
    }
}


