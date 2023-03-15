package plub.plubserver.domain.account.model;

import lombok.*;
import plub.plubserver.common.exception.StatusCode;
import plub.plubserver.common.model.BaseEntity;
import plub.plubserver.domain.account.exception.AccountException;
import plub.plubserver.domain.archive.model.Archive;
import plub.plubserver.domain.calendar.model.Calendar;
import plub.plubserver.domain.calendar.model.CalendarAttend;
import plub.plubserver.domain.feed.model.Feed;
import plub.plubserver.domain.feed.model.FeedComment;
import plub.plubserver.domain.feed.model.FeedLike;
import plub.plubserver.domain.notice.model.Notice;
import plub.plubserver.domain.notice.model.NoticeComment;
import plub.plubserver.domain.notice.model.NoticeLike;
import plub.plubserver.domain.notification.model.Notification;
import plub.plubserver.domain.plubbing.exception.PlubbingException;
import plub.plubserver.domain.plubbing.model.AccountPlubbing;
import plub.plubserver.domain.plubbing.model.Plubbing;
import plub.plubserver.domain.recruit.model.AppliedAccount;
import plub.plubserver.domain.recruit.model.Bookmark;
import plub.plubserver.domain.report.model.Report;
import plub.plubserver.domain.todo.model.Todo;
import plub.plubserver.domain.todo.model.TodoLike;
import plub.plubserver.domain.todo.model.TodoTimeline;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Account extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "account_id")
    private Long id;

    private String email;
    private String password;
    private String nickname;
    private int age;
    private String birthday;
    private String gender;
    private String phone;
    private String introduce;
    @Enumerated(EnumType.STRING)
    private SocialType socialType;
    private String profileImage;
    private String lastLogin;
    private String fcmToken;
    @Enumerated(EnumType.STRING)
    private Role role;
    private String providerRefreshToken;

    // 회원(1) - 차단 사용자(다)
    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BanAccount> bannedAccounts = new ArrayList<>();

    // 회원(1) - 푸시 알림(다)
    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Notification> notifications = new ArrayList<>();

    // 회원(1) - 회원_모집페이지(다) # 다대다 용
    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AppliedAccount> appliedAccountList = new ArrayList<>();

    // 회원(1) - 회원_모임페이지(다) # 다대다 용
    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AccountPlubbing> accountPlubbingList = new ArrayList<>();

    // 회원(1) - 회원_카테고리(다)
    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AccountCategory> accountCategories = new ArrayList<>();

    // 회원(1) - 정책(다)
    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AccountPolicy> accountPolicyList = new ArrayList<>();

    // 회원(1) - 북마크(다)
    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Bookmark> bookmarkList = new ArrayList<>();

    // 회원(1) - 게시판(다)
    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Feed> feedList = new ArrayList<>();

    // 회원(1) - 게시판 좋아요(다)
    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FeedLike> feedLikeList = new ArrayList<>();

    // 회원(1) - 게시판 댓글(다)
    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FeedComment> feedCommentList = new ArrayList<>();

    // 회원(1) - 공지(다)
    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Notice> noticeList = new ArrayList<>();

    // 회원(1) - 공지 좋아요(다)
    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<NoticeLike>  noticeLikeList = new ArrayList<>();

    // 회원(1) - 공지 댓글(다)
    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<NoticeComment> noticeCommentList = new ArrayList<>();

    // 회원(1) - 참석여부(다)
    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CalendarAttend> attendList = new ArrayList<>();

    // 회원(1) - 아카이브(다) : 당장은 필요없지만 일단 만들어 놓음
    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Archive> archiveList = new ArrayList<>();

    // 회원(1) - 투두리스트(다)
    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Todo> todoList = new ArrayList<>();

    // 회원(1) - 타임라인(다)
    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TodoTimeline> timeLineList = new ArrayList<>();

    // 회원(1) - 신고(다)
    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Report> reportList = new ArrayList<>();

    // 회원(1) - 투두 좋아요(다)
    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TodoLike> todoLikeList = new ArrayList<>();

    // 회원(1) - 일정(다)
    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Calendar> calendars = new ArrayList<>();

    public void setIdForTest(Long id) {
        this.id = id;
    }

    public void updateProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public void updateProfile(String newNickname, String newIntroduce, String newProfileImage) {
        this.nickname = newNickname;
        this.profileImage = newProfileImage;
        this.introduce = newIntroduce;
    }

    public void updateRefreshToken(String refreshToken) {
        this.providerRefreshToken = refreshToken;
    }

    public void updateAccountCategory(List<AccountCategory> accountCategories) {
        this.accountCategories = accountCategories;
    }

    public void updateAccountPolicy(List<AccountPolicy> accountPolicyList) {
        this.accountPolicyList = accountPolicyList;
    }

    public void setAccountCategory(List<AccountCategory> accountCategories) {
        this.getAccountCategories().clear();
        this.getAccountCategories().addAll(accountCategories);
    }

    public void addBookmark(Bookmark bookmark) {
        if (bookmarkList == null) bookmarkList = new ArrayList<>();
        bookmarkList.add(bookmark);
    }

    public void removeBookmark(Bookmark bookmark) {
        bookmarkList.remove(bookmark);
    }

    public Plubbing getPlubbing(Long plubbingId) {
        return accountPlubbingList.stream()
                .filter(it -> it.getPlubbing().getId().equals(plubbingId))
                .findFirst()
                .orElseThrow(() -> new PlubbingException(StatusCode.NOT_FOUND_PLUBBING))
                .getPlubbing();
    }

    public void addArchive(Archive archive) {
        if (archiveList == null) archiveList = new ArrayList<>();
        archiveList.add(archive);
    }

    public void addNotification(Notification notification) {
        if (notifications == null) notifications = new ArrayList<>();
        notifications.add(notification);
    }

    public void addNoticeComment(NoticeComment noticeComment) {
        if (noticeCommentList == null) noticeCommentList = new ArrayList<>();
        noticeCommentList.add(noticeComment);
    }

    public void addFeedComment(FeedComment feedComment) {
        if (feedCommentList == null) feedCommentList = new ArrayList<>();
        feedCommentList.add(feedComment);
    }

    public void addNotice(Notice notice) {
        if (noticeList == null) noticeList = new ArrayList<>();
        noticeList.add(notice);
    }

    public void isAdmin() {
        if (!this.getRole().equals(Role.ROLE_ADMIN)) {
            throw new AccountException(StatusCode.ROLE_ACCESS_ERROR);
        }
    }

    public void addReport(Report report) {
        if (reportList == null) reportList = new ArrayList<>();
        reportList.add(report);
    }

    public void updateFcmToken(String newFcmToken) {
        this.fcmToken = newFcmToken;
    }
}
