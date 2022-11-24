package plub.plubserver.domain.plubbing.dto;

import lombok.Builder;
import org.springframework.lang.Nullable;
import org.springframework.web.multipart.MultipartFile;
import plub.plubserver.domain.plubbing.model.MeetingDay;
import plub.plubserver.domain.plubbing.model.Plubbing;
import plub.plubserver.domain.plubbing.model.PlubbingMeetingDay;
import plub.plubserver.domain.plubbing.model.PlubbingOnOff;
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
            @NotEmpty
            @Size(max = 5)
            List<String> subCategories,

            @NotBlank
            @Size(max = 25)
            String title,

            @NotBlank
            @Size(max = 12)
            String name,

            @NotBlank
            @Size(max = 12)
            String goal,

            @NotBlank
            @Size(max = 12)
            String introduce,

            @Nullable
            MultipartFile mainImageFile,

            @NotBlank
            List<String> days, // MON, TUE, WED, THR, FRI, SAT, SUN, ALL

            @NotBlank
            @Pattern(regexp = "^(ON|OFF)$", message = "only permit ON or OFF.")
            String onOff,

            // 오프라인시 - 장소 좌표 (온라인이면 0.0, 0.0)
            String address,
            Double placePositionX,
            Double placePositionY,

            @Size(min = 4, max = 20)
            int maxAccountNum,

            @Size(max = 5)
            List<String> questionTitles
    ){
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
    @Builder
    public record PlubbingResponse(
            Long plubbingId,
            List<String> subCategories,
            String name,
            String goal,
            String mainImageFileName,
            List<MeetingDay> days,
            String onOff,
            String address,
            Double placePositionX,
            Double placePositionY,
            int curAccountNum,
            int maxAccountNum,
            RecruitResponse recruit,
            String createdAt,
            String modifiedAt
    ) {
        @Builder public PlubbingResponse {}
        public static PlubbingResponse of(Plubbing plubbing) {
            return PlubbingResponse.builder()
                    .plubbingId(plubbing.getId())
                    .subCategories(plubbing.getPlubbingSubCategories().stream()
                            .map(it -> it.getSubCategory().getName())
                            .toList())
                    .name(plubbing.getName())
                    .goal(plubbing.getGoal())
                    .mainImageFileName(plubbing.getMainImageFileName())
                    .days(plubbing.getDays().stream()
                            .map(PlubbingMeetingDay::getDay)
                            .toList())
                    .onOff(plubbing.getOnOff().name())
                    .address(plubbing.getPlubbingPlace().getAddress())
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
}