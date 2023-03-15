package plub.plubserver.domain.feed.model;

import lombok.*;
import plub.plubserver.common.model.BaseEntity;
import plub.plubserver.domain.account.model.Account;
import plub.plubserver.domain.feed.dto.FeedDto.*;
import plub.plubserver.domain.plubbing.model.Plubbing;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Feed extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "feed_id")
    private Long id;

    private String title;
    private String content;
    private String feedImage;

    @NotNull
    private boolean pin;
    protected String pinedAt;

    @Enumerated(EnumType.STRING)
    private FeedType feedType;
    @Enumerated(EnumType.STRING)
    private ViewType viewType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plubbing_id")
    private Plubbing plubbing;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;

    // 피드(1) - 피드 좋아요(다)
    @OneToMany(mappedBy = "feed", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FeedLike> feedLikeList = new ArrayList<>();

    // 피드(1) - 피드 댓글(다)
    @OneToMany(mappedBy = "feed", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FeedComment> feedCommentList = new ArrayList<>();

    public static Feed createSystemFeed(Plubbing plubbing, String title, String content) {
        return Feed.builder()
                .title(title)
                .content(content)
                .feedImage("")
                .feedType(FeedType.LINE)
                .viewType(ViewType.SYSTEM)
                .pin(false)
                .account(plubbing.getHost())
                .plubbing(plubbing)
                .build();
    }

    public void updateFeed(UpdateFeedRequest request) {
        String contentValue = request.content();
        String feedImageValue = request.feedImage();
        if (this.feedType.equals(FeedType.LINE)) {
            feedImageValue = "";
        } else if (this.feedType.equals(FeedType.PHOTO)) {
            contentValue = "";
        }
        this.title = request.title();
        this.content = contentValue;
        this.feedImage = feedImageValue;
        this.feedType = FeedType.valueOf(request.feedType());
    }

    public void pin() {
        if (this.pin) {
            this.pin = false;
            this.viewType = ViewType.NORMAL;
        }
        else {
            this.pin = true;
            this.viewType = ViewType.PIN;
        }
        this.pinedAt = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public void makeSystem() {
        this.viewType = ViewType.SYSTEM;
    }
}