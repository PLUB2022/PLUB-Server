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
            @Size(max = 300)
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
            @Size(max = 300)
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
            Boolean isAuthorComment,
            String commentType,
            String parentCommentNickname,
            Long parentCommentId,
            Long commentGroupId
    ) {
        @Builder
        public FeedCommentResponse {
        }

        public static FeedCommentResponse of(FeedComment feedComment, Boolean isCommentAuthor, Boolean isFeedAuthor, Boolean isAuthorComment) {
            return FeedCommentResponse.builder()
                    .commentId(feedComment.getId())
                    .content(feedComment.getContent())
                    .profileImage(feedComment.getAccount().getProfileImage())
                    .nickname(feedComment.getAccount().getNickname())
                    .createdAt(feedComment.getCreatedAt())
                    .isCommentAuthor(isCommentAuthor)
                    .isFeedAuthor(isFeedAuthor)
                    .isAuthorComment(isAuthorComment)
                    .commentType(feedComment.getParent() == null ? "COMMENT" : "REPLY")
                    .parentCommentNickname(feedComment.getParent() == null ? null : feedComment.getParent().getAccount().getNickname())
                    .parentCommentId(feedComment.getParent() == null ? null : feedComment.getParent().getId())
                    .commentGroupId(feedComment.getCommentGroupId())
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
            Boolean isAuthorComment,
            String commentType,
            String parentCommentNickname,
            Long parentCommentId,
            Long commentGroupId
    ) {
        @Builder
        public NoticeCommentResponse {
        }

        public static NoticeCommentResponse of(NoticeComment noticeComment, Boolean isCommentAuthor, Boolean isNoticeAuthor, Boolean isAuthorComment) {
            return NoticeCommentResponse.builder()
                    .commentId(noticeComment.getId())
                    .content(noticeComment.getContent())
                    .profileImage(noticeComment.getAccount().getProfileImage())
                    .nickname(noticeComment.getAccount().getNickname())
                    .createdAt(noticeComment.getCreatedAt())
                    .isCommentAuthor(isCommentAuthor)
                    .isNoticeAuthor(isNoticeAuthor)
                    .isAuthorComment(isAuthorComment)
                    .commentType(noticeComment.getParent() == null ? "COMMENT" : "REPLY")
                    .parentCommentNickname(noticeComment.getParent() == null ? null : noticeComment.getParent().getAccount().getNickname())
                    .parentCommentId(noticeComment.getParent() == null ? null : noticeComment.getParent().getId())
                    .commentGroupId(noticeComment.getCommentGroupId())
                    .build();
        }
    }

    public record CommentIdResponse(Long commentId) {
    }

    public record CommentMessage(Object result) {
    }
}
