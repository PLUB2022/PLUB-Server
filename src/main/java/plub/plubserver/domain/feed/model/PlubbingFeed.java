package plub.plubserver.domain.feed.model;

import lombok.*;
import plub.plubserver.common.model.BaseTimeEntity;
import plub.plubserver.domain.plubbing.model.AccountPlubbing;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PlubbingFeed extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "feed_id")
    private Long id;

    private String title;
    private String content;
    private String feedImage;

    @NotNull
    private boolean visibility;

    @NotNull
    private boolean pin;

    @Enumerated(EnumType.STRING)
    private FeedType feedType;

    // 피드(다) - 회원_모임페이지(1)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_plubbing_id")
    private AccountPlubbing accountPlubbing;
}