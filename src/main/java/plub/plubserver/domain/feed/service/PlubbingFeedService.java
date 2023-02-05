package plub.plubserver.domain.feed.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import plub.plubserver.common.dto.PageResponse;
import plub.plubserver.domain.account.model.Account;
import plub.plubserver.domain.feed.dto.PlubbingFeedDto.*;
import plub.plubserver.common.dto.CommentDto.*;
import plub.plubserver.domain.feed.repository.PlubbingFeedRepository;

import java.util.List;

import static plub.plubserver.common.dummy.DummyImage.PLUB_MAIN_LOGO;
import static plub.plubserver.common.dummy.DummyImage.PLUB_PROFILE_TEST;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PlubbingFeedService {
    private final PlubbingFeedRepository plubbingFeedRepository;

    public FeedIdResponse createFeed(Long plubbingId, Account owner, CreateFeedRequest createFeedRequest) {
        return new FeedIdResponse(1L);
    }

    public PageResponse<FeedCardResponse> getFeedList(Account loginAccount, Long plubbingId, Pageable pageable) {
        List<FeedCardResponse> feedList = List.of(
                new FeedCardResponse(5L, "PHOTO", "NORMAL", "feedTitle4", "", PLUB_MAIN_LOGO, "2020-02-02 12:12:00", false, PLUB_PROFILE_TEST, "nickname4", 1L),
                new FeedCardResponse(6L, "LINE", "NORMAL", "feedTitle5", "줄글로 된 내용~~~", "", "2020-02-02 12:12:33", false, PLUB_PROFILE_TEST, "nickname5", 1L),
                new FeedCardResponse(7L, "PHOTO_LINE", "NORMAL", "feedTitle6", "어쩌꾸 저쩌구!!~~`", PLUB_MAIN_LOGO, "2020-02-05 12:12:00", false, PLUB_PROFILE_TEST, "nickname6", 1L),
                new FeedCardResponse(8L, "LINE", "SYSTEM", "8번째 멤버와 함께 갑니다.", "<b>김밥먹고싶다</b> 님이 <b>요란한 한줄</b> 에 들어왔어요", "", "2020-02-11 12:12:00", false, "", "", 1L),
                new FeedCardResponse(9L, "PHOTO", "NORMAL", "feedTitle7", "", PLUB_MAIN_LOGO, "2020-02-12 12:12:00", false, PLUB_PROFILE_TEST, "nickname7", 1L),
                new FeedCardResponse(10L, "LINE", "NORMAL", "feedTitle8", "줄글로 된 내용~~~", "", "2020-03-13 12:12:00", false, PLUB_PROFILE_TEST, "nickname8", 1L),
                new FeedCardResponse(11L, "PHOTO_LINE", "NORMAL", "feedTitle9", "어쩌꾸 저쩌구!!~~`", PLUB_MAIN_LOGO, "2020-04-15 12:12:00", false, PLUB_PROFILE_TEST, "nickname9", 1L)
        );
        return PageResponse.of(pageable, feedList);
    }

    public FeedListResponse getPinedFeedList(Account loginAccount, Long plubbingId) {
        List<FeedCardResponse> pinedFeedList = List.of(
                new FeedCardResponse(1L, "PHOTO", "PIN", "feedTitle1", "", PLUB_MAIN_LOGO, "2020-02-02 12:12:00", true, PLUB_PROFILE_TEST, "nickname1", 1L),
                new FeedCardResponse(2L, "LINE", "PIN", "feedTitle2", "줄글로 된 내용~~~", "", "2020-02-02 12:24:00", true, PLUB_PROFILE_TEST, "nickname2", 1L),
                new FeedCardResponse(3L, "PHOTO_LINE", "PIN", "feedTitle3", "어쩌꾸 저쩌구!!~~`", PLUB_MAIN_LOGO, "2020-02-05 12:12:00", true, PLUB_PROFILE_TEST, "nickname3", 1L)
        );
        return FeedListResponse.of(pinedFeedList);
    }

    public FeedIdResponse updateFeed(Account loginAccount, Long feedId, UpdateFeedRequest updateFeedRequest) {
        return new FeedIdResponse(1L);
    }

    public FeedMessage deleteFeed(Account loginAccount, Long feedId) {
        return new FeedMessage("success");
    }

    public FeedResponse getFeed(Account loginAccount, Long feedId) {
        return new FeedResponse(1L, "PHOTO", "NORMAL", "feedTitle1", "", "imgaeUrl1", "2020-02-02 12:12:00", true, "profileUrl1", "nickname1", 5L, 3L, List.of(
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
