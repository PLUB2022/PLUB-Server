package plub.plubserver.common.dto;

import lombok.Builder;
import org.springframework.lang.Nullable;
import plub.plubserver.domain.account.model.Account;
import plub.plubserver.domain.feed.model.Feed;
import plub.plubserver.domain.feed.model.FeedComment;
import plub.plubserver.domain.notice.model.Notice;
import plub.plubserver.domain.notice.model.NoticeComment;

import javax.validation.constraints.Size;

public class CommentDto {
    public record CreateCommentRequest(
            @Size(max = 100)
            String content,
            @Nullable
            Long parentCommentId
    ) {
        @Builder
        public CreateCommentRequest {
        }

        public FeedComment toFeedComment(Feed feed, Account account) {
            return FeedComment.builder()
                    .content(this.content)
                    .feed(feed)
                    .account(account)
                    .build();
        }

        public NoticeComment toNoticeComment(Notice notice, Account account) {
            return NoticeComment.builder()
                    .content(this.content)
                    .notice(notice)
                    .account(account)
                    .build();
        }
    }

    public record UpdateCommentRequest(
            @Size(max = 100)
            String content
    ) {
        @Builder
        public UpdateCommentRequest {
        }
    }

    public record FeedCommentResponse(
            long commentId,
            String content,
            String profileImage,
            String nickname,
            String createdAt,
            Boolean isCommentAuthor,
            Boolean isFeedAuthor,
            String commentType,
            Long parentCommentId,
            Long groupId
    ) {
        @Builder
        public FeedCommentResponse {
        }

        public static FeedCommentResponse of(FeedComment feedComment, Boolean isCommentAuthor, Boolean isFeedAuthor) {
            return FeedCommentResponse.builder()
                    .commentId(feedComment.getId())
                    .content(feedComment.getContent())
                    .profileImage(feedComment.getAccount().getProfileImage())
                    .nickname(feedComment.getAccount().getNickname())
                    .createdAt(feedComment.getCreatedAt())
                    .isCommentAuthor(isCommentAuthor)
                    .isFeedAuthor(isFeedAuthor)
                    .commentType(feedComment.getParent() == null ? "COMMENT" : "REPLY")
                    .parentCommentId(feedComment.getParent() == null ? null : feedComment.getParent().getId())
                    .groupId(feedComment.getGroupId())
                    .build();
        }
    }

    public record NoticeCommentResponse(
            long commentId,
            String content,
            String profileImage,
            String nickname,
            String createdAt,
            Boolean isCommentAuthor,
            Boolean isNoticeAuthor,
            String commentType,
            Long parentCommentId,
            Long groupId
    ) {
        @Builder
        public NoticeCommentResponse {
        }

        public static NoticeCommentResponse of(NoticeComment noticeComment, Boolean isCommentAuthor, Boolean isNoticeAuthor) {
            return NoticeCommentResponse.builder()
                    .commentId(noticeComment.getId())
                    .content(noticeComment.getContent())
                    .profileImage(noticeComment.getAccount().getProfileImage())
                    .nickname(noticeComment.getAccount().getNickname())
                    .createdAt(noticeComment.getCreatedAt())
                    .isCommentAuthor(isCommentAuthor)
                    .isNoticeAuthor(isNoticeAuthor)
                    .commentType(noticeComment.getParent() == null ? "COMMENT" : "REPLY")
                    .parentCommentId(noticeComment.getParent() == null ? null : noticeComment.getParent().getId())
                    .groupId(noticeComment.getGroupId())
                    .build();
        }
    }

    public record CommentIdResponse(Long commentId) {
    }

    public record CommentMessage(Object result) {
    }
}
