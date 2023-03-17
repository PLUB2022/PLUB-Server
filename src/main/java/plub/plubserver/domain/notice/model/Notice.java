package plub.plubserver.domain.notice.model;

import lombok.*;
import plub.plubserver.common.model.BaseEntity;
import plub.plubserver.domain.account.model.Account;
import plub.plubserver.domain.notice.dto.NoticeDto.*;
import plub.plubserver.domain.plubbing.model.Plubbing;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notice extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notice_id")
    private Long id;

    private String title;
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plubbing_id")
    private Plubbing plubbing;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;

    // 공지(1) - 공지 좋아요(다)
    @OneToMany(mappedBy = "notice", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<NoticeLike> noticeLikeList = new ArrayList<>();

    // 공지(1) - 공지 댓글(다)
    @OneToMany(mappedBy = "notice", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<NoticeComment> noticeCommentList = new ArrayList<>();

    public void updateFeed(UpdateNoticeRequest request) {
        this.title = request.title();
        this.content = request.content();
    }
}
