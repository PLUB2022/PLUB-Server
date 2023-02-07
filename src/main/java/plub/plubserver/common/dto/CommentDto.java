package plub.plubserver.common.dto;

import lombok.Builder;
import plub.plubserver.domain.account.model.Account;
import plub.plubserver.domain.feed.model.Feed;
import plub.plubserver.domain.feed.model.FeedComment;
import plub.plubserver.domain.notice.model.NoticeComment;

import javax.validation.constraints.Size;

public class CommentDto {
    public record CreateCommentRequest(
            @Size(max = 100)
            String content
    ) {
        @Builder
        public CreateCommentRequest {
        }

        public FeedComment toEntity(Feed feed, Account account) {
            return FeedComment.builder()
                    .content(this.content)
                    .visibility(true)
                    .feed(feed)
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

    public record CommentResponse(
            long commentId,
            String content,
            String profileImage,
            String nickname,
            String createdAt
    ) {
        @Builder
        public CommentResponse {
        }

        public static CommentResponse ofFeedComment(FeedComment feedComment) {
            return CommentResponse.builder()
                    .commentId(feedComment.getId())
                    .content(feedComment.getContent())
                    .profileImage(feedComment.getAccount().getProfileImage())
                    .nickname(feedComment.getAccount().getNickname())
                    .createdAt(feedComment.getCreatedAt())
                    .build();
        }

        public static CommentResponse ofNoticeComment(NoticeComment noticeComment) {
            return CommentResponse.builder()
                    .commentId(noticeComment.getId())
                    .content(noticeComment.getContent())
                    .profileImage(noticeComment.getAccount().getProfileImage())
                    .nickname(noticeComment.getAccount().getNickname())
                    .createdAt(noticeComment.getCreatedAt())
                    .build();
        }
    }

    public record CommentIdResponse(Long commentId) {
    }

    public record CommentMessage(Object result) {
    }
}
