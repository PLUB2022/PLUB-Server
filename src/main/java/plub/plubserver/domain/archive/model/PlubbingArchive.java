package plub.plubserver.domain.archive.model;

import lombok.*;
import plub.plubserver.common.model.BaseTimeEntity;

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
    @OneToMany(mappedBy = "plubbingArchive", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PlubbingArchiveImage> images = new ArrayList<>();

    /**
     * methods
     */


}
