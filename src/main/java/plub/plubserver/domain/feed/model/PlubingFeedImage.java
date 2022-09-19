package plub.plubserver.domain.feed.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;


@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PlubingFeedImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "feed_image_id")
    private Long id;

    private String archiveImg;

    // 피드 사진(다) - 피드(1)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "feed_id")
    private PlubingFeed feed;
}