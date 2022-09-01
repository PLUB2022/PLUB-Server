package plub.plubserver.domain.archive.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import plub.plubserver.common.model.BaseTimeEntity;
import plub.plubserver.domain.activity.model.AccountPlubing;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Archive extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "archive_id")
    private Long id;

    private String content;
    private int seq;
    private String date;

    // 아카이브(다) - 회원_모임페이지(1)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_plubing_id")
    private AccountPlubing accountPlubing;

    // 아카이브(1) - 아카이브 사진(다)
    @OneToMany(mappedBy = "archive", cascade = CascadeType.ALL)
    private List<ArchiveImage> archiveImageList = new ArrayList<>();
}