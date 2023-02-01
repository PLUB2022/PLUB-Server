package plub.plubserver.domain.recruit.model;

import lombok.*;
import plub.plubserver.common.model.BaseEntity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;


@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RecruitQuestion extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recruit_question_id")
    private Long id;

    private String questionTitle;

    // 질문(다) - 모집(1)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recruit_id")
    private Recruit recruit;

    // 질문(1) - 답변(다)
    @OneToMany(mappedBy = "recruitQuestion", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RecruitQuestionAnswer> answerList = new ArrayList<>();


    public void addRecruit(Recruit recruit) {
        this.recruit = recruit;
    }
}