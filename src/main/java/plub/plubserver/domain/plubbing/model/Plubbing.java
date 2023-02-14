package plub.plubserver.domain.plubbing.model;

import lombok.*;
import plub.plubserver.common.model.BaseEntity;
import plub.plubserver.domain.account.model.Account;
import plub.plubserver.domain.archive.model.Archive;
import plub.plubserver.domain.calendar.model.Calendar;
import plub.plubserver.domain.category.model.PlubbingSubCategory;
import plub.plubserver.domain.feed.model.Feed;
import plub.plubserver.domain.notice.model.Notice;
import plub.plubserver.domain.plubbing.dto.PlubbingDto.UpdatePlubbingRequest;
import plub.plubserver.domain.recruit.dto.RecruitDto.UpdateRecruitRequest;
import plub.plubserver.domain.recruit.model.Recruit;
import plub.plubserver.domain.todo.model.TodoTimeline;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

import static plub.plubserver.domain.plubbing.model.PlubbingStatus.DELETED;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Plubbing extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "plubbing_id")
    private Long id;

    private String name; // 모임 이름
    private String goal;
    private String mainImage;

    @NotNull
    private boolean visibility;

    @Enumerated(EnumType.STRING)
    private PlubbingStatus status; // ACTIVE, END

    @OneToMany(mappedBy = "plubbing", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PlubbingMeetingDay> days;

    @Enumerated(EnumType.STRING)
    private PlubbingOnOff onOff; // ON, OFF

    @Embedded
    private PlubbingPlace plubbingPlace;
    private int maxAccountNum; // 최대 인원수 4~20
    private int curAccountNum; // 현재 인원수
    private int views; // 조회수
    private String time;

    // 모임(1) - 모집(1) # 모임이 부모 : 외래키 관리
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "recruit_id")
    private Recruit recruit;

    // 모임(1) - 플러빙 일정(다)
    @OneToMany(mappedBy = "plubbing", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Calendar> calendarList;

    // 모임(1) - 회원_모임페이지(다) # 다대다 용
    @OneToMany(mappedBy = "plubbing", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AccountPlubbing> accountPlubbingList;

    // 모임(1) - 플러빙 공지(다)
    @OneToMany(mappedBy = "plubbing", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Notice> noticeList;

    // 모임(1) - 게시판(다)
    @OneToMany(mappedBy = "plubbing", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Feed> feedList;

    // 모임(1) - 모임 카테고리(다)
    @OneToMany(mappedBy = "plubbing", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PlubbingSubCategory> plubbingSubCategories;

    // 모임(1) - 투두(다)
    @OneToMany(mappedBy = "plubbing", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TodoTimeline> todoTimelineList;

    // 모임(1) - 아카이브(다)
    @OneToMany(mappedBy = "plubbing", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Archive> archives;

    /**
     * methods
     */
    // 모임 생성때 서브 카테고리들을 저장함
    public void addPlubbingSubCategories(List<PlubbingSubCategory> plubbingSubCategories) {
        if (this.plubbingSubCategories == null) {
            this.plubbingSubCategories = new ArrayList<>(plubbingSubCategories);
        } else {
            this.plubbingSubCategories.addAll(plubbingSubCategories);
        }
    }

    public void addPlubbingMeetingDay(List<PlubbingMeetingDay> days) {
        if (this.days == null) {
            this.days = new ArrayList<>(days);
        } else {
            this.days.addAll(days);
        }
    }

    public void addAccountPlubbing(AccountPlubbing accountPlubbing) {
        if (accountPlubbingList == null) accountPlubbingList = new ArrayList<>();
        accountPlubbingList.add(accountPlubbing);
    }

    public void addPlubbingPlace(PlubbingPlace plubbingPlace) {
        this.plubbingPlace = plubbingPlace;
    }

    public void addRecruit(Recruit recruit) {
        this.recruit = recruit;
    }

    public void deletePlubbing() {
        visibility = false;
        status = DELETED;
    }

    // 모집글 수정 : 타이틀, 모임 이름, 목표, 모임 소개글, 메인이미지
    public void updateRecruit(UpdateRecruitRequest updateRecruitRequest) {
        // 모집 수정
        recruit.updateTitleAndIntroduce(updateRecruitRequest);

        // 모임 수정
        name = updateRecruitRequest.name();
        goal = updateRecruitRequest.goal();
        mainImage = updateRecruitRequest.mainImage();
    }

    // 모임 정보 수정 : 날짜, 온/오프라인, 최대인원수

    public void updatePlubbing(UpdatePlubbingRequest updatePlubbingRequest) {
        days.clear();
        days.addAll(updatePlubbingRequest.getPlubbingMeetingDay(this));
        onOff = updatePlubbingRequest.getOnOff();
        maxAccountNum = updatePlubbingRequest.maxAccountNum();
        time = updatePlubbingRequest.time();
    }


    public void endPlubbing(PlubbingStatus status) {
        this.status = status;
    }

    @PostUpdate
    public void updateCurAccountNum() {
        // TODO : 명시적 호출을 안 하고도 자동으로 업데이트 할 수 있는 방법 찾기
        curAccountNum = accountPlubbingList.size();
    }

    public void plusView() {
        views++;
    }

    // archive
    public void addArchive(Archive archive) {
        if (archives == null) archives = new ArrayList<>();
        archives.add(archive);
    }

    public String getTime() {
        if (time.length() >= 2) {
            return time.substring(0, 2) + ":" + time.substring(2);
        }
        return time;
    }

    public void setMainImage(String mainImage) {
        this.mainImage = mainImage;
    }

    public Account getHost() {
        return accountPlubbingList.stream().filter(AccountPlubbing::isHost)
                .findFirst()
                .get()
                .getAccount();
    }

    public List<Account> getMembers() {
        return accountPlubbingList.stream()
                .map(AccountPlubbing::getAccount)
                .toList();
    }
}