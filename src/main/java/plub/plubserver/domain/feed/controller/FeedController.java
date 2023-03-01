package plub.plubserver.domain.feed.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;
import plub.plubserver.common.dto.ApiResponse;
import plub.plubserver.common.dto.PageResponse;
import plub.plubserver.domain.account.model.Account;
import plub.plubserver.domain.account.service.AccountService;
import plub.plubserver.domain.feed.dto.FeedDto.*;
import plub.plubserver.common.dto.CommentDto.*;
import plub.plubserver.domain.feed.service.FeedService;

import javax.validation.Valid;

import static plub.plubserver.common.dto.ApiResponse.success;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/plubbings")
@Slf4j
@Api(tags = "게시판 API")
public class FeedController {
    private final FeedService feedService;
    private final AccountService accountService;

    @ApiOperation(value = "게시판 글 작성")
    @PostMapping("/{plubbingId}/feeds")
    public ApiResponse<FeedIdResponse> createFeed(
            @PathVariable Long plubbingId,
            @Valid @RequestBody CreateFeedRequest createFeedRequest
    ) {
        Account loginAccount = accountService.getCurrentAccount();
        return success(feedService.createFeed(plubbingId, loginAccount, createFeedRequest));
    }

    @ApiOperation(value = "게시판 조회")
    @GetMapping("/{plubbingId}/feeds")
    public ApiResponse<PageResponse<FeedCardResponse>> getFeedList(
            @PathVariable Long plubbingId,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        Account loginAccount = accountService.getCurrentAccount();
        return success(feedService.getFeedList(loginAccount, plubbingId, pageable));
    }

    @ApiOperation(value = "클립 보드 조회")
    @GetMapping("/{plubbingId}/pins")
    public ApiResponse<FeedListResponse> getPinedFeedList(@PathVariable Long plubbingId) {
        Account loginAccount = accountService.getCurrentAccount();
        return success(feedService.getPinedFeedList(loginAccount, plubbingId));
    }

    @ApiOperation(value = "게시글 상세 조회")
    @GetMapping("/{plubbingId}/feeds/{feedId}")
    public ApiResponse<FeedResponse> getFeed(
            @PathVariable Long plubbingId,
            @PathVariable Long feedId
    ) {
        Account loginAccount = accountService.getCurrentAccount();
        return success(feedService.getFeed(loginAccount, plubbingId, feedId));
    }

    @ApiOperation(value = "게시글 수정")
    @PutMapping("/{plubbingId}/feeds/{feedId}")
    public ApiResponse<FeedResponse> updateFeed(
            @PathVariable Long plubbingId,
            @PathVariable Long feedId,
            @Valid @RequestBody UpdateFeedRequest updateFeedRequest
    ) {
        Account loginAccount = accountService.getCurrentAccount();
        return success(feedService.updateFeed(loginAccount, plubbingId, feedId, updateFeedRequest));
    }

    @ApiOperation(value = "게시글 삭제")
    @DeleteMapping("/{plubbingId}/feeds/{feedId}")
    public ApiResponse<FeedMessage> deleteFeed(
            @PathVariable Long plubbingId,
            @PathVariable Long feedId
    ) {
        Account loginAccount = accountService.getCurrentAccount();
        return success(feedService.softDeleteFeed(loginAccount, plubbingId, feedId));
    }

    @ApiOperation(value = "게시글 고정")
    @PutMapping("/{plubbingId}/feeds/{feedId}/pin")
    public ApiResponse<FeedIdResponse> pinFeed(
            @PathVariable Long plubbingId,
            @PathVariable Long feedId
    ) {
        Account loginAccount = accountService.getCurrentAccount();
        return success(feedService.pinFeed(loginAccount, plubbingId, feedId));
    }

    @ApiOperation(value = "게시글 좋아요")
    @PutMapping("/{plubbingId}/feeds/{feedId}/like")
    public ApiResponse<FeedMessage> likeFeed(
            @PathVariable Long plubbingId,
            @PathVariable Long feedId
    ) {
        Account loginAccount = accountService.getCurrentAccount();
        return success(feedService.likeFeed(loginAccount, plubbingId, feedId));
    }

    @ApiOperation(value = "게시글별 댓글 조회")
    @GetMapping("{plubbingId}/feeds/{feedId}/comments")
    public ApiResponse<PageResponse<FeedCommentResponse>> getFeedCommentList(
            @PathVariable Long plubbingId,
            @PathVariable Long feedId,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        Account loginAccount = accountService.getCurrentAccount();
        return success(feedService.getFeedCommentList(loginAccount, plubbingId, feedId, pageable));
    }

    @ApiOperation(value = "게시글 댓글 생성")
    @PostMapping("/{plubbingId}/feeds/{feedId}/comments")
    public ApiResponse<FeedCommentResponse> createFeedComment(
            @PathVariable Long plubbingId,
            @PathVariable Long feedId,
            @Valid @RequestBody CreateCommentRequest createCommentRequest) {
        Account loginAccount = accountService.getCurrentAccount();
        return success(feedService.createFeedComment(loginAccount, plubbingId, feedId, createCommentRequest));
    }

    @ApiOperation(value = "게시글 댓글 수정")
    @PutMapping("/{plubbingId}/feeds/{feedId}/comments/{commentId}")
    public ApiResponse<FeedCommentResponse> updateFeedComment(
            @PathVariable Long plubbingId,
            @PathVariable Long feedId,
            @PathVariable Long commentId,
            @Valid @RequestBody UpdateCommentRequest updateCommentRequest
    ) {
        Account loginAccount = accountService.getCurrentAccount();
        return success(feedService.updateFeedComment(loginAccount, plubbingId, feedId, commentId, updateCommentRequest));
    }

    @ApiOperation(value = "게시글 댓글 삭제")
    @DeleteMapping("/{plubbingId}/feeds/{feedId}/comments/{commentId}")
    public ApiResponse<CommentMessage> deleteFeedComment(
            @PathVariable Long plubbingId,
            @PathVariable Long feedId,
            @PathVariable Long commentId
    ) {
        Account loginAccount = accountService.getCurrentAccount();
        return success(feedService.deleteFeedComment(loginAccount, plubbingId, feedId, commentId));
    }

    @ApiOperation(value = "게시글 댓글 신고")
    @PostMapping("/{plubbingId}/feeds/{feedId}/comments/{commentId}/report")
    public ApiResponse<CommentIdResponse> reportFeedComment(
            @PathVariable Long plubbingId,
            @PathVariable Long feedId,
            @PathVariable Long commentId
    ) {
        Account loginAccount = accountService.getCurrentAccount();
        return success(feedService.reportFeedComment(loginAccount, plubbingId, feedId, commentId));
    }

    @ApiOperation(value = "마이페이지 - 내 게시글 조회")
    @GetMapping("/{plubbingId}/feeds/my")
    public ApiResponse<PageResponse<FeedCardResponse>> getMyFeedList(
            @PathVariable Long plubbingId,
            @PageableDefault(size = 20) Pageable pageable) {
        Account loginAccount = accountService.getCurrentAccount();
        return success(feedService.getMyFeedList(loginAccount, plubbingId, pageable));
    }
}
