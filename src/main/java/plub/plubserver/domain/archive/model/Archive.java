package plub.plubserver.domain.archive.model;

import lombok.*;
import org.hibernate.annotations.Where;
import plub.plubserver.common.constant.Visibility;
import plub.plubserver.common.model.BaseEntity;
import plub.plubserver.domain.account.model.Account;
import plub.plubserver.domain.plubbing.model.Plubbing;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@AllArgsConstructor
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Where(clause = Visibility.TRUE)
public class Archive extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "archive_id")
    private Long id;

    private String title;
//    private boolean visibility;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plubbing_id")
    private Plubbing plubbing;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;

    @OneToMany(mappedBy = "archive", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ArchiveImage> images = new ArrayList<>();

    /**
     * methods
     */
    public void setImages(List<ArchiveImage> images) {
        this.images = images;
    }

    public void update(String title, List<ArchiveImage> images) {
        this.title = title;
        this.images.clear();
        this.images.addAll(images);
    }


}
