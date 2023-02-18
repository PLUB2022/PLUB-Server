package plub.plubserver.domain.admin.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import plub.plubserver.common.dto.ApiResponse;
import plub.plubserver.domain.admin.dto.AdminDto;
import plub.plubserver.domain.admin.dto.AdminDto.AccountPlubbingStatResponse;
import plub.plubserver.domain.admin.dto.AdminDto.LikePlubbingStatResponse;
import plub.plubserver.domain.admin.dto.AdminDto.WeeklySummaryResponse;
import plub.plubserver.domain.admin.service.AdminService;

import java.util.List;

import static plub.plubserver.common.dto.ApiResponse.success;

@RestController
@Api(tags = "회원 API", hidden = true)
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    @ApiOperation(value = "대시보드 - 회원/모임 현황 조회")
    @GetMapping("/accounts-plubbings")
    public ApiResponse<List<AccountPlubbingStatResponse>> getAccountPlubbingStat() {
        return success(adminService.getAccountPlubbingStat());
    }

    @ApiOperation(value = "대시보드 - 일자별 요약 조회")
    @GetMapping("/weekly-summary")
    public ApiResponse<List<WeeklySummaryResponse>> getWeeklySummary() {
        return success(adminService.getWeeklySummary());
    }

    @ApiOperation(value = "대시보드 - 문의/신고 조회")
    @GetMapping("/inquires-reports")
    public ApiResponse<List<AdminDto.InquiryReportResponse>> getInquiryReport() {
        return success(adminService.getInquiryReport());
    }

    @ApiOperation(value = "대시보드 - 실시간 좋아요 순 플러빙 조회")
    @GetMapping("/plubbing-ranking")
    public ApiResponse<List<LikePlubbingStatResponse>> getLikePlubbingRanking() {
        return success(adminService.getLikePlubbingRanking());
    }

}
