package plub.plubserver.domain.feed.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;
import plub.plubserver.common.dto.ApiResponse;
import plub.plubserver.domain.account.model.Account;
import plub.plubserver.domain.account.service.AccountService;
import plub.plubserver.domain.feed.dto.FeedDto.*;
import plub.plubserver.domain.feed.service.PlubbingFeedService;

import javax.validation.Valid;

import static plub.plubserver.common.dto.ApiResponse.success;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/plubbings")
@Slf4j
@Api(tags = "게시판 API")
public class PlubbingFeedController {
    private final PlubbingFeedService plubbingFeedService;
    private final AccountService accountService;


    @ApiOperation(value = "게시판 글 작성")
    @PostMapping("/{plubbingId}/feeds")
    public ApiResponse<FeedIdResponse> createFeed(@PathVariable Long plubbingId,
                                                  @Valid @RequestBody CreateFeedRequest createFeedRequest) {
        Account loginAccount = accountService.getCurrentAccount();
        return success(plubbingFeedService.createFeed(plubbingId, loginAccount, createFeedRequest));
    }

    @ApiOperation(value = "게시판 조회")
    @GetMapping("/{plubbingId}/feeds")
    public ApiResponse<FeedListResponse> getFeedList(@PathVariable Long plubbingId,
                                                     @PageableDefault(size = 20) Pageable pageable) {
        Account loginAccount = accountService.getCurrentAccount();
        FeedListResponse feedListResponse = plubbingFeedService.getFeedList(loginAccount, plubbingId, pageable);
        return success(feedListResponse);
    }

    @ApiOperation(value = "게시글 상세 조회")
    @GetMapping("/feeds/{feedId}")
    public ApiResponse<FeedResponse> getFeed(@PathVariable Long feedId) {
        Account loginAccount = accountService.getCurrentAccount();
        return success(plubbingFeedService.getFeed(loginAccount, feedId));
    }

    @ApiOperation(value = "게시글 수정")
    @PutMapping("/feeds/{feedId}")
    public ApiResponse<FeedIdResponse> updateFeed(@PathVariable Long feedId,
            @Valid @RequestBody UpdateFeedRequest updateFeedRequest) {
        Account loginAccount = accountService.getCurrentAccount();
        return success(plubbingFeedService.updateFeed(loginAccount, feedId, updateFeedRequest));
    }

    @ApiOperation(value = "게시글 삭제")
    @DeleteMapping("/feeds/{feedId}")
    public ApiResponse<FeedMessage> deleteFeed(@PathVariable Long feedId) {
        Account loginAccount = accountService.getCurrentAccount();
        return success(plubbingFeedService.deleteFeed(loginAccount, feedId));
    }

    @ApiOperation(value = "게시글 고정")
    @PutMapping("/feeds/{feedId}/pin")
    public ApiResponse<FeedIdResponse> pinFeed(@PathVariable Long feedId) {
        Account loginAccount = accountService.getCurrentAccount();
        return success(plubbingFeedService.pinFeed(loginAccount, feedId));
    }

    @ApiOperation(value = "게시글 좋아요")
    @PutMapping("/feeds/{feedId}/like")
    public ApiResponse<FeedIdResponse> likeFeed(@PathVariable Long feedId) {
        Account loginAccount = accountService.getCurrentAccount();
        return success(plubbingFeedService.likeFeed(loginAccount, feedId));
    }

    @ApiOperation(value = "게시글 댓글 생성")
    @PostMapping("/feeds/{feedId}/comment")
    public ApiResponse<CommentIdResponse> createFeedComment(@PathVariable Long feedId,
                                                         @Valid @RequestBody CreateCommentRequest createCommentRequest) {
        Account loginAccount = accountService.getCurrentAccount();
        return success(plubbingFeedService.createFeedComment(loginAccount, feedId, createCommentRequest));
    }

    @ApiOperation(value = "게시글 댓글 수정")
    @PutMapping("/feeds/{feedId}/comment/{commentId}")
    public ApiResponse<CommentIdResponse> updateFeedComment(@PathVariable Long feedId,
                                                         @Valid @RequestBody UpdateCommentRequest updateCommentRequest) {
        Account loginAccount = accountService.getCurrentAccount();
        return success(plubbingFeedService.updateFeedComment(loginAccount, feedId, updateCommentRequest));
    }

    @ApiOperation(value = "게시글 댓글 삭제")
    @DeleteMapping("/feeds/{feedId}/comment/{commentId}")
    public ApiResponse<CommentMessage> deleteFeedComment(@PathVariable Long feedId) {
        Account loginAccount = accountService.getCurrentAccount();
        return success(plubbingFeedService.deleteFeedComment(loginAccount, feedId));
    }

    @ApiOperation(value = "게시글 댓글 신고")
    @PostMapping("/feeds/{feedId}/comment/{commentId}/report")
    public ApiResponse<CommentIdResponse> reportFeedComment(@PathVariable Long feedId) {
        Account loginAccount = accountService.getCurrentAccount();
        return success(plubbingFeedService.reportFeedComment(loginAccount, feedId));
    }
}
