package plub.plubserver.domain.admin.dto;

public class AdminDto {

    public record AccountPlubbingStatResponse(
        String date,
        Long plubbings,
        Long accounts
    ) { }

    public record WeeklySummaryResponse(
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
