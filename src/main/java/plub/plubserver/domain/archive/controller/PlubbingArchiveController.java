package plub.plubserver.domain.archive.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;
import plub.plubserver.common.dto.ApiResponse;
import plub.plubserver.domain.archive.dto.PlubbingArchiveDto.ArchiveCardListResponse;
import plub.plubserver.domain.archive.dto.PlubbingArchiveDto.ArchiveCardResponse;
import plub.plubserver.domain.archive.dto.PlubbingArchiveDto.ArchiveIdResponse;
import plub.plubserver.domain.archive.dto.PlubbingArchiveDto.ArchiveRequest;
import plub.plubserver.domain.archive.service.PlubbingArchiveService;

import javax.validation.Valid;

import static plub.plubserver.common.dto.ApiResponse.success;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/plubbings/{plubbingId}/archives")
public class PlubbingArchiveController {

    private final PlubbingArchiveService plubbingArchiveService;

    @GetMapping
    public ApiResponse<ArchiveCardListResponse> getArchiveList(
            @PathVariable Long plubbingId,
            @PageableDefault Pageable pageable
    ) {
        return success(plubbingArchiveService.getArchiveList(plubbingId, pageable));
    }

    @GetMapping("/{archiveId}")
    public ApiResponse<ArchiveCardResponse> getArchive(
            @PathVariable Long plubbingId,
            @PathVariable Long archiveId
    ) {
        return success(plubbingArchiveService.getArchive(plubbingId, archiveId));
    }

    @PostMapping
    public ApiResponse<ArchiveIdResponse> createArchive(
            @PathVariable Long plubbingId,
            @Valid @RequestBody ArchiveRequest archiveRequest
    ) {
        return success(
                plubbingArchiveService.createArchive(plubbingId, archiveRequest)
        );
    }

    @PutMapping("/{archiveId}")
    public ApiResponse<ArchiveIdResponse> updateArchive(
            @PathVariable("plubbingId") Long plubbingId,
            @PathVariable("archiveId") Long archiveId,
            @Valid @RequestBody ArchiveRequest archiveRequest
    ) {
        return success(
                plubbingArchiveService.updateArchive(plubbingId, archiveId, archiveRequest)
        );
    }
}
