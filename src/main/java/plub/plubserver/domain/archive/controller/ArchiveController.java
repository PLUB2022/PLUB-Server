package plub.plubserver.domain.archive.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;
import plub.plubserver.common.dto.ApiResponse;
import plub.plubserver.common.dto.PageResponse;
import plub.plubserver.domain.account.model.Account;
import plub.plubserver.domain.account.service.AccountService;
import plub.plubserver.domain.archive.dto.ArchiveDto.ArchiveCardResponse;
import plub.plubserver.domain.archive.dto.ArchiveDto.ArchiveIdResponse;
import plub.plubserver.domain.archive.dto.ArchiveDto.ArchiveRequest;
import plub.plubserver.domain.archive.dto.ArchiveDto.ArchiveResponse;
import plub.plubserver.domain.archive.service.ArchiveService;

import javax.validation.Valid;

import static plub.plubserver.common.dto.ApiResponse.success;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/plubbings/{plubbingId}/archives")
@Api(tags = "아카이브 API", hidden = true)
public class ArchiveController {

    private final ArchiveService archiveService;
    private final AccountService accountService;

    @ApiOperation(value = "아카이브 전체 조회")
    @GetMapping
    public ApiResponse<PageResponse<ArchiveCardResponse>> getArchiveList(
            @PathVariable Long plubbingId,
            @RequestParam(required = false) Long cursorId,
            @PageableDefault Pageable pageable
    ) {
        Account loginAccount = accountService.getCurrentAccount();
        return success(archiveService.getArchiveList(loginAccount, plubbingId, pageable, cursorId));
    }

    @ApiOperation(value = "아카이브 상세 조회")
    @GetMapping("/{archiveId}")
    public ApiResponse<ArchiveResponse> getArchive(
            @PathVariable Long plubbingId,
            @PathVariable Long archiveId
    ) {
        Account loginAccount = accountService.getCurrentAccount();
        return success(archiveService.getArchive(loginAccount, plubbingId, archiveId));
    }

    @ApiOperation(value = "아카이브 생성")
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

    @ApiOperation(value = "아카이브 수정")
    @PutMapping("/{archiveId}")
    public ApiResponse<ArchiveCardResponse> updateArchive(
            @PathVariable("plubbingId") Long plubbingId,
            @PathVariable("archiveId") Long archiveId,
            @Valid @RequestBody ArchiveRequest archiveRequest
    ) {
        Account loginAccount = accountService.getCurrentAccount();
        return success(
                archiveService.updateArchive(loginAccount, plubbingId, archiveId, archiveRequest)
        );
    }

    @ApiOperation(value = "아카이브 삭제")
    @DeleteMapping("/{archiveId}")
    public ApiResponse<ArchiveCardResponse> deleteArchive(
            @PathVariable("plubbingId") Long plubbingId,
            @PathVariable("archiveId") Long archiveId
    ) {
        Account loginAccount = accountService.getCurrentAccount();
        return success(
                archiveService.softDeleteArchive(loginAccount, plubbingId, archiveId)
        );
    }
}
