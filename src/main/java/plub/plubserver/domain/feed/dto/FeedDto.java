package plub.plubserver.domain.feed.dto;

import lombok.Builder;
import plub.plubserver.common.dto.PageResponse;
import plub.plubserver.domain.account.model.Account;
import plub.plubserver.domain.feed.model.Feed;
import plub.plubserver.domain.feed.model.FeedType;
import plub.plubserver.domain.feed.model.ViewType;
import plub.plubserver.domain.plubbing.model.Plubbing;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static plub.plubserver.domain.plubbing.dto.PlubbingDto.PlubbingInfoResponse;

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
                    .pin(false)
                    .pinedAt(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
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
            Long likeCount,
            Long commentCount,
            String profileImage,
            String nickname,
            Long plubbingId,
            Boolean isAuthor,
            Boolean isHost
    ) {
        @Builder
        public FeedCardResponse {
        }

        public static FeedCardResponse of(Feed feed, Boolean isAuthor, Boolean isHost, Long likeCount, Long commentCount) {
            return FeedCardResponse.builder()
                    .feedId(feed.getId())
                    .feedType(feed.getFeedType().toString())
                    .viewType(feed.getViewType().toString())
                    .title(feed.getTitle())
                    .content(feed.getContent())
                    .feedImage(feed.getFeedImage())
                    .createdAt(feed.getCreatedAt())
                    .pin(feed.isPin())
                    .likeCount(likeCount)
                    .commentCount(commentCount)
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
            String plubbingName,
            String title,
            String content,
            String feedImage,
            String createdAt,
            boolean pin,
            String profileImage,
            String nickname,
            Boolean isAuthor,
            Boolean isHost,
            Long likeCount,
            Long commentCount
    ) {
        @Builder
        public FeedResponse {
        }

        public static FeedResponse of(Feed feed, Boolean isAuthor, Boolean isHost, Long likeCount, Long commentCount) {
            return FeedResponse.builder()
                    .feedId(feed.getId())
                    .feedType(feed.getFeedType().toString())
                    .viewType(feed.getViewType().toString())
                    .plubbingName(feed.getPlubbing().getName())
                    .title(feed.getTitle())
                    .content(feed.getContent())
                    .feedImage(feed.getFeedImage())
                    .createdAt(feed.getCreatedAt())
                    .pin(feed.isPin())
                    .profileImage(feed.getAccount().getProfileImage())
                    .nickname(feed.getAccount().getNickname())
                    .likeCount(likeCount)
                    .commentCount(commentCount)
                    .isAuthor(isAuthor)
                    .isHost(isHost)
                    .build();
        }
    }

    public record FeedIdResponse(Long feedId) {
    }

    public record FeedMessage(Object result) {
    }

    public record MyFeedListResponse(
            PlubbingInfoResponse plubbingInfo,
            PageResponse<FeedCardResponse> myFeedList
    ) {
        @Builder
        public MyFeedListResponse {
        }

        public static MyFeedListResponse of(Plubbing plubbing, PageResponse<FeedCardResponse> myFeedList) {
            return MyFeedListResponse.builder()
                    .plubbingInfo(PlubbingInfoResponse.of(plubbing))
                    .myFeedList(myFeedList)
                    .build();
        }
    }
}