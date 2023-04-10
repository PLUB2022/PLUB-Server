package plub.plubserver.domain.plubbing.dto;

import lombok.Builder;
import org.hibernate.validator.constraints.Range;
import org.springframework.lang.Nullable;
import plub.plubserver.common.exception.StatusCode;
import plub.plubserver.domain.account.dto.AccountDto.PlubbingAccountInfoResponse;
import plub.plubserver.domain.account.model.Account;
import plub.plubserver.domain.category.exception.CategoryException;
import plub.plubserver.domain.plubbing.model.*;
import plub.plubserver.domain.recruit.dto.RecruitDto.RecruitResponse;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.List;

public class PlubbingDto {

    /**
     * Request
     */
    public record CreatePlubbingRequest(
            @NotEmpty @Size(max = 5)
            List<Long> subCategoryIds,

            @NotBlank @Size(max = 25)
            String title,

            @NotBlank @Size(max = 12)
            String name,

            @NotBlank @Size(max = 12)
            String goal,

            @NotBlank @Size(max = 12)
            String introduce,

            @Nullable
            String mainImage,
            String time,

            List<String> days, // MON, TUE, WED, THR, FRI, SAT, SUN, ALL

            @NotBlank
            @Pattern(regexp = "^(ON|OFF)$", message = "only permit ON or OFF.")
            String onOff,

            // 오프라인시 - 장소 좌표 (온라인이면 0.0, 0.0)
            String address,
            String roadAddress,
            String placeName,
            Double placePositionX,
            Double placePositionY,

            @Range(min = 4, max = 20)
            int maxAccountNum,

            @Size(max = 5)
            List<String> questions
    ) {
        @Builder
        public CreatePlubbingRequest {
        }

        public PlubbingOnOff getOnOff() {
            if (this.onOff.equals("ON")) return PlubbingOnOff.ON;
            else return PlubbingOnOff.OFF;
        }

        public List<PlubbingMeetingDay> getPlubbingMeetingDay(Plubbing plubbing) {
            return this.days.stream().map(it -> new PlubbingMeetingDay(it, plubbing)).toList();
        }

        public Plubbing toEntity() {
            return Plubbing.builder()
                    .name(this.name)
                    .goal(this.goal)
                    .time(this.time)
                    .onOff(this.getOnOff())
                    .maxAccountNum(this.maxAccountNum)
                    .status(PlubbingStatus.ACTIVE)
                    .visibility(true)
                    .build();
        }
    }

    public record UpdatePlubbingRequest(
            @Size(max = 5)
            List<String> days,
            @NotBlank
            @Pattern(regexp = "^(ON|OFF)$", message = "only permit ON or OFF.")
            String onOff,
            @Range(min = 4, max = 20)
            int maxAccountNum,

            // 오프라인시 - 장소 좌표 (온라인이면 0.0, 0.0)
            String address,
            String roadAddress,
            String placeName,
            Double placePositionX,
            Double placePositionY,

            String time
    ) {
        @Builder
        public UpdatePlubbingRequest {
        }

        public PlubbingOnOff getOnOff() {
            if (this.onOff.equals("ON")) return PlubbingOnOff.ON;
            else return PlubbingOnOff.OFF;
        }

        public List<PlubbingMeetingDay> getPlubbingMeetingDay(Plubbing plubbing) {
            return this.days.stream().map(it -> new PlubbingMeetingDay(it, plubbing)).toList();
        }
    }

    public record PlubbingCardRequest(
            List<Long> subCategoryId,
            List<String> days,
            @Range(min = 4, max = 10)
            Integer accountNum
    ) {
        @Builder
        public PlubbingCardRequest {
        }
    }

    /**
     * Response
     */
    public record PlubbingIdResponse(
            Long plubbingId
    ) {
        public static PlubbingIdResponse of(Plubbing plubbing) {
            return new PlubbingIdResponse(plubbing.getId());
        }
        public static PlubbingIdResponse of(Long plubbingId) {
            return new PlubbingIdResponse(plubbingId);
        }
    }

    public record JoinedAccountsInfoResponse(
            int maxAccountNum,
            int curAccountNum
    ) {
        @Builder
        public JoinedAccountsInfoResponse {
        }

        public static JoinedAccountsInfoResponse of(Plubbing plubbing) {
            return JoinedAccountsInfoResponse.builder()
                    .maxAccountNum(plubbing.getMaxAccountNum())
                    .curAccountNum(plubbing.getCurAccountNum())
                    .build();
        }
    }

