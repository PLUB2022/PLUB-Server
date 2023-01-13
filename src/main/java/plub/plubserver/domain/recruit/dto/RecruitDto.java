package plub.plubserver.domain.recruit.dto;

import lombok.Builder;
import org.springframework.lang.Nullable;
import plub.plubserver.domain.plubbing.model.AccountPlubbing;
import plub.plubserver.domain.plubbing.model.Plubbing;
import plub.plubserver.domain.recruit.dto.QuestionDto.AnswerRequest;
import plub.plubserver.domain.recruit.dto.QuestionDto.QuestionAnswerResponse;
import plub.plubserver.domain.recruit.model.AppliedAccount;
import plub.plubserver.domain.recruit.model.Recruit;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

public class RecruitDto {
    /**
     * Request
     */
    public record ApplyRecruitRequest(
            List<AnswerRequest> answers
    ) {
        @Builder public ApplyRecruitRequest {}
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
        @Builder
        public UpdateRecruitRequest {
        }
    }

    public record UpdateRecruitQuestionRequest(
            List<String> questions
    ) {
        @Builder
        public UpdateRecruitQuestionRequest {
        }
    }

    /**
     * Response
     */
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
            boolean isApplied,
            int curAccountNum,
            List<JoinedAccountDto> joinedAccounts
    ) {
        @Builder
        public RecruitResponse {
        }

        public static RecruitResponse of(Recruit recruit, boolean isApplied) {
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
                    .isApplied(isApplied)
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

    public record BookmarkResponse(
            Long plubbingId,
            boolean isBookmarked
    ) {
        @Builder public BookmarkResponse {}
    }

}
