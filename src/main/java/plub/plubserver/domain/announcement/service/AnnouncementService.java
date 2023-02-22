package plub.plubserver.domain.announcement.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import plub.plubserver.common.exception.StatusCode;
import plub.plubserver.domain.account.model.Account;
import plub.plubserver.domain.account.service.AccountService;
import plub.plubserver.domain.announcement.exception.AnnouncementException;
import plub.plubserver.domain.announcement.model.Announcement;
import plub.plubserver.domain.announcement.repository.AnnouncementRepository;

import static plub.plubserver.domain.announcement.dto.AnnouncementDto.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AnnouncementService {

    private final AnnouncementRepository announcementRepository;
    private final AccountService accountService;

    // 공지 생성
    @Transactional
    public AnnouncementIdResponse createAnnouncement(Account account, AnnouncementRequest request) {
        // 어드민인지 확인
        account.isAdmin();
        Announcement announcement = request.toEntity();
        announcementRepository.save(announcement);
        return new AnnouncementIdResponse(announcement.getId());
    }

    // 공지 전체 조회
    public AnnouncementListResponse getAnnouncementList(Pageable pageable) {
        Page<AnnouncementResponse> announcementPage = announcementRepository.findAll(pageable)
                .map(AnnouncementResponse::of);
        return AnnouncementListResponse.of(announcementPage);
    }

    // 공지 상세 조회
    public AnnouncementResponse getAnnouncement(Long announcementId) {
        Announcement announcement = announcementRepository.findById(announcementId)
                .orElseThrow(() -> new AnnouncementException(StatusCode.NOT_FOUND_ANNOUNCEMENT));
        return AnnouncementResponse.of(announcement);
    }

    // 공지 수정
    @Transactional
    public AnnouncementIdResponse updateAnnouncement(Long announcementId, Account account, AnnouncementRequest request) {
        account.isAdmin();
        Announcement announcement = announcementRepository.findById(announcementId)
                .orElseThrow(() -> new AnnouncementException(StatusCode.NOT_FOUND_ANNOUNCEMENT));
        announcement.updateAnnouncement(request.title(), request.content());
        return new AnnouncementIdResponse(announcement.getId());
    }

    // 공지 삭제
    @Transactional
    public AnnouncementMessage softDeleteAnnouncement(Long announcementId, Account account) {
        account.isAdmin();
        Announcement announcement = announcementRepository.findById(announcementId)
                .orElseThrow(() -> new AnnouncementException(StatusCode.NOT_FOUND_ANNOUNCEMENT));
        announcement.softDelete();
        return new AnnouncementMessage("soft delete success");
    }
}
