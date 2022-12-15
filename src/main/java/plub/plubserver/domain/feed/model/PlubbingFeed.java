package plub.plubserver.domain.feed.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import plub.plubserver.common.model.BaseTimeEntity;
import plub.plubserver.domain.plubbing.model.AccountPlubbing;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PlubbingFeed extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "feed_id")
    private Long id;

    private String content;
    private int seq;
    private String date;

    // 피드(다) - 회원_모임페이지(1)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_plubbing_id")
    private AccountPlubbing accountPlubbing;

    // 피드(1) - 피드 사진(다)
    @OneToMany(mappedBy = "feed", cascade = CascadeType.ALL)
    private List<PlubbingFeedImage> feedImageList = new ArrayList<>();
}