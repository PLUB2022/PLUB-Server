package plub.plubserver.domain.recruit.dto;

import lombok.Builder;
import plub.plubserver.domain.recruit.model.Question;
import plub.plubserver.domain.recruit.model.Recruit;

import java.util.List;

public class RecruitDto {

    public record RecruitResponse(
            Long recruitId,
            String title,
            String introduce,
            int questionNum,
            List<String> questions
    ){
        @Builder public RecruitResponse {}
        public static RecruitResponse of(Recruit recruit) {
            return RecruitResponse.builder()
                    .recruitId(recruit.getId())
                    .title(recruit.getTitle())
                    .introduce(recruit.getIntroduce())
                    .questionNum(recruit.getQuestionNum())
                    .questions(recruit.getQuestions().stream().map(Question::getQuestionTitle).toList())
                    .build();
        }
    }
}
