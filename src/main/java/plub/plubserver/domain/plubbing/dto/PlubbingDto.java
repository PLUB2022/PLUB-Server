package plub.plubserver.domain.plubbing.dto;

import lombok.Builder;
import org.hibernate.validator.constraints.Range;
import org.springframework.lang.Nullable;
import plub.plubserver.domain.account.dto.AccountDto.PlubbingAccountInfoResponse;
import plub.plubserver.domain.account.model.Account;
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
    @Builder
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
                    .mainImage(this.mainImage)
                    .time(this.time)
                    .onOff(this.getOnOff())
                    .maxAccountNum(this.maxAccountNum)
                    .status(PlubbingStatus.ACTIVE)
                    .visibility(true)
                    .build();
        }
    }

    @Builder
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
            Double placePositionY
    ) {
        public PlubbingOnOff getOnOff() {
            if (this.onOff.equals("ON")) return PlubbingOnOff.ON;
            else return PlubbingOnOff.OFF;
        }

        public List<PlubbingMeetingDay> getPlubbingMeetingDay(Plubbing plubbing) {
            return this.days.stream().map(it -> new PlubbingMeetingDay(it, plubbing)).toList();
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
                    .recruit(RecruitResponse.of(plubbing.getRecruit()))
                    .build();
        }
    }

    public record MyPlubbingResponse(
            Long plubbingId,
            String name,
            String goal,
            String mainImage,
            List<MeetingDay> days
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

    public record MainPlubbingResponse(
            Long plubbingId,
            String name,
            String goal,
            String mainImage,
            List<MeetingDay> days,
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
            List<MeetingDay> days,
            int curAccountNum,
            boolean isBookmarked
    ) {
        @Builder
        public PlubbingCardResponse {
        }

        public static PlubbingCardResponse of(Plubbing plubbing) {
            return PlubbingCardResponse.builder()
                    .plubbingId(plubbing.getId())
                    .name(plubbing.getName())
                    .title(plubbing.getGoal())
                    .mainImage(plubbing.getMainImage())
                    .introduce(plubbing.getGoal())
                    .days(plubbing.getDays().stream()
                            .map(PlubbingMeetingDay::getDay)
                            .toList())
                    .curAccountNum(plubbing.getCurAccountNum())
                    .build();
        }
    }

    public record PlubbingMessage(Object result) {
    }
}