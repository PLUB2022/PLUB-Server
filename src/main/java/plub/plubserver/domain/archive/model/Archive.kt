package plub.plubserver.domain.archive.model

import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.OneToMany
import org.hibernate.annotations.Where
import plub.plubserver.common.constant.Visibility
import plub.plubserver.common.model.BaseEntity
import plub.plubserver.domain.account.model.Account
import plub.plubserver.domain.plubbing.model.Plubbing


@Entity
@Where(clause = Visibility.TRUE)
class Archive(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "archive_id")
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plubbing_id")
    val plubbing: Plubbing,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    val account: Account,
    var title: String,
    var sequence: Int
) : BaseEntity() {
    @OneToMany(mappedBy = "archive", cascade = [CascadeType.ALL], orphanRemoval = true)
    var images: MutableList<ArchiveImage> = mutableListOf()

    /**
     * methods
     */
    fun setArchiveImages(images: List<ArchiveImage>) {
        this.images = images.toMutableList()
    }

    fun update(title: String, images: List<ArchiveImage>) {
        this.title = title
        this.images.clear()
        this.images.addAll(images)
    }
}

@Entity
class ArchiveImage(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "archive_image_id")
    val id: Long? = null,
    val image: String = "",

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "archive_id")
    val archive: Archive
)
