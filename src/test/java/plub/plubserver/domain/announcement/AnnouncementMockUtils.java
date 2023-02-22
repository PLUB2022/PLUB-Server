package plub.plubserver.domain.announcement;

import plub.plubserver.domain.announcement.model.Announcement;

import static plub.plubserver.domain.announcement.dto.AnnouncementDto.AnnouncementRequest;

public class AnnouncementMockUtils {
    public static AnnouncementRequest createAnnouncementRequest() {
        return AnnouncementRequest.builder()
                .title("title")
                .content("content")
                .build();
    }

    public static AnnouncementRequest updateAnnouncementRequest() {
        return AnnouncementRequest.builder()
                .title("title")
                .content("content")
                .build();
    }

    public static Announcement getMockAnnouncement() {
        return createAnnouncementRequest().toEntity();
    }
}
