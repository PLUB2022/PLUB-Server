package plub.plubserver.domain.recruit.model;

import lombok.*;
import plub.plubserver.common.model.BaseTimeEntity;
import plub.plubserver.domain.plubbing.model.Plubbing;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;


@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Recruit extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recruit_id")
    private Long id;

    private String title; // 소개글 제목 - 모집 페이지에서 제일 크게 보여줄 제목
    private String introduce; // 모임 소개글 - 모집 페이지에서 보여줄 내용
    private int questionNum;
    private boolean visibility;

    // 모집(1) - 회원_모집페이지(다) # 다대다 용
    @OneToMany(mappedBy = "recruit", cascade = CascadeType.ALL)
    private List<AppliedAccount> appliedAccountList = new ArrayList<>();

    // 모집(1) - 모임(1) # 모집이 자식 -> 외래키는 모임이 관리
    @OneToOne(mappedBy = "recruit", cascade = CascadeType.ALL)
    private Plubbing plubbing;

    // 모집(1) - 질문(다)
    @OneToMany(mappedBy = "recruit", cascade = CascadeType.ALL)
    private List<RecruitQuestion> recruitQuestionList = new ArrayList<>();

    @PostUpdate
    private void updateQuestionNum() {
        questionNum = recruitQuestionList.size();
    }

    /**
     * methods
     */
    public void addAppliedAccount(AppliedAccount appliedAccount) {
        if (appliedAccountList == null) appliedAccountList = new ArrayList<>();
        appliedAccountList.add(appliedAccount);
    }
    public void done() {
        visibility = false;
    }

}
