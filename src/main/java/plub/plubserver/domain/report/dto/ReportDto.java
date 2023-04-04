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

        public Report toEntity(Account reporter, Account reportedAccount) {
            return Report.builder()
                    .reportType(ReportType.toEnum(reportType))
                    .reportTarget(ReportTarget.toEnum(reportTarget))
                    .targetId(reportTargetId)
                    .content(content)
                    .reporter(reporter)
                    .reportedAccount(reportedAccount)
                    .isCanceled(false)
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

    public record ReportTypeResponse(
            String reportType,
            String detailContent
    ) {
        @Builder
        public ReportTypeResponse {
        }
    }

    public record ReportIdResponse(
            Long reportId
    ) {
        @Builder
        public ReportIdResponse {
        }

        public static ReportIdResponse of(Report report) {
            return ReportIdResponse.builder()
                    .reportId(report.getId())
                    .build();
        }
    }
}
