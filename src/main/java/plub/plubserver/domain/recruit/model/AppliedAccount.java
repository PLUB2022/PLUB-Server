package plub.plubserver.domain.recruit.model;

import lombok.*;
import plub.plubserver.common.model.BaseTimeEntity;
import plub.plubserver.domain.account.model.Account;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AppliedAccount extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "applied_account_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    private ApplicantStatus status;

    // 지원한 사용자(다) - 회원(1) # 다대다 용
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;

    // 지원한 사용자(다) - 모집(1) # 다대다 용
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recruit_id")
    private Recruit recruit;

    // 지원한 사용자(1) - 답변(다)
    @OneToMany(mappedBy = "appliedAccount", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RecruitQuestionAnswer> answerList = new ArrayList<>();

    /**
     * methods
     */
    public void addAnswerList(List<RecruitQuestionAnswer> answers) {
        if (answerList == null) answerList = new ArrayList<>();
        this.answerList.addAll(answers);
    }
    public void accept() {
        status = ApplicantStatus.ACCEPTED;
    }

    public void reject() {
        status = ApplicantStatus.REJECTED;
    }


}