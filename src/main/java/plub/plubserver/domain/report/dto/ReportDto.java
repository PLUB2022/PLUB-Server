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
}
