package plub.plubserver.domain.recruit.dto;

import lombok.Builder;
import plub.plubserver.domain.recruit.model.AppliedAccount;
import plub.plubserver.domain.recruit.model.Recruit;

import java.util.List;

public class RecruitDto {
    /**
     * Request
     */
    @Builder
    public record ApplyRecruitRequest(
            List<QuestionDto.AnswerRequest> answers
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
    public record RecruitIdResponse(
            Long recruitId
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
            List<QuestionDto.QuestionAnswerResponse> answers
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
                            .map(QuestionDto.QuestionAnswerResponse::of)
                            .toList())
                    .build();
        }
    }

    public record AppliedAccountListResponse(
            List<AppliedAccountResponse> appliedAccounts
    ) {}

    public record RecruitCardResponse(

    ) {}
    public record RecruitCardListResponse(
            List<RecruitCardResponse> recruitCardList,
            long totalResultCount,
            int totalPageCount,
            boolean isLast
    ) {
        @Builder public RecruitCardListResponse {}
//        public static RecruitCardListResponse of(Page<Recruit> recruits) {
//            return RecruitCardListResponse.builder()
//                    .recruitCardList(recruits.stream()
//                            .map(RecruitCardResponse::of)
//                            .toList())
//                    .totalPageCount(recruits.getTotalPages())
//                    .totalResultCount(recruits.getTotalElements())
//                    .isLast(recruits.isLast())
//                    .build();
//        }
    }

    @Builder
    public record BookmarkResponse(
            Long recruitId,
            boolean isBookmarked
    ) {}

}
