package plub.plubserver.domain.plubbing.model;

import lombok.*;
import plub.plubserver.common.model.BaseTimeEntity;
import plub.plubserver.domain.category.model.PlubbingSubCategory;
import plub.plubserver.domain.recruit.model.Recruit;
import plub.plubserver.domain.timeline.model.PlubbingTimeline;

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
public class Plubbing extends BaseTimeEntity {

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
    private List<PlubbingDate> plubbingDateList;

    // 모임(1) - 회원_모임페이지(다) # 다대다 용
    @OneToMany(mappedBy = "plubbing", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AccountPlubbing> accountPlubbingList;

    // 모임(1) - 플러빙 공지(다)
    @OneToMany(mappedBy = "plubbing", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PlubbingNotice> notices;

    // 모임(1) - 모임 카테고리(다)
    @OneToMany(mappedBy = "plubbing", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PlubbingSubCategory> plubbingSubCategories;

    // 모임(1) - 타임라인(다)
    @OneToMany(mappedBy = "plubbing", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PlubbingTimeline> timeLineList;

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

    public void updatePlubbing(String name, String goal, String mainImageUrl) {
        this.name = name;
        this.goal = goal;
        this.mainImage = mainImageUrl;
    }

    public void endPlubbing(PlubbingStatus status) {
        this.status = status;
    }

    @PostUpdate
    public void updateCurAccountNum() {
        // TODO : 명시적 호출을 안 하고도 자동으로 업데이트 할 수 있는 방법 찾기
        curAccountNum = accountPlubbingList.size();
    }
}