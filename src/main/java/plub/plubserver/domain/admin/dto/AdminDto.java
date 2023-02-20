package plub.plubserver.domain.admin.dto;

import lombok.Builder;

import java.util.List;

public class AdminDto {

    public record AccountPlubbingStatResponse(
        String date,
        Long plubbings,
        Long accounts
    ) { }

    public record WeeklySummaryResponse(
            List<WeeklySummaryDto> week,
            Long weeklyTotalPlubbings,
            Long weeklyTotalAccounts,
            Long weeklyTotalInquires,
            Long weeklyTotalReports,
            Long monthlyTotalPlubbings,
            Long monthlyTotalAccounts,
            Long monthlyTotalInquires,
            Long monthlyTotalReports

    ) {
        @Builder public WeeklySummaryResponse{}
    }

    public record WeeklySummaryDto(
            String date,
            Long plubbings,
            Long accounts,
            Long inquires,
            Long reports

    ) {}

    public record InquiryReportResponse(
            Long id,
            String title
    ) {}

    public record LikePlubbingStatResponse(
            Long plubbingId,
            String title
    ) {}
}
