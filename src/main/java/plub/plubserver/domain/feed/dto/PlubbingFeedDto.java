package plub.plubserver.domain.feed.dto;

import lombok.Builder;
import org.springframework.data.domain.Page;
import plub.plubserver.common.dto.CommentDto.*;
import plub.plubserver.common.dto.PageResponse;
import plub.plubserver.domain.account.model.Account;
import plub.plubserver.domain.feed.model.FeedType;
import plub.plubserver.domain.feed.model.PlubbingFeed;
import plub.plubserver.domain.feed.model.ViewType;

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
                    .viewType(ViewType.NORMAL)
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
            String viewType,
            String title,
            String content,
            String feedImage,
            String createdAt,
            boolean pin,
            String profileImage,
            String nickname,
            Long plubbingId
    ) {
        @Builder
        public FeedCardResponse {
        }

        public static FeedCardResponse of(PlubbingFeed plubbingFeed, Account account) {
            return FeedCardResponse.builder()
                    .feedId(plubbingFeed.getId())
                    .feedType(plubbingFeed.getFeedType().toString())
                    .viewType(plubbingFeed.getViewType().toString())
                    .title(plubbingFeed.getTitle())
                    .content(plubbingFeed.getContent())
                    .feedImage(plubbingFeed.getFeedImage())
                    .createdAt(plubbingFeed.getCreatedAt())
                    .pin(plubbingFeed.isPin())
                    .profileImage(account.getProfileImage())
                    .nickname(account.getNickname())
                    .plubbingId(plubbingFeed.getPlubbing().getId())
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
