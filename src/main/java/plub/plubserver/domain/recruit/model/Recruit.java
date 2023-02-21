package plub.plubserver.domain.recruit.model;

import lombok.*;
import plub.plubserver.common.model.BaseEntity;
import plub.plubserver.domain.plubbing.model.Plubbing;
import plub.plubserver.domain.recruit.dto.RecruitDto.UpdateRecruitRequest;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;


@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Recruit extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recruit_id")
    private Long id;

    private String title; // 소개글 제목 - 모집 페이지에서 제일 크게 보여줄 제목
    private String introduce; // 모임 소개글 - 모집 페이지에서 보여줄 내용
    private int questionNum;

    private int views;

    @Enumerated(EnumType.STRING)
    private RecruitStatus status;
    private boolean visibility;

    // 모집(1) - 회원_모집페이지(다) # 다대다 용
    @OneToMany(mappedBy = "recruit", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AppliedAccount> appliedAccountList = new ArrayList<>();

    // 모집(1) - 모임(1) # 모집이 자식 -> 외래키는 모임이 관리
    @OneToOne(mappedBy = "recruit", cascade = CascadeType.ALL)
    private Plubbing plubbing;

    // 모집(1) - 질문(다)
    @OneToMany(mappedBy = "recruit", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RecruitQuestion> recruitQuestionList = new ArrayList<>();

    // 모집(1) - 북마크(다)
    @OneToMany(mappedBy = "recruit", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Bookmark> bookmarkList = new ArrayList<>();

    /**
     * methods
     */
    public void addAppliedAccount(AppliedAccount appliedAccount) {
        if (appliedAccountList == null) appliedAccountList = new ArrayList<>();
        appliedAccountList.add(appliedAccount);
    }
    public void done() {
        status = RecruitStatus.END;
    }

    public void start() {
        status = RecruitStatus.RECRUITING;
    }

    public void updateTitleAndIntroduce(UpdateRecruitRequest updateRecruitRequest) {
        title = updateRecruitRequest.title();
        introduce = updateRecruitRequest.introduce();
    }

    public void updateQuestions(List<RecruitQuestion> recruitQuestions) {
        recruitQuestionList.clear();
        recruitQuestions.forEach(it -> it.addRecruit(this));
        recruitQuestionList.addAll(recruitQuestions);
        questionNum = recruitQuestions.size();
    }

    public void plusView() {
        views++;
    }

}
