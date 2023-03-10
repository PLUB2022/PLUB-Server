package plub.plubserver.common.dummy;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import plub.plubserver.domain.announcement.model.Announcement;
import plub.plubserver.domain.announcement.repository.AnnouncementRepository;

import javax.annotation.PostConstruct;

@Slf4j
@Component("announcementDummy")
@RequiredArgsConstructor
@Transactional
public class AnnouncementDummy {

    private final AnnouncementRepository announcementRepository;

    @PostConstruct
    public void init() {
        if (announcementRepository.count() > 0) {
            log.info("[00] 공지사항이 이미 존재합니다");
            return;
        }

        for (int i = 0; i < 10; i++) {
            Announcement announcement = Announcement.builder()
                    .title("공지사항 제목" + i)
                    .content("공지사항 내용" + i)
                    .build();

            announcementRepository.save(announcement);
        }
        log.info("[00] 공지사항이 생성되었습니다");
    }
}
