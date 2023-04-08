package plub.plubserver.domain.archive.model

import javax.persistence.Column
import javax.persistence.Convert
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import org.hibernate.annotations.Where
import plub.plubserver.common.constant.Visibility
import plub.plubserver.common.model.BaseEntity
import plub.plubserver.domain.account.model.Account
import plub.plubserver.domain.plubbing.model.Plubbing
import plub.plubserver.util.ImageUrlsConverter


@Entity
@Where(clause = Visibility.TRUE)
class Archive(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "archive_id")
    val id: Long = 0L,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plubbing_id")
    val plubbing: Plubbing,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    val account: Account,
    title: String,
    sequence: Int,
    imageUrls: List<String> = emptyList(),
) : BaseEntity() {
    var title: String = title
        protected set
    var sequence: Int = sequence
        protected set
    @Convert(converter = ImageUrlsConverter::class)
    @Column(columnDefinition = "TEXT")
    var images: List<String> = imageUrls
        protected set

    /**
     * methods
     */
    fun updateArchive(
        title: String? = null,
        images: List<String>? = null
    ) {
        title?.let { this.title = it }
        images?.let { this.images = it }
    }
}

//@Entity
//class ArchiveImage(
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name = "archive_image_id")
//    val id: Long? = null,
//    val image: String = "",
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "archive_id")
//    val archive: Archive
//)
