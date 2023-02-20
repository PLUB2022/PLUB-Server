package plub.plubserver.domain.report.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import plub.plubserver.domain.account.model.Account;
import plub.plubserver.domain.report.repositoy.ReportRepository;

import static plub.plubserver.domain.report.dto.ReportDto.CreateReportRequest;

@Service
@RequiredArgsConstructor
public class ReportService {
    private final ReportRepository reportRepository;

    // 신고하기
    public String createReport(CreateReportRequest request, Account reporter) {
        reportRepository.save(request.toEntity(reporter));
        return "신고가 접수되었습니다.";
    }
}
