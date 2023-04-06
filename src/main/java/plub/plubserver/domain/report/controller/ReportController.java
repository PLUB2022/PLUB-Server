package plub.plubserver.domain.report.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import plub.plubserver.common.dto.ApiResponse;
import plub.plubserver.domain.account.model.Account;
import plub.plubserver.domain.account.service.AccountService;
import plub.plubserver.domain.report.dto.ReportDto.CreateReportRequest;
import plub.plubserver.domain.report.dto.ReportDto.ReportIdResponse;
import plub.plubserver.domain.report.dto.ReportDto.ReportTypeResponse;
import plub.plubserver.domain.report.service.ReportService;

import java.util.List;

import static plub.plubserver.common.dto.ApiResponse.success;
import static plub.plubserver.domain.report.dto.ReportDto.ReportResponse;

@Slf4j
@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@Api(tags = "신고 API", hidden = true)
public class ReportController {
    private final ReportService reportService;
    private final AccountService accountService;

    @ApiOperation(value = "신고 사유 조회")
    @GetMapping()
    public ApiResponse<List<ReportTypeResponse>> getReportType(
    ) {
        return success(reportService.getReportType());
    }

    @ApiOperation(value = "신고 생성")
    @PostMapping()
    public ApiResponse<ReportIdResponse> createReport(
            @RequestBody CreateReportRequest reportRequest
    ) {
        Account currentAccount = accountService.getCurrentAccount();
        return success(reportService.createReport(reportRequest, currentAccount));
    }

    @ApiOperation(value = "신고 화면 조회")
    @GetMapping("/{reportId}")
    public ApiResponse<ReportResponse> getReport(
            @PathVariable Long reportId
    ) {
        return success(reportService.getReport(reportId));
    }

    @ApiOperation(value = "신고 취소 처리")
    @PutMapping("/{reportId}/cancel")
    public ApiResponse<ReportIdResponse> cancelReport(
            @PathVariable Long reportId,
            @RequestParam(value = "isCancel") boolean isCancel
    ) {
        Account currentAccount = accountService.getCurrentAccount();
        return success(reportService.cancelReport(reportId, isCancel, currentAccount));
    }
}
