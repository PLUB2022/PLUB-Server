package plub.plubserver.domain.recruit.dto;

import lombok.Builder;
import org.springframework.lang.Nullable;
import plub.plubserver.domain.plubbing.model.AccountPlubbing;
import plub.plubserver.domain.plubbing.model.Plubbing;
import plub.plubserver.domain.recruit.model.AppliedAccount;
import plub.plubserver.domain.recruit.model.Recruit;
import plub.plubserver.domain.recruit.model.RecruitQuestion;
import plub.plubserver.domain.recruit.model.RecruitQuestionAnswer;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
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
            String profileImage
    ) {
        public static JoinedAccountDto of(AccountPlubbing accountPlubbing) {
            return new JoinedAccountDto(
                    accountPlubbing.getAccount().getId(),
                    accountPlubbing.getAccount().getProfileImage()
            );
        }
    }

    public record UpdateRecruitRequest(
            String title,
            @NotBlank @Size(max = 12)
            String name,
            @NotBlank @Size(max = 12)
            String goal,
            String introduce,
            @Nullable
            String mainImage
    ) {
    }

    public record UpdateRecruitQuestionRequest(
            List<String> questions
    ){}

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
    ) {
    }

    public record RecruitResponse(
            String recruitTitle,
            String recruitIntroduce,
            List<String> categories,
            String plubbingName,
            String plubbingGoal,
            String plubbingMainImage,
            List<String> plubbingDays,
            String plubbingTime,

            boolean isBookmarked,
            int curAccountNum,
            List<JoinedAccountDto> joinedAccounts
    ) {
        @Builder
        public RecruitResponse {
        }

        public static RecruitResponse of(Recruit recruit) {
            Plubbing plubbing = recruit.getPlubbing();

            List<String> categories = plubbing.getPlubbingSubCategories().stream()
                    .map(it -> it.getSubCategory().getName())
                    .toList();

            List<String> days = plubbing.getDays().stream()
                    .map(it -> it.getDay().toKorean())
                    .toList();

            List<JoinedAccountDto> joinedAccounts = plubbing.getAccountPlubbingList().stream()
                    .map(JoinedAccountDto::of)
                    .toList();

            return RecruitResponse.builder()
                    .recruitTitle(recruit.getTitle())
                    .recruitIntroduce(recruit.getIntroduce())
                    .categories(categories)
                    .plubbingName(plubbing.getName())
                    .plubbingTime(plubbing.getTime())
                    .plubbingGoal(plubbing.getGoal())
                    .plubbingDays(days)
                    .isBookmarked(false) // TODO: 북마크 여부
                    .curAccountNum(plubbing.getCurAccountNum())
                    .joinedAccounts(joinedAccounts)
                    .build();
        }
    }

    public record AppliedAccountResponse(
            Long accountId,
            String accountName,
            String profileImage,
            String createdAt,
            List<QuestionAnswerResponse> answers
    ) {
        @Builder
        public AppliedAccountResponse {
        }

        public static AppliedAccountResponse of(AppliedAccount appliedAccount) {
            return AppliedAccountResponse.builder()
                    .accountId(appliedAccount.getAccount().getId())
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
    ) {
    }

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

    public record RecruitStatusResponse(
            Long plubbingId,
            String status
    ) {
        @Builder
        public RecruitStatusResponse {
        }

        public static RecruitStatusResponse of(Recruit recruit) {
            return RecruitStatusResponse.builder()
                    .plubbingId(recruit.getPlubbing().getId())
                    .status(recruit.getStatus().name())
                    .build();
        }
    }

}
