package plub.plubserver.domain.recruit.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RecruitQuestionAnswer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recruit_question_answer_id")
    private Long id;

    // 답변(다) - 질문(1)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recruit_question_id")
    private RecruitQuestion recruitQuestion;

    // 답변(다) - 지원한 사용자(1)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "applied_account_id")
    private AppliedAccount appliedAccount;

    private String answer;
}
