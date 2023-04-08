package plub.plubserver.domain.archive.dto

import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size
import plub.plubserver.domain.archive.model.Archive

data class ArchiveRequest(
    @field:NotBlank @field:Size(max = 12)
    val title: String,

    @field:Size(max = 10)
    val images: List<String>,
)

data class ArchiveIdResponse(
    val archiveId: Long,
)

data class ArchiveCardResponse(
    val archiveId: Long,
    val title: String,
    val images: List<String>, // limit 3
    val imageCount: Int,
    val sequence: Int,
    val createdAt: String,
    val accessType: String,
) {
    constructor(archive: Archive, accessType: String) : this(
        archiveId = archive.id,
        title = archive.title,
        images = archive.images.take(3),
        imageCount = archive.images.size,
        sequence = archive.sequence,
        createdAt = archive.createdAt,
        accessType = accessType
    )
}

data class ArchiveResponse(
    val title: String,
    val images: List<String>,
    val imageCount: Int,
    val sequence: Int,
    val createdAt: String
) {
    constructor(archive: Archive) : this(
        title = archive.title,
        images = archive.images,
        imageCount = archive.images.size,
        sequence = archive.sequence,
        createdAt = archive.createdAt
    )
}