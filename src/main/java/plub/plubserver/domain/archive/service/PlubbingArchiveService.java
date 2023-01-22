package plub.plubserver.domain.archive.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import plub.plubserver.domain.archive.dto.PlubbingArchiveDto.ArchiveCardListResponse;
import plub.plubserver.domain.archive.dto.PlubbingArchiveDto.ArchiveCardResponse;
import plub.plubserver.domain.archive.dto.PlubbingArchiveDto.ArchiveIdResponse;
import plub.plubserver.domain.archive.dto.PlubbingArchiveDto.ArchiveRequest;
import plub.plubserver.domain.archive.model.PlubbingArchive;
import plub.plubserver.domain.archive.model.PlubbingArchiveImage;
import plub.plubserver.domain.archive.repository.PlubbingArchiveRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PlubbingArchiveService {
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
        return ArchiveIdResponse.of(PlubbingArchive.builder().id(1L).build());
    }

    @Transactional
    public ArchiveIdResponse updateArchive(Long plubbingId, Long archiveId, ArchiveRequest archiveRequest) {
        return ArchiveIdResponse.of(PlubbingArchive.builder().id(1L).build());
    }
}
