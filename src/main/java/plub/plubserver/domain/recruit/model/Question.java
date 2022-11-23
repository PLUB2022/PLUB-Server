package plub.plubserver.domain.recruit.model;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import plub.plubserver.common.model.BaseTimeEntity;

import javax.persistence.*;


@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Question extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "question_id")
    private Long id;

    private String questionTitle;

    // 질문(다) - 모집(1)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private Recruit recruit;

    @Builder
    public Question(String questionTitle, Recruit recruit) {
        this.questionTitle = questionTitle;
        this.recruit = recruit;
    }

    public void addRecruit(Recruit recruit) {
        this.recruit = recruit;
    }
}