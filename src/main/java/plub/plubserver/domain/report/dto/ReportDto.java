package plub.plubserver.domain.report.dto;

import lombok.Builder;
import plub.plubserver.domain.account.model.Account;
import plub.plubserver.domain.report.model.Report;
import plub.plubserver.domain.report.model.ReportTarget;
import plub.plubserver.domain.report.model.ReportType;

public class ReportDto {

    public record CreateReportRequest(
            String reportType,
            String reportTarget,
            Long reportTargetId,
            String content
    ) {
        @Builder
        public CreateReportRequest {
        }

        public Report toEntity(Account reporter) {
            return Report.builder()
                    .reportType(ReportType.toEnum(reportType))
                    .reportTarget(ReportTarget.toEnum(reportTarget))
                    .targetId(reportTargetId)
                    .content(content)
                    .account(reporter)
                    .build();
        }
    }

    public record ReportResponse(
            Long reportId,
            Long targetId,
            String reportType,
            String reportTarget,
            String content,
            String message
    ) {
        @Builder
        public ReportResponse {
        }

        public static ReportResponse of(Report report, String message) {
            return ReportResponse.builder()
                    .reportId(report.getId())
                    .targetId(report.getTargetId())
                    .reportType(report.getReportType().toString())
                    .reportTarget(report.getReportTarget().toString())
                    .content(report.getContent())
                    .message(message)
                    .build();
        }
    }
}
