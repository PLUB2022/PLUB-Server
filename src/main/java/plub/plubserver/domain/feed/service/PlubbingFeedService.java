package plub.plubserver.domain.feed.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import plub.plubserver.domain.account.model.Account;
import plub.plubserver.domain.feed.dto.PlubbingFeedDto.*;
import plub.plubserver.common.dto.CommentDto.*;
import plub.plubserver.domain.feed.repository.PlubbingFeedRepository;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PlubbingFeedService {
    private final PlubbingFeedRepository plubbingFeedRepository;

    public FeedIdResponse createFeed(Long plubbingId, Account owner, CreateFeedRequest createFeedRequest) {
        return new FeedIdResponse(1L);
    }

    public FeedListResponse getFeedList(Account loginAccount, Long plubbingId, Pageable pageable) {
        List<FeedCardResponse> feedCardResponses = List.of(
                new FeedCardResponse(1L, "PHOTO", "feedTitle1", "", "imgaeUrl1", "2020-02-02 12:12:00", true, "profileUrl1", "nickname1"),
                new FeedCardResponse(2L, "LINE", "feedTitle2", "줄글로 된 내용~~~", "", "2020-02-02 12:12:00", false, "profileUrl2", "nickname2"),
                new FeedCardResponse(3L, "PHOTO_LINE", "feedTitle3", "어쩌꾸 저쩌구!!~~`", "imgaeUrl3", "2020-02-02 12:12:00", true, "profileUrl3", "nickname3")
        );
        return FeedListResponse.of(feedCardResponses);
    }

    public FeedIdResponse updateFeed(Account loginAccount, Long feedId, UpdateFeedRequest updateFeedRequest) {
        return new FeedIdResponse(1L);
    }

    public FeedMessage deleteFeed(Account loginAccount, Long feedId) {
        return new FeedMessage("success");
    }

    public FeedResponse getFeed(Account loginAccount, Long feedId) {
        return new FeedResponse(1L, "PHOTO", "feedTitle1", "", "imgaeUrl1", "2020-02-02 12:12:00", true, "profileUrl1", "nickname1", 5L, 3L, List.of(
                new CommentResponse(1L, "commentContent1", "2020-02-02 12:12:00", "profileUrl1", "nickname1"),
                new CommentResponse(2L, "commentContent2", "2020-02-02 12:12:00", "profileUrl2", "nickname2"),
                new CommentResponse(3L, "commentContent3", "2020-02-02 12:12:00", "profileUrl3", "nickname3")
        ));
    }

    public FeedIdResponse pinFeed(Account loginAccount, Long feedId) {
        return new FeedIdResponse(1L);
    }

    public FeedIdResponse likeFeed(Account loginAccount, Long feedId) {
        return new FeedIdResponse(1L);
    }

    public CommentIdResponse createFeedComment(Account loginAccount, Long feedId, CreateCommentRequest createCommentRequest) {
        return new CommentIdResponse(1L);
    }

    public CommentIdResponse updateFeedComment(Account loginAccount, Long feedId, UpdateCommentRequest updateCommentRequest) {
        return new CommentIdResponse(1L);
    }

    public CommentMessage deleteFeedComment(Account loginAccount, Long feedId) {
        return new CommentMessage("success");
    }

    public CommentIdResponse reportFeedComment(Account loginAccount, Long feedId) {
        return new CommentIdResponse(1L);
    }
}
