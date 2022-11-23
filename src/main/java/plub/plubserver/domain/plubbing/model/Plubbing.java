package plub.plubserver.domain.plubbing.model;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import plub.plubserver.common.model.BaseTimeEntity;
import plub.plubserver.domain.account.model.AccountPlubbing;
import plub.plubserver.domain.category.model.PlubbingSubCategory;
import plub.plubserver.domain.recruit.model.Recruit;
import plub.plubserver.domain.timeline.model.PlubbingTimeline;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Plubbing extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "plubbing_id")
    private Long id;

    private String name; // 모임 이름
    private String goal;
    private String mainImageFileName;

    @Enumerated(EnumType.STRING)
    private PlubbingStatus status; // ACTIVE, END

    private String days; // 월 화 수 목 금 토 일 무

    @Enumerated(EnumType.STRING)
    private PlubbingOnOff onOff; // ON, OFF

    @Embedded
    private PlubbingPlace plubbingPlace;
    private int maxAccountNum; // 최대 인원수 4~20
    private int curAccountNum; // 현재 인원수

    // 모임(1) - 모집(1) # 모임이 부모 : 외래키 관리
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "recruit_id")
    private Recruit recruit;

    // 모임(1) - 플러빙 일정(다)
    @OneToMany(mappedBy = "plubbing", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PlubbingDate> plubbingDateList = new ArrayList<>();

    // 모임(1) - 회원_모임페이지(다) # 다대다 용
    @OneToMany(mappedBy = "plubbing", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AccountPlubbing> accountPlubbingList = new ArrayList<>();

    // 모임(1) - 플러빙 공지(다)
    @OneToMany(mappedBy = "plubbing", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PlubbingNotice> notices = new ArrayList<>();

    // 모임(1) - 모임 카테고리(다)
    @OneToMany(mappedBy = "plubbing", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PlubbingSubCategory> plubbingSubCategories = new ArrayList<>();

    // 모임(1) - 타임라인(다)
    @OneToMany(mappedBy = "plubbing", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PlubbingTimeline> timeLineList = new ArrayList<>();

    @Builder
    public Plubbing(String name, String goal, String mainImageFileName, PlubbingStatus status, String days, PlubbingOnOff onOff, PlubbingPlace plubbingPlace, int maxAccountNum, int curAccountNum, Recruit recruit, List<PlubbingDate> plubbingDateList, List<AccountPlubbing> accountPlubbingList, List<PlubbingNotice> notices, List<PlubbingSubCategory> plubbingSubCategories, List<PlubbingTimeline> timeLineList) {
        this.name = name;
        this.goal = goal;
        this.mainImageFileName = mainImageFileName;
        this.status = status;
        this.days = days;
        this.onOff = onOff;
        this.plubbingPlace = plubbingPlace;
        this.maxAccountNum = maxAccountNum;
        this.curAccountNum = curAccountNum;
        this.recruit = recruit;
        this.plubbingDateList = plubbingDateList;
        this.accountPlubbingList = accountPlubbingList;
        this.notices = notices;
        this.plubbingSubCategories = plubbingSubCategories;
        this.timeLineList = timeLineList;
    }


    /**
     * methods
     */
    // 모임 생성때 서브 카테고리들을 저장함
    public void addPlubbingSubCategories(List<PlubbingSubCategory> plubbingSubCategories) {
        this.plubbingSubCategories.addAll(plubbingSubCategories);
    }

    public void addAccountPlubbing(AccountPlubbing accountPlubbing) {
        this.accountPlubbingList.add(accountPlubbing);
    }

    public void addPlubbingPlace(PlubbingPlace plubbingPlace) {
        this.plubbingPlace = plubbingPlace;
    }

    public void addRecruit(Recruit recruit) {
        this.recruit = recruit;
    }
}