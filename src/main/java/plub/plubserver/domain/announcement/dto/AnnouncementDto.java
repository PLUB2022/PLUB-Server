package plub.plubserver.domain.announcement.dto;

import lombok.Builder;
import org.springframework.data.domain.Page;
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
                    .build();
        }
    }

    public record AnnouncementListResponse(
            PageResponse<AnnouncementResponse> pageResponse
    ) {
        @Builder
        public AnnouncementListResponse {
        }

        public static AnnouncementListResponse of(Page<AnnouncementResponse> page) {
            return AnnouncementListResponse.builder()
                    .pageResponse(PageResponse.of(page))
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

}
