package plub.plubserver.domain.announcement.dto;

import lombok.Builder;
import plub.plubserver.common.dto.PageResponse;
import plub.plubserver.domain.announcement.model.Announcement;

public class AnnouncementDto {

    public record AnnouncementRequest(
            String title,
            String content
    ) {
        @Builder
        public AnnouncementRequest {
        }

        public Announcement toEntity() {
            return Announcement.builder()
                    .title(title)
                    .content(content)
                    .build();
        }
    }

    public record AnnouncementResponse(
            Long announcementId,
            String title,
            String content,
            String createdAt,
            String updatedAt
    ) {
        @Builder
        public AnnouncementResponse {
        }

        public static AnnouncementResponse of(Announcement announcement) {
            return AnnouncementResponse.builder()
                    .announcementId(announcement.getId())
                    .title(announcement.getTitle())
                    .content(announcement.getContent())
                    .createdAt(announcement.getCreatedAt())
                    .updatedAt(announcement.getModifiedAt())
                    .build();
        }
    }

    public record AnnouncementIdResponse(
            Long announcementId
    ) {
        @Builder
        public AnnouncementIdResponse {
        }
    }

    public record AnnouncementMessage(Object result) {
    }

    public record AnnouncementListResponse(PageResponse<AnnouncementResponse> data) {
    }

}
