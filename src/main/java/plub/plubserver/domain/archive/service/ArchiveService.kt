package plub.plubserver.domain.archive.service

import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import plub.plubserver.common.dto.PageResponse
import plub.plubserver.common.exception.ArchiveException
import plub.plubserver.common.exception.StatusCode
import plub.plubserver.domain.account.model.Account
import plub.plubserver.domain.account.service.AccountService
import plub.plubserver.domain.archive.dto.ArchiveDto.ArchiveCardResponse
import plub.plubserver.domain.archive.dto.ArchiveDto.ArchiveIdResponse
import plub.plubserver.domain.archive.dto.ArchiveDto.ArchiveRequest
import plub.plubserver.domain.archive.dto.ArchiveDto.ArchiveResponse
import plub.plubserver.domain.archive.model.Archive
import plub.plubserver.domain.archive.model.ArchiveImage
import plub.plubserver.domain.archive.repository.ArchiveRepository
import plub.plubserver.domain.plubbing.model.Plubbing
import plub.plubserver.domain.plubbing.service.PlubbingService


@Service
@Transactional(readOnly = true)
class ArchiveService(
    private val archiveRepository: ArchiveRepository,
    private val accountService: AccountService,
    private val plubbingService: PlubbingService
) {

    /**
     * 아카이브 조회
     */
    private fun getArchive(archiveId: Long) = archiveRepository
        .findById(archiveId)
        .orElseThrow { ArchiveException(StatusCode.NOT_FOUND_ARCHIVE) }

    // 아카이브 전체 조회
    fun getArchiveList(
        account: Account,
        plubbingId: Long,
        pageable: Pageable,
        cursorId: Long?,
    ): PageResponse<ArchiveCardResponse> {
        val plubbing = plubbingService.getPlubbing(plubbingId)
        plubbingService.checkMember(account, plubbing)
        // 로그인한 사용자를 기반으로 액세스 타입 체크
        val loginAccount = accountService.currentAccount
        val result = archiveRepository
            .findAllByPlubbingId(plubbingId, pageable, cursorId)
            .map {
                ArchiveCardResponse.of(
                    it,
                    getAccessType(loginAccount, it)
                )
            }
        val totalElements = archiveRepository.countAllByPlubbingId(plubbingId)
        return PageResponse.ofCursor(result, totalElements)
    }

    private fun getAccessType(loginAccount: Account, archive: Archive): String {
        var accessType = "normal"
        val host = plubbingService.getHost(archive.plubbing.id)
        if (loginAccount.id == host.id) accessType = "host"
        if (loginAccount.id == archive.account.id) accessType = "author"
        return accessType
    }

    fun getArchive(
        loginAccount: Account,
        plubbingId: Long,
        archiveId: Long,
    ): ArchiveResponse {
        val plubbing = plubbingService.getPlubbing(plubbingId)
        plubbingService.checkMember(loginAccount, plubbing)
        val archive = plubbing.archives
            .findLast { it.id == archiveId }
            ?: throw ArchiveException(StatusCode.NOT_FOUND_ARCHIVE)
        return ArchiveResponse.of(archive)
    }

    private fun ArchiveRequest.toArchiveImageList(archive: Archive) = this.images
        .map { ArchiveImage(archive = archive, image = it) }
        .toList()

    /**
     * 아카이브 생성
     */
    @Transactional
    fun createArchive(
        account: Account,
        plubbingId: Long,
        archiveRequest: ArchiveRequest,
    ): ArchiveIdResponse {
        val loginAccount = accountService.getAccount(account.id)
        val plubbing = plubbingService.getPlubbing(plubbingId)
        plubbingService.checkMember(loginAccount, plubbing)

        val sequence = archiveRepository.findFirstByPlubbingIdOrderBySequenceDesc(plubbingId)
            .map(Archive::sequence)
            .orElse(0) + 1

        val archive = archiveRepository.save(
            Archive(
                title = archiveRequest.title,
                account = loginAccount,
                plubbing = plubbing,
                sequence = sequence
            )
        )

        // Archive 매핑해서 이미지 엔티티화
        val archiveImages = archiveRequest.toArchiveImageList(archive)

        // ArchiveImage 매핑
        archive.setArchiveImages(archiveImages)

        // Archive 저장
        plubbing.addArchive(archive)
        loginAccount.addArchive(archive)

        return ArchiveIdResponse.of(archive)
    }

    // 호스트, 작성자인지 권한 체크
    private fun checkAuthorities(loginAccount: Account, plubbing: Plubbing, archive: Archive) {
        val account = accountService.getAccount(loginAccount.id)
        if (!plubbingService.isHost(account, plubbing) &&
            archive.account.id != account.id // isArchiveAuthor
        ) throw ArchiveException(StatusCode.NOT_ARCHIVE_AUTHOR)
    }


    /**
     * 아카이브 수정
     */
    // only for 작성자, 호스트
    @Transactional
    fun updateArchive(
        account: Account,
        plubbingId: Long,
        archiveId: Long,
        archiveRequest: ArchiveRequest,
    ): ArchiveCardResponse {
        val loginAccount = accountService.getAccount(account.id)
        val plubbing = plubbingService.getPlubbing(plubbingId)
        plubbingService.checkMember(loginAccount, plubbing)
        val archive = getArchive(archiveId)
        checkAuthorities(loginAccount, plubbing, archive)

        archive.update(
            archiveRequest.title,
            archiveRequest.toArchiveImageList(archive)
        )
        val accessType = getAccessType(loginAccount, archive)
        return ArchiveCardResponse.of(archive, accessType)
    }

    /**
     * 아카이브 삭제 (소프트, 하드)
     */
    // only for 작성자, 호스트
    @Transactional
    fun softDeleteArchive(
        account: Account,
        plubbingId: Long,
        archiveId: Long,
    ): ArchiveCardResponse {
        val loginAccount = accountService.getAccount(account.id)
        val plubbing = plubbingService.getPlubbing(plubbingId)
        plubbingService.checkMember(loginAccount, plubbing)
        val archive = getArchive(archiveId)
        checkAuthorities(loginAccount, plubbing, archive)
        archive.softDelete()
        val accessType = getAccessType(loginAccount, archive)
        return ArchiveCardResponse.of(archive, accessType)
    }

    @Transactional
    fun hardDeleteArchive(archiveId: Long) {
        archiveRepository.deleteById(archiveId)
    }
}