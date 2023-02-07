package plub.plubserver.domain.notice.dto;

import lombok.Builder;
import plub.plubserver.common.dto.CommentDto.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

public class NoticeDto {
    public record CreateNoticeRequest(
            @NotBlank @Size(max = 15)
            String title,

            @NotBlank @Size(max = 400)
            String content
    ) {
        @Builder
        public CreateNoticeRequest {
        }
    }

    public record UpdateNoticeRequest(
            @NotBlank @Size(max = 15)
            String title,

            @NotBlank @Size(max = 400)
            String content
    ) {
    }

    public record NoticeCardResponse(
            Long noticeId,
            String title,
            String content,
            String createdAt
    ) {
        @Builder
        public NoticeCardResponse {
        }
    }

    public record NoticeListResponse(
            List<NoticeCardResponse> notices
    ) {
        @Builder
        public NoticeListResponse {
        }

        public static NoticeListResponse of(List<NoticeCardResponse> notices) {
            return NoticeListResponse.builder()
                    .notices(notices)
                    .build();
        }
    }

    public record NoticeResponse(
            Long noticeId,
            String title,
            String content,
            String createdAt,
            long likeCount,
            long commentCount,
            List<CommentResponse> comments
    ) {
        @Builder
        public NoticeResponse {
        }
    }

    public record NoticeIdResponse(Long feedId) {
    }

    public record NoticeMessage(Object result) {
    }
}
