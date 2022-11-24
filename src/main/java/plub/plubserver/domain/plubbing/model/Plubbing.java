package plub.plubserver.domain.plubbing.model;

import lombok.*;
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
    private String mainImageFileName;

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
        if (this.accountPlubbingList == null) this.accountPlubbingList = new ArrayList<>();
        this.accountPlubbingList.add(accountPlubbing);
    }

    public void addPlubbingPlace(PlubbingPlace plubbingPlace) {
        this.plubbingPlace = plubbingPlace;
    }

    public void addRecruit(Recruit recruit) {
        this.recruit = recruit;
    }
}