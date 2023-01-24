package plub.plubserver.domain.archive.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import plub.plubserver.domain.account.model.Account;
import plub.plubserver.domain.account.service.AccountService;
import plub.plubserver.domain.archive.dto.PlubbingArchiveDto.ArchiveCardListResponse;
import plub.plubserver.domain.archive.dto.PlubbingArchiveDto.ArchiveCardResponse;
import plub.plubserver.domain.archive.dto.PlubbingArchiveDto.ArchiveIdResponse;
import plub.plubserver.domain.archive.dto.PlubbingArchiveDto.ArchiveRequest;
import plub.plubserver.domain.archive.model.PlubbingArchive;
import plub.plubserver.domain.archive.model.PlubbingArchiveImage;
import plub.plubserver.domain.archive.repository.PlubbingArchiveRepository;
import plub.plubserver.domain.plubbing.model.Plubbing;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PlubbingArchiveService {

    private final AccountService accountService;
    private final PlubbingArchiveRepository plubbingArchiveRepository;

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
        return ArchiveCardResponse.of(PlubbingArchive.builder()
                .id(1L)
                .title("title")
                .images(new ArrayList<PlubbingArchiveImage>() {{
                    add(PlubbingArchiveImage.builder()
                            .image("image")
                            .build());
                }})
                .build());
    }


    @Transactional
    public ArchiveIdResponse createArchive(Long plubbingId, ArchiveRequest archiveRequest) {
        Account account = accountService.getCurrentAccount();
        Plubbing plubbing = account.getPlubbing(plubbingId);

        PlubbingArchive plubbingArchive = plubbingArchiveRepository.save(
                PlubbingArchive.builder().title(archiveRequest.title()).build()
        );

        // PlubbingArchive 매핑해서 이미지 엔티티화
        List<PlubbingArchiveImage> archiveImages = archiveRequest.images().stream()
                .map(it -> PlubbingArchiveImage.builder()
                        .plubbingArchive(plubbingArchive)
                        .image(it)
                        .build()
                )
                .toList();

        // PlubbingArchiveImage 매핑
        plubbingArchive.setImages(archiveImages);

        // PlubbingArchive 저장
        plubbing.addArchive(plubbingArchive);

        return ArchiveIdResponse.of(plubbingArchive);
    }

    @Transactional
    public ArchiveIdResponse updateArchive(Long plubbingId, Long archiveId, ArchiveRequest archiveRequest) {
        return ArchiveIdResponse.of(PlubbingArchive.builder().id(1L).build());
    }
}
