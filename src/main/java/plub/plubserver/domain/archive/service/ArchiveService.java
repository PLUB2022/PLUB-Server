package plub.plubserver.domain.archive.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import plub.plubserver.domain.account.model.Account;
import plub.plubserver.domain.account.service.AccountService;
import plub.plubserver.domain.archive.config.ArchiveCode;
import plub.plubserver.domain.archive.dto.ArchiveDto.ArchiveCardListResponse;
import plub.plubserver.domain.archive.dto.ArchiveDto.ArchiveCardResponse;
import plub.plubserver.domain.archive.dto.ArchiveDto.ArchiveIdResponse;
import plub.plubserver.domain.archive.dto.ArchiveDto.ArchiveRequest;
import plub.plubserver.domain.archive.exception.ArchiveException;
import plub.plubserver.domain.archive.model.Archive;
import plub.plubserver.domain.archive.model.ArchiveImage;
import plub.plubserver.domain.archive.repository.ArchiveRepository;
import plub.plubserver.domain.plubbing.model.Plubbing;
import plub.plubserver.domain.plubbing.service.PlubbingService;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ArchiveService {

    private final AccountService accountService;
    private final ArchiveRepository archiveRepository;
    private final PlubbingService plubbingService;

    private Archive getArchive(Long archiveId) {
        return archiveRepository.findById(archiveId)
                .orElseThrow(() -> new ArchiveException(ArchiveCode.NOT_FOUND_ARCHIVE));
    }

    public ArchiveCardListResponse getArchiveList(Long plubbingId, Pageable pageable) {
        List<ArchiveCardResponse> archiveCardList = new ArrayList<>();
        archiveCardList.add(ArchiveCardResponse.builder()
                .archiveId(1L)
                .title("title")
                .image("image")
                .imageCount(1)
                .sequence(1)
                .createdAt("2021-08-01 00:00:00")
                .build());
        Page<ArchiveCardResponse> archiveCardPage = new PageImpl<>(archiveCardList, pageable, 0);
        return ArchiveCardListResponse.of(archiveCardPage);
    }

    public ArchiveCardResponse getArchive(Long plubbingId, Long archiveId) {
        Account account = accountService.getCurrentAccount();
        Plubbing plubbing = account.getPlubbing(plubbingId);

        Archive archive = plubbing.getArchives().stream()
                .filter(it -> it.getId().equals(archiveId))
                .findFirst()
                .orElseThrow(() -> new ArchiveException(ArchiveCode.NOT_FOUND_ARCHIVE));

        return ArchiveCardResponse.of(archive);
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

    @Transactional
    public ArchiveIdResponse createArchive(
            Account loginAccount,
            Long plubbingId,
            ArchiveRequest archiveRequest
    ) {
        Plubbing plubbing = loginAccount.getPlubbing(plubbingId);

        Archive archive = archiveRepository.save(
                Archive.builder()
                        .title(archiveRequest.title())
                        .account(loginAccount)
                        .plubbing(plubbing)
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
    private void checkAuthorities(Account loginAccount, Long plubbingId, Long archiveId) {
        // 호스트 체크
        plubbingService.checkHost(plubbingId);

        // 작성자 체크
        if (loginAccount.getPlubbing(plubbingId).getArchives().stream()
                .noneMatch(it -> it.getId().equals(archiveId)))
            throw new ArchiveException(ArchiveCode.IS_NOT_WRITER);
    }

    // only for 작성자, 호스트
    @Transactional
    public ArchiveIdResponse updateArchive(
            Account loginAccount,
            Long plubbingId,
            Long archiveId,
            ArchiveRequest archiveRequest
    ) {
        checkAuthorities(loginAccount, plubbingId, archiveId);

        Archive archive = getArchive(archiveId);

        archive.update(
                archiveRequest.title(),
                makeArchiveImageList(archiveRequest, archive)
        );

        return ArchiveIdResponse.of(archive);
    }

    // only for 작성자, 호스트
    @Transactional
    public ArchiveIdResponse softDeleteArchive(Account loginAccount, Long plubbingId, Long archiveId) {
        checkAuthorities(loginAccount, plubbingId, archiveId);

        Archive archive = getArchive(archiveId);
        archive.softDelete();

        return ArchiveIdResponse.of(archive);
    }

    @Transactional
    public void hardDeleteArchive(Long archiveId) {
        archiveRepository.deleteById(archiveId);
    }
}
