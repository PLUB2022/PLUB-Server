package plub.plubserver.domain.recruit.dto;

import lombok.Builder;
import plub.plubserver.domain.recruit.model.AppliedAccount;
import plub.plubserver.domain.recruit.model.Recruit;
import plub.plubserver.domain.recruit.model.RecruitQuestion;
import plub.plubserver.domain.recruit.model.RecruitQuestionAnswer;

import java.util.List;

public class RecruitDto {
    /**
     * Request
     */
    public record AnswerRequest(Long questionId, String answer) {
    }

    @Builder
    public record ApplyRecruitRequest(
            List<AnswerRequest> answers
    ) {
    }

    public record JoinedAccountDto(
            Long accountId,
            String profileImageUrl
    ) {
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

        public static List<QuestionResponse> ofList(List<RecruitQuestion> recruitQuestions) {
            return recruitQuestions.stream()
                    .map(QuestionResponse::of)
                    .toList();
        }
    }

    public record QuestionListResponse(
            List<QuestionResponse> questions
    ) {}

    public record RecruitResponse(
            String title,
            List<String> categories,
            String plubbingName,
            String plubbingStartDate,
            String plubbingGoal,
            String plubbingMainImage,
            String introduce,
            boolean isBookmarked,
            List<JoinedAccountDto> joinedAccounts
    ) {
        @Builder
        public RecruitResponse {
        }

        public static RecruitResponse of(Recruit recruit) {
            return RecruitResponse.builder()
                    .title(recruit.getTitle())
                    // 카테고리 스트림
                    .categories(recruit.getPlubbing()
                            .getPlubbingSubCategories()
                            .stream()
                            .map(plubbingSubCategory -> plubbingSubCategory
                                    .getSubCategory()
                                    .getName())
                            .toList())
                    .plubbingName(recruit.getPlubbing().getName())
                    .plubbingStartDate(recruit.getPlubbing().getCreatedAt())
                    .plubbingGoal(recruit.getPlubbing().getGoal())
                    .introduce(recruit.getIntroduce())
                    .isBookmarked(false) // TODO: 북마크 여부
                    // 참여자 목록 스트림
                    .joinedAccounts(recruit.getPlubbing().getAccountPlubbingList()
                            .stream()
                            .map(accountPlubbing -> new JoinedAccountDto(
                                    accountPlubbing.getAccount().getId(),
                                    accountPlubbing.getAccount().getProfileImage()
                            ))
                            .toList())
                    .build();
        }
    }

    public record AppliedAccountResponse(
            String accountName,
            String profileImage,
            String createdAt,
            List<QuestionAnswerResponse> answers
    ) {
        @Builder
        public AppliedAccountResponse {
        }

        public static AppliedAccountResponse of (AppliedAccount appliedAccount) {
            return AppliedAccountResponse.builder()
                    .accountName(appliedAccount.getAccount().getNickname())
                    .profileImage(appliedAccount.getAccount().getProfileImage())
                    .createdAt(appliedAccount.getCreatedAt())
                    .answers(appliedAccount.getAnswerList()
                            .stream()
                            .map(QuestionAnswerResponse::of)
                            .toList())
                    .build();
        }
    }

    public record AppliedAccountListResponse(
            List<AppliedAccountResponse> appliedAccounts
    ) {}

    public record QuestionAnswerResponse(
            String question,
            String answer
    ) {
        @Builder
        public QuestionAnswerResponse {
        }

        public static QuestionAnswerResponse of(RecruitQuestionAnswer recruitQuestionAnswer) {
            String question = recruitQuestionAnswer.getId() + ". " + recruitQuestionAnswer
                    .getRecruitQuestion().getQuestionTitle();
            return QuestionAnswerResponse.builder()
                    .question(question)
                    .answer(recruitQuestionAnswer.getAnswer())
                    .build();
        }
    }
}
