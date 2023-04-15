package plub.plubserver.domain.archive.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import plub.plubserver.common.dto.PageResponse;
import plub.plubserver.common.exception.StatusCode;
import plub.plubserver.domain.account.model.Account;
import plub.plubserver.domain.account.service.AccountService;
import plub.plubserver.domain.archive.dto.ArchiveDto.ArchiveCardResponse;
import plub.plubserver.domain.archive.dto.ArchiveDto.ArchiveIdResponse;
import plub.plubserver.domain.archive.dto.ArchiveDto.ArchiveRequest;
import plub.plubserver.domain.archive.dto.ArchiveDto.ArchiveResponse;
import plub.plubserver.domain.archive.exception.ArchiveException;
import plub.plubserver.domain.archive.model.Archive;
import plub.plubserver.domain.archive.model.ArchiveImage;
import plub.plubserver.domain.archive.repository.ArchiveRepository;
import plub.plubserver.domain.plubbing.model.Plubbing;
import plub.plubserver.domain.plubbing.service.PlubbingService;
import plub.plubserver.domain.report.service.ReportService;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ArchiveService {

    private final AccountService accountService;
    private final ArchiveRepository archiveRepository;
    private final PlubbingService plubbingService;
    private final ReportService reportService;

    /**
     * 아카이브 조회
     */
    private Archive getArchive(Long archiveId) {
        return archiveRepository.findById(archiveId)
                .orElseThrow(() -> new ArchiveException(StatusCode.NOT_FOUND_ARCHIVE));
    }

    // 아카이브 전체 조회
    public PageResponse<ArchiveCardResponse> getArchiveList(
            Account account,
            Long plubbingId,
            Pageable pageable,
            Long cursorId
    ) {
        Plubbing plubbing = plubbingService.getPlubbing(plubbingId);
        log.warn("1");
        plubbingService.checkMember(account, plubbing);
        log.warn("2");
        // 로그인한 사용자를 기반으로 액세스 타입 체크
        Account loginAccount = accountService.getCurrentAccount();
        log.warn("3");
        Page<ArchiveCardResponse> result = archiveRepository
                .findAllByPlubbingId(plubbingId, pageable, cursorId)
                .map(it -> ArchiveCardResponse.of(it, getAccessType(loginAccount, it)));
        log.warn("4");
        Long totalElements = archiveRepository.countAllByPlubbingId(plubbingId);
        log.warn("5");
        return PageResponse.ofCursor(result, totalElements);
    }

    private String getAccessType(Account loginAccount, Archive archive) {
        String accessType = "normal";
        Account host = plubbingService.getHost(archive.getPlubbing().getId());
        if (loginAccount.getId().equals(host.getId())) accessType = "host";
        if (loginAccount.getId().equals(archive.getAccount().getId())) accessType = "author";
        return accessType;
    }

    public ArchiveResponse getArchive(Account loginAccount, Long plubbingId, Long archiveId) {
        Plubbing plubbing = plubbingService.getPlubbing(plubbingId);
        plubbingService.checkMember(loginAccount, plubbing);

        Archive archive = plubbing.getArchiveList().stream()
                .filter(it -> it.getId().equals(archiveId))
                .findFirst()
                .orElseThrow(() -> new ArchiveException(StatusCode.NOT_FOUND_ARCHIVE));

        return ArchiveResponse.of(archive);
    }

    private static List<ArchiveImage> makeArchiveImageList(ArchiveRequest archiveRequest, Archive archive) {
        return archiveRequest.images().stream()
                .map(it -> ArchiveImage.builder()
                        .archive(archive)
                        .image(it)
                        .build()
                )
                .toList();
    }

    /**
     * 아카이브 생성
     */
    @Transactional
    public ArchiveIdResponse createArchive(Account account, Long plubbingId, ArchiveRequest archiveRequest) {
        Account loginAccount = accountService.getAccount(account.getId());
        Plubbing plubbing = plubbingService.getPlubbing(plubbingId);
        plubbingService.checkMember(loginAccount, plubbing);

        int sequence = archiveRepository.findFirstByPlubbingIdOrderBySequenceDesc(plubbingId)
                .map(Archive::getSequence)
                .orElse(0) + 1;

        Archive archive = archiveRepository.save(
                Archive.builder()
                        .title(archiveRequest.title())
                        .account(loginAccount)
                        .plubbing(plubbing)
                        .sequence(sequence)
                        .build()
        );

        // Archive 매핑해서 이미지 엔티티화
        List<ArchiveImage> archiveImages = makeArchiveImageList(archiveRequest, archive);

        // ArchiveImage 매핑
        archive.setImages(archiveImages);

        // Archive 저장
        plubbing.addArchive(archive);
        loginAccount.addArchive(archive);

        return ArchiveIdResponse.of(archive);
    }

    // 호스트, 작성자인지 권한 체크
    private void checkAuthorities(Account loginAccount, Plubbing plubbing, Archive archive) {
        Account account = accountService.getAccount(loginAccount.getId());
        if (!plubbingService.isHost(account, plubbing) && !isArchiveAuthor(account, archive))
            throw new ArchiveException(StatusCode.NOT_ARCHIVE_AUTHOR);
    }

    public Boolean isArchiveAuthor(Account account, Archive archive) {
       return archive.getAccount().getId().equals(account.getId());
    }

    /**
     * 아카이브 수정
     */
    // only for 작성자, 호스트
    @Transactional
    public ArchiveCardResponse updateArchive(
            Account account,
            Long plubbingId,
            Long archiveId,
            ArchiveRequest archiveRequest
    ) {
        Account loginAccount = accountService.getAccount(account.getId());
        Plubbing plubbing = plubbingService.getPlubbing(plubbingId);
        plubbingService.checkMember(loginAccount, plubbing);

        Archive archive = getArchive(archiveId);
        checkAuthorities(loginAccount, plubbing, archive);

        archive.update(
                archiveRequest.title(),
                makeArchiveImageList(archiveRequest, archive)
        );
        String accessType = getAccessType(loginAccount, archive);
        return ArchiveCardResponse.of(archive, accessType);
    }

    /**
     * 아카이브 삭제 (소프트, 하드)
     */
    // only for 작성자, 호스트
    @Transactional
    public ArchiveCardResponse softDeleteArchive(
            Account account,
            Long plubbingId,
            Long archiveId
    ) {
        Account loginAccount = accountService.getAccount(account.getId());
        Plubbing plubbing = plubbingService.getPlubbing(plubbingId);
        plubbingService.checkMember(loginAccount, plubbing);

        Archive archive = getArchive(archiveId);
        checkAuthorities(loginAccount, plubbing, archive);

        archive.softDelete();
        String accessType = getAccessType(loginAccount, archive);
        return ArchiveCardResponse.of(archive, accessType);
    }

    @Transactional
    public void hardDeleteArchive(Long archiveId) {
        archiveRepository.deleteById(archiveId);
    }
}
