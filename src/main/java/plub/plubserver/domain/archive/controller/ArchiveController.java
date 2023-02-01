package plub.plubserver.domain.archive.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;
import plub.plubserver.common.dto.ApiResponse;
import plub.plubserver.domain.account.model.Account;
import plub.plubserver.domain.account.service.AccountService;
import plub.plubserver.domain.archive.dto.ArchiveDto.ArchiveCardListResponse;
import plub.plubserver.domain.archive.dto.ArchiveDto.ArchiveCardResponse;
import plub.plubserver.domain.archive.dto.ArchiveDto.ArchiveIdResponse;
import plub.plubserver.domain.archive.dto.ArchiveDto.ArchiveRequest;
import plub.plubserver.domain.archive.service.ArchiveService;

import javax.validation.Valid;

import static plub.plubserver.common.dto.ApiResponse.success;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/plubbings/{plubbingId}/archives")
public class ArchiveController {

    private final ArchiveService archiveService;
    private final AccountService accountService;

    @GetMapping
    public ApiResponse<ArchiveCardListResponse> getArchiveList(
            @PathVariable Long plubbingId,
            @PageableDefault Pageable pageable
    ) {
        return success(archiveService.getArchiveList(plubbingId, pageable));
    }

    @GetMapping("/{archiveId}")
    public ApiResponse<ArchiveCardResponse> getArchive(
            @PathVariable Long plubbingId,
            @PathVariable Long archiveId
    ) {
        return success(archiveService.getArchive(plubbingId, archiveId));
    }

    @PostMapping
    public ApiResponse<ArchiveIdResponse> createArchive(
            @PathVariable Long plubbingId,
            @Valid @RequestBody ArchiveRequest archiveRequest
    ) {
        Account loginAccount = accountService.getCurrentAccount();
        return success(
                archiveService.createArchive(loginAccount, plubbingId, archiveRequest)
        );
    }

    @PutMapping("/{archiveId}")
    public ApiResponse<ArchiveIdResponse> updateArchive(
            @PathVariable("plubbingId") Long plubbingId,
            @PathVariable("archiveId") Long archiveId,
            @Valid @RequestBody ArchiveRequest archiveRequest
    ) {
        Account loginAccount = accountService.getCurrentAccount();
        return success(
                archiveService.updateArchive(loginAccount, plubbingId, archiveId, archiveRequest)
        );
    }

    @DeleteMapping("/{archiveId}")
    public ApiResponse<ArchiveIdResponse> deleteArchive(
            @PathVariable("plubbingId") Long plubbingId,
            @PathVariable("archiveId") Long archiveId
    ) {
        Account loginAccount = accountService.getCurrentAccount();
        return success(
                archiveService.softDeleteArchive(loginAccount, plubbingId, archiveId)
        );
    }
}