    public record PlubbingResponse(
            Long plubbingId,
            List<String> subCategories,
            String name,
            String goal,
            String mainImage,
            String time,
            List<MeetingDay> days,
            String onOff,
            String address,
            String roadAddress,
            String placeName,
            Double placePositionX,
            Double placePositionY,
            int curAccountNum,
            int maxAccountNum,
            RecruitResponse recruit,
            String createdAt,
            String modifiedAt
    ) {
        @Builder
        public PlubbingResponse {
        }

        public static PlubbingResponse of(Plubbing plubbing) {
            return PlubbingResponse.builder()
                    .plubbingId(plubbing.getId())
                    .subCategories(plubbing.getPlubbingSubCategories().stream()
                            .map(it -> it.getSubCategory().getName())
                            .toList())
                    .name(plubbing.getName())
                    .goal(plubbing.getGoal())
                    .mainImage(plubbing.getMainImage())
                    .time(plubbing.getTime())
                    .days(plubbing.getDays().stream()
                            .map(PlubbingMeetingDay::getDay)
                            .toList())
                    .onOff(plubbing.getOnOff().name())
                    .address(plubbing.getPlubbingPlace().getAddress())
                    .roadAddress(plubbing.getPlubbingPlace().getRoadAddress())
                    .placeName(plubbing.getPlubbingPlace().getPlaceName())
                    .placePositionX(plubbing.getPlubbingPlace().getPlacePositionX())
                    .placePositionY(plubbing.getPlubbingPlace().getPlacePositionY())
                    .curAccountNum(plubbing.getCurAccountNum())
                    .maxAccountNum(plubbing.getMaxAccountNum())
                    .createdAt(plubbing.getCreatedAt())
                    .modifiedAt(plubbing.getModifiedAt())
                    .recruit(RecruitResponse.of(plubbing.getRecruit(), true, false))
                    .build();
        }
    }
    public record MyProfilePlubbingResponse(
            Long plubbingId,
            String title,
            String goal,
            String iconImage,
            MyPlubbingStatus myPlubbingStatus
    ){
        @Builder
        public MyProfilePlubbingResponse{
        }

        public static MyProfilePlubbingResponse of(Plubbing plubbing, MyPlubbingStatus myPlubbingStatus) {
            return MyProfilePlubbingResponse.builder()
                    .plubbingId(plubbing.getId())
                    .title(plubbing.getName())
                    .goal(plubbing.getGoal())
                    .iconImage(plubbing.getPlubbingSubCategories().stream().findFirst().
                            orElseThrow(() -> new CategoryException(StatusCode.NOT_FOUND_CATEGORY))
                            .getSubCategory().getCategory().getIcon())
                    .myPlubbingStatus(myPlubbingStatus)
                    .build();
        }
    }

    public record MyProfilePlubbingListResponse(
            String plubbingStatus,
            List<MyProfilePlubbingResponse> plubbings

    ) {
        @Builder
        public MyProfilePlubbingListResponse {
        }

        public static MyProfilePlubbingListResponse of(List<MyProfilePlubbingResponse> plubbings, String status) {
            return MyProfilePlubbingListResponse.builder()
                    .plubbings(plubbings)
                    .plubbingStatus(status)
                    .build();
        }
    }

    public record MyPlubbingResponse(
            Long plubbingId,
            String name,
            String goal,
            String mainImage,
            List<MeetingDay> days,
            String time,
            boolean isHost
    ) {
        @Builder
        public MyPlubbingResponse {
        }

        public static MyPlubbingResponse of(AccountPlubbing accountPlubbing) {
            Plubbing plubbing = accountPlubbing.getPlubbing();
            return MyPlubbingResponse.builder()
                    .plubbingId(plubbing.getId())
                    .name(plubbing.getName())
                    .goal(plubbing.getGoal())
                    .mainImage(plubbing.getMainImage())
                    .days(plubbing.getDays().stream()
                            .map(PlubbingMeetingDay::getDay)
                            .toList())
                    .time(plubbing.getTime())
                    .isHost(accountPlubbing.isHost())
                    .build();
        }
    }

    public record MyPlubbingListResponse(
            List<MyPlubbingResponse> plubbings
    ) {
        @Builder
        public MyPlubbingListResponse {
        }

        public static MyPlubbingListResponse of(List<MyPlubbingResponse> plubbings) {
            return MyPlubbingListResponse.builder()
                    .plubbings(plubbings)
                    .build();
        }
    }

