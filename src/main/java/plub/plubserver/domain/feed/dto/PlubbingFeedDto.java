package plub.plubserver.domain.feed.dto;

import lombok.Builder;
import plub.plubserver.common.dto.CommentDto.*;
import plub.plubserver.domain.account.model.Account;
import plub.plubserver.domain.feed.model.FeedType;
import plub.plubserver.domain.feed.model.PlubbingFeed;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

public class PlubbingFeedDto {

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

        public PlubbingFeed toEntity() {
            return PlubbingFeed.builder()
                    .title(this.title)
                    .content(this.content)
                    .feedImage(this.feedImage)
                    .feedType(FeedType.valueOf(this.feedType))
                    .visibility(true)
                    .pin(false)
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
            String title,
            String content,
            String feedImage,
            String createdAt,
            boolean pin,
            String profileImage,
            String nickname
    ) {
        @Builder
        public FeedCardResponse {
        }

        public static FeedCardResponse of(PlubbingFeed plubbingFeed, Account account) {
            return FeedCardResponse.builder()
                    .feedId(plubbingFeed.getId())
                    .feedType(plubbingFeed.getFeedType().toString())
                    .title(plubbingFeed.getTitle())
                    .content(plubbingFeed.getContent())
                    .feedImage(plubbingFeed.getFeedImage())
                    .createdAt(plubbingFeed.getCreatedAt())
                    .pin(plubbingFeed.isPin())
                    .profileImage(account.getProfileImage())
                    .nickname(account.getNickname())
                    .build();
        }
    }

    public record FeedListResponse(
            List<FeedCardResponse> feeds
    ) {
        @Builder
        public FeedListResponse {
        }

        public static FeedListResponse of(List<FeedCardResponse> feeds) {
            return FeedListResponse.builder()
                    .feeds(feeds)
                    .build();
        }
    }

    public record FeedResponse(
            Long feedId,
            String feedType,
            String title,
            String content,
            String feedImage,
            String createdAt,
            boolean pin,
            String profileImage,
            String nickname,
            long likeCount,
            long commentCount,
            List<CommentResponse> comments
    ) {
        @Builder
        public FeedResponse {
        }
    }

    public record FeedIdResponse(Long feedId) {
    }

    public record FeedMessage(Object result) {
    }
}
