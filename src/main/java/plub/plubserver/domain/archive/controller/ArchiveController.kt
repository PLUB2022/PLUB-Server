package plub.plubserver.domain.archive.controller

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import javax.validation.Valid
import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import plub.plubserver.common.dto.ApiResponse
import plub.plubserver.common.dto.ApiResponse.success
import plub.plubserver.common.dto.PageResponse
import plub.plubserver.domain.account.service.AccountService
import plub.plubserver.domain.archive.dto.ArchiveDto.ArchiveCardResponse
import plub.plubserver.domain.archive.dto.ArchiveDto.ArchiveIdResponse
import plub.plubserver.domain.archive.dto.ArchiveDto.ArchiveRequest
import plub.plubserver.domain.archive.dto.ArchiveDto.ArchiveResponse
import plub.plubserver.domain.archive.service.ArchiveService

@RestController
@RequestMapping("/api/plubbings/{plubbingId}/archives")
@Api(tags = ["아카이브 API"], hidden = true)
class ArchiveController(
    private val archiveService: ArchiveService,
    private val accountService: AccountService
) {

    @ApiOperation(value = "아카이브 전체 조회")
    @GetMapping
    fun getArchiveList(
        @PathVariable plubbingId: Long,
        @RequestParam(required = false) cursorId: Long?,
        pageable: Pageable
    ): ApiResponse<PageResponse<ArchiveCardResponse>> {
        val loginAccount = accountService.currentAccount
        return success(
            archiveService.getArchiveList(loginAccount, plubbingId, pageable, cursorId)
        )
    }

    @ApiOperation(value = "아카이브 상세 조회")
    @GetMapping("/{archiveId}")
    fun getArchive(
        @PathVariable plubbingId: Long,
        @PathVariable archiveId: Long
    ): ApiResponse<ArchiveResponse> {
        val loginAccount = accountService.currentAccount
        return success(archiveService.getArchive(loginAccount, plubbingId, archiveId))
    }

    @ApiOperation(value = "아카이브 생성")
    @PostMapping
    fun createArchive(
        @PathVariable plubbingId: Long,
        @Valid @RequestBody archiveRequest: ArchiveRequest
    ): ApiResponse<ArchiveIdResponse> {
        val loginAccount = accountService.currentAccount
        return success(archiveService.createArchive(loginAccount, plubbingId, archiveRequest))
    }

    @ApiOperation(value = "아카이브 수정")
    @PutMapping("/{archiveId}")
    fun updateArchive(
        @PathVariable plubbingId: Long,
        @PathVariable archiveId: Long,
        @Valid @RequestBody archiveRequest: ArchiveRequest
    ): ApiResponse<ArchiveCardResponse> {
        val loginAccount = accountService.currentAccount
        return success(
            archiveService.updateArchive(loginAccount, plubbingId, archiveId, archiveRequest)
        )
    }

    @ApiOperation(value = "아카이브 삭제")
    @DeleteMapping("/{archiveId}")
    fun deleteArchive(
        @PathVariable plubbingId: Long,
        @PathVariable archiveId: Long
    ): ApiResponse<ArchiveCardResponse> {
        val loginAccount = accountService.currentAccount
        return success(
            archiveService.softDeleteArchive(loginAccount, plubbingId, archiveId)
        )
    }
}