package plub.plubserver.domain.announcement;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import plub.plubserver.domain.account.AccountTemplate;
import plub.plubserver.domain.account.exception.AccountException;
import plub.plubserver.domain.account.model.Account;
import plub.plubserver.domain.announcement.dto.AnnouncementDto;
import plub.plubserver.domain.announcement.model.Announcement;
import plub.plubserver.domain.announcement.repository.AnnouncementRepository;
import plub.plubserver.domain.announcement.service.AnnouncementService;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static plub.plubserver.domain.announcement.dto.AnnouncementDto.AnnouncementRequest;

@ExtendWith(MockitoExtension.class)
class AnnouncementServiceTest {

    @InjectMocks
    AnnouncementService announcementService;

    @Mock
    AnnouncementRepository announcementRepository;

    @Test
    @DisplayName("공지사항 생성 성공")
    void createAnnouncement_success() {
        // given
        Account account = AccountTemplate.makeAccountAdmin();
        AnnouncementRequest request = AnnouncementMockUtils.createAnnouncementRequest();
        given(announcementRepository.save(any()))
                .willReturn(request.toEntity());
        // when
        AnnouncementDto.AnnouncementIdResponse announcement = announcementService.createAnnouncement(account, request);

        // then
        assertThat(announcement.announcementId()).isEqualTo(request.title());
    }

    @Test
    @DisplayName("공지사항 생성 실패")
    void createAnnouncement_fail() {
        // given
        Account account = AccountTemplate.makeAccount1();
        AnnouncementRequest request = AnnouncementMockUtils.createAnnouncementRequest();
        // when
        // then
        assertThatThrownBy(() -> announcementService.createAnnouncement(account, request))
                .isInstanceOf(AccountException.class);
    }

    @Test
    @DisplayName("공지사항 수정 성공")
    void updateAnnouncement_success() {
        // given
        Account account = AccountTemplate.makeAccountAdmin();
        AnnouncementRequest request = AnnouncementMockUtils.updateAnnouncementRequest();
        Announcement announcement = request.toEntity();

        given(announcementRepository.findById(any()))
                .willReturn(java.util.Optional.of(announcement));
        // when
        announcementService.updateAnnouncement(announcement.getId(), account, request);

        // then
        assertThat(request.title()).isEqualTo(request.title());
    }

    @Test
    @DisplayName("공지사항 수정 실패")
    void updateAnnouncement_fail() {
        // given
        Account account = AccountTemplate.makeAccount1();
        AnnouncementRequest request = AnnouncementMockUtils.updateAnnouncementRequest();
        Announcement announcement = request.toEntity();

        // when
        // then
        assertThatThrownBy(() -> announcementService.updateAnnouncement(announcement.getId(), account, request))
                .isInstanceOf(AccountException.class);
    }

}