package plub.plubserver.domain.archive.model;

import lombok.*;
import plub.plubserver.common.model.BaseTimeEntity;
import plub.plubserver.domain.plubbing.model.Plubbing;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@AllArgsConstructor
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PlubbingArchive extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "plubbing_archive_id")
    private Long id;

    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plubbing_id")
    private Plubbing plubbing;

    @OneToMany(mappedBy = "plubbingArchive", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PlubbingArchiveImage> images = new ArrayList<>();

    /**
     * methods
     */
    public void setImages(List<PlubbingArchiveImage> images) {
        this.images = images;
    }


}
