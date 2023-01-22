package plub.plubserver.domain.feed.dto;

import lombok.Builder;
import org.springframework.data.domain.Page;
import plub.plubserver.common.dto.PageResponse;
import plub.plubserver.domain.feed.model.FeedType;
import plub.plubserver.domain.feed.model.PlubbingFeed;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
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

    public record CreateCommentRequest(
            @Size(max = 800)
            String content
    ) {
        @Builder
        public CreateCommentRequest {
        }
    }

    public record UpdateCommentRequest(
            @Size(max = 800)
            String content
    ) {
        @Builder
        public UpdateCommentRequest {
        }
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

        public static FeedCardResponse of(PlubbingFeed plubbingFeed) {
            return FeedCardResponse.builder()
                    .feedId(plubbingFeed.getId())
                    .feedType(plubbingFeed.getFeedType().toString())
                    .title(plubbingFeed.getTitle())
                    .content(plubbingFeed.getContent())
                    .feedImage(plubbingFeed.getFeedImage())
                    .createdAt(plubbingFeed.getCreatedAt())
                    .pin(plubbingFeed.isPin())
                    .profileImage(plubbingFeed.getAccountPlubbing().getAccount().getProfileImage())
                    .nickname(plubbingFeed.getAccountPlubbing().getAccount().getNickname())
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

    public record CommentResponse(
            long commentId,
            String content,
            String profileImage,
            String nickname,
            String createdAt
    ){
        @Builder
        public CommentResponse {
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

    public record CommentIdResponse(Long commentId) {
    }

    public record CommentMessage(Object result) {
    }

}
