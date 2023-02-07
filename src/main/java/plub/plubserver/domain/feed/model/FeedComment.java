package plub.plubserver.domain.feed.model;

import lombok.*;
import plub.plubserver.common.dto.CommentDto.*;
import plub.plubserver.common.model.BaseEntity;
import plub.plubserver.domain.account.model.Account;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FeedComment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "feed_comment_id")
    private Long id;

    private String content;

    @NotNull
    private boolean visibility;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "feed_id")
    private Feed feed;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;

    public void updateFeedComment(UpdateCommentRequest request) {
        this.content = request.content();
    }
}