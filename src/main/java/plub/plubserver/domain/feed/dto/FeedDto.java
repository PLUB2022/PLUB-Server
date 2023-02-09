package plub.plubserver.domain.feed.dto;

import lombok.Builder;
import plub.plubserver.common.dto.CommentDto.*;
import plub.plubserver.domain.account.model.Account;
import plub.plubserver.domain.feed.model.FeedType;
import plub.plubserver.domain.feed.model.Feed;
import plub.plubserver.domain.feed.model.ViewType;
import plub.plubserver.domain.plubbing.model.Plubbing;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class FeedDto {

    public record CreateFeedRequest(
            @NotBlank @Size(max = 20)
            String title,

            @Size(max = 800)
            String content,

            String feedImage,

            @NotBlank
            String feedType
    ) {
        @Builder
        public CreateFeedRequest {
        }

        public Feed toEntity(Plubbing plubbing, Account account) {
            String contentValue = this.content;
            String feedImageValue = this.feedImage;
            if (this.feedType.equals("LINE")) {
                feedImageValue = "";
            } else if (this.feedType.equals("PHOTO")) {
                contentValue = "";
            }
            return Feed.builder()
                    .title(this.title)
                    .content(contentValue)
                    .feedImage(feedImageValue)
                    .feedType(FeedType.valueOf(this.feedType))
                    .viewType(ViewType.NORMAL)
                    .visibility(true)
                    .pin(false)
                    .pinedAt(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                    .likeCount(0)
                    .commentCount(0)
                    .plubbing(plubbing)
                    .account(account)
                    .build();
        }
    }

    public record UpdateFeedRequest(
            @NotBlank
            String feedType,

            @NotBlank @Size(max = 20)
            String title,

            @Size(max = 800)
            String content,

            String feedImage
    ) {
    }

    public record FeedCardResponse(
            Long feedId,
            String feedType,
            String viewType,
            String title,
            String content,
            String feedImage,
            String createdAt,
            Boolean pin,
            int likeCount,
            int commentCount,
            String profileImage,
            String nickname,
            Long plubbingId,
            Boolean isAuthor,
            Boolean isHost
    ) {
        @Builder
        public FeedCardResponse {
        }

        public static FeedCardResponse of(Feed feed, Boolean isAuthor, Boolean isHost) {
            return FeedCardResponse.builder()
                    .feedId(feed.getId())
                    .feedType(feed.getFeedType().toString())
                    .viewType(feed.getViewType().toString())
                    .title(feed.getTitle())
                    .content(feed.getContent())
                    .feedImage(feed.getFeedImage())
                    .createdAt(feed.getCreatedAt())
                    .pin(feed.isPin())
                    .likeCount(feed.getLikeCount())
                    .commentCount(feed.getCommentCount())
                    .profileImage(feed.getAccount().getProfileImage())
                    .nickname(feed.getAccount().getNickname())
                    .plubbingId(feed.getPlubbing().getId())
                    .isAuthor(isAuthor)
                    .isHost(isHost)
                    .build();
        }
    }

    public record FeedListResponse(
            List<FeedCardResponse> pinedFeedList) {
        @Builder
        public FeedListResponse {
        }

        public static FeedListResponse of(List<FeedCardResponse> pinedFeedList) {
            return FeedListResponse.builder()
                    .pinedFeedList(pinedFeedList)
                    .build();
        }
    }

    public record FeedResponse(
            Long feedId,
            String feedType,
            String viewType,
            String title,
            String content,
            String feedImage,
            String createdAt,
            boolean pin,
            String profileImage,
            String nickname,
            Boolean isAuthor,
            Boolean isHost,
            int likeCount,
            int commentCount,
            List<CommentResponse> comments
    ) {
        @Builder
        public FeedResponse {
        }

        public static FeedResponse of(Feed feed, List<CommentResponse> comments, Boolean isAuthor, Boolean isHost) {
            return FeedResponse.builder()
                    .feedId(feed.getId())
                    .feedType(feed.getFeedType().toString())
                    .viewType(feed.getViewType().toString())
                    .title(feed.getTitle())
                    .content(feed.getContent())
                    .feedImage(feed.getFeedImage())
                    .createdAt(feed.getCreatedAt())
                    .pin(feed.isPin())
                    .profileImage(feed.getAccount().getProfileImage())
                    .nickname(feed.getAccount().getNickname())
                    .likeCount(feed.getLikeCount())
                    .isAuthor(isAuthor)
                    .isHost(isHost)
                    .commentCount(comments.size())
                    .comments(comments)
                    .build();
        }
    }

    public record FeedIdResponse(Long feedId) {
    }

    public record FeedMessage(Object result) {
    }
}