    public record PlubbingMemberListResponse(
           List<PlubbingAccountInfoResponse> accountInfo
    ) {
        @Builder
        public PlubbingMemberListResponse {
        }

        public static PlubbingMemberListResponse of(List<Account> accounts) {
            return PlubbingMemberListResponse.builder()
                    .accountInfo(accounts.stream()
                            .map(PlubbingAccountInfoResponse::of)
                            .toList())
                    .build();
        }
    }

    public record MainPlubbingResponse(
            Long plubbingId,
            String name,
            String goal,
            String mainImage,
            List<MeetingDay> days,
            String time,
            String onOff,
            String address,
            String roadAddress,
            String placeName,
            Double placePositionX,
            Double placePositionY,
            List<PlubbingAccountInfoResponse> accountInfo
    ) {
        @Builder
        public MainPlubbingResponse {
        }

        public static MainPlubbingResponse of(Plubbing plubbing, List<Account> accounts) {
            return MainPlubbingResponse.builder()
                    .plubbingId(plubbing.getId())
                    .name(plubbing.getName())
                    .goal(plubbing.getGoal())
                    .mainImage(plubbing.getMainImage())
                    .days(plubbing.getDays().stream()
                            .map(PlubbingMeetingDay::getDay)
                            .toList())
                    .time(plubbing.getTime())
                    .onOff(plubbing.getOnOff().name())
                    .address(plubbing.getPlubbingPlace().getAddress())
                    .roadAddress(plubbing.getPlubbingPlace().getRoadAddress())
                    .placeName(plubbing.getPlubbingPlace().getPlaceName())
                    .placePositionX(plubbing.getPlubbingPlace().getPlacePositionX())
                    .placePositionY(plubbing.getPlubbingPlace().getPlacePositionY())
                    .accountInfo(accounts.stream()
                            .map(PlubbingAccountInfoResponse::of)
                            .toList())
                    .build();
        }
    }

    public record PlubbingCardResponse(
            Long plubbingId,
            String name,
            String title,
            String mainImage,
            String introduce,
            String time,
            List<MeetingDay> days,
            String address,
            String roadAddress,
            String placeName,
            Double placePositionX,
            Double placePositionY,
            int curAccountNum,
            int remainAccountNum,
            boolean isBookmarked,
            boolean isHost
    ) {
        @Builder
        public PlubbingCardResponse {
        }

        public static PlubbingCardResponse of(Plubbing plubbing, boolean isHost, Boolean isBookmarked) {
            return PlubbingCardResponse.builder()
                    .plubbingId(plubbing.getId())
                    .name(plubbing.getName())
                    .title(plubbing.getGoal())
                    .mainImage(plubbing.getMainImage())
                    .introduce(plubbing.getGoal())
                    .time(plubbing.getTime())
                    .days(plubbing.getDays().stream()
                            .map(PlubbingMeetingDay::getDay)
                            .toList())
                    .address(plubbing.getPlubbingPlace().getAddress())
                    .roadAddress(plubbing.getPlubbingPlace().getRoadAddress())
                    .placeName(plubbing.getPlubbingPlace().getPlaceName())
                    .placePositionX(plubbing.getPlubbingPlace().getPlacePositionX())
                    .placePositionY(plubbing.getPlubbingPlace().getPlacePositionY())
                    .curAccountNum(plubbing.getCurAccountNum())
                    .remainAccountNum(plubbing.getMaxAccountNum() - plubbing.getCurAccountNum())
                    .isHost(isHost)
                    .isBookmarked(isBookmarked)
                    .build();
        }
    }

    public record PlubbingMessage(Object result) {
    }

    public record PlubbingInfoResponse(
            Long plubbingId,
            String name,
            List<String> days,
            String address,
            String roadAddress,
            String placeName,
            String goal,
            String time
    ){
        @Builder
        public PlubbingInfoResponse {
        }

        public static PlubbingInfoResponse of(Plubbing plubbing) {
            return PlubbingInfoResponse.builder()
                    .plubbingId(plubbing.getId())
                    .name(plubbing.getName())
                    .days(plubbing.getDays().stream()
                            .map(PlubbingMeetingDay::getDay)
                            .map(MeetingDay::name)
                            .toList())
                    .address(plubbing.getPlubbingPlace().getAddress())
                    .roadAddress(plubbing.getPlubbingPlace().getRoadAddress())
                    .placeName(plubbing.getPlubbingPlace().getPlaceName())
                    .goal(plubbing.getGoal())
                    .time(plubbing.getTime())
                    .build();
        }
    }
}