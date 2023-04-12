package plub.plubserver.domain.recruit.dto;

import lombok.Builder;
import plub.plubserver.domain.recruit.model.RecruitQuestion;
import plub.plubserver.domain.recruit.model.RecruitQuestionAnswer;

import java.util.List;

public class QuestionDto {
    /**
     * Request
     */
    public record AnswerRequest(Long questionId, String answer) {
        @Builder public AnswerRequest {}
    }

    /**
     * Response
     */
    public record QuestionResponse(
            Long id,
            String question
    ) {
        @Builder
        public QuestionResponse {
        }

        public static QuestionResponse of(RecruitQuestion recruitQuestion) {
            return QuestionResponse.builder()
                    .id(recruitQuestion.getId())
                    .question(recruitQuestion.getQuestionTitle())
                    .build();
        }

        public static List<QuestionResponse> listOf(List<RecruitQuestion> recruitQuestions) {
            return recruitQuestions.stream()
                    .map(QuestionResponse::of)
                    .toList();
        }
    }

    public record QuestionListResponse(
            List<QuestionResponse> questions
    ) {}

    public record QuestionAnswerResponse(
            Long questionId,
            String question,
            String answer
    ) {
        @Builder
        public QuestionAnswerResponse {
        }

        public static QuestionAnswerResponse of(RecruitQuestionAnswer recruitQuestionAnswer) {
            String question = recruitQuestionAnswer
                    .getRecruitQuestion().getQuestionTitle();
            Long questionId = recruitQuestionAnswer.getRecruitQuestion().getId();
            return QuestionAnswerResponse.builder()
                    .questionId(questionId)
                    .question(question)
                    .answer(recruitQuestionAnswer.getAnswer())
                    .build();
        }
    }
}
