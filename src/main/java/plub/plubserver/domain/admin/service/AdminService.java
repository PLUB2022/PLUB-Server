package plub.plubserver.domain.admin.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import plub.plubserver.domain.account.repository.AccountRepository;
import plub.plubserver.domain.admin.dto.AdminDto.*;
import plub.plubserver.domain.plubbing.repository.PlubbingRepository;

import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static plub.plubserver.util.CustomDateUtil.getWeekDatesFromToday;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AdminService {
    private final AccountRepository accountRepository;
    private final PlubbingRepository plubbingRepository;

    /**
     * 대시보드
     */
    // 회원,모임 현황
    public List<AccountPlubbingStatResponse> getAccountPlubbingStat() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM월 dd일");
        return getWeekDatesFromToday().stream()
                .map(day -> {
                    Long accounts = accountRepository.countByCreatedAt(day);
                    Long plubbings = plubbingRepository.countByCreatedAt(day);
                    String responseDay = LocalDate.parse(day).format(formatter);
                    return new AccountPlubbingStatResponse(responseDay, plubbings, accounts);
                })
                .toList();
    }

    // 일자별 요약
    public WeeklySummaryResponse getWeeklySummary() {
        List<WeeklySummaryDto> week = getWeekDatesFromToday().stream()
                .map(day -> {
                    Long accounts = accountRepository.countByCreatedAt(day);
                    Long plubbings = plubbingRepository.countByCreatedAt(day);
                    Long inquires = 0L; // TODO
                    Long reports = 0L; // TODO
                    return new WeeklySummaryDto(day, plubbings, accounts, inquires, reports);
                })
                .toList();
        Long weeklyPlubbings = week.stream()
                .map(WeeklySummaryDto::plubbings)
                .reduce(0L, Long::sum);
        Long weeklyAccounts = week.stream()
                .map(WeeklySummaryDto::accounts)
                .reduce(0L, Long::sum);
        Long weeklyInquires = week.stream()
                .map(WeeklySummaryDto::inquires)
                .reduce(0L, Long::sum);
        Long weeklyReports = week.stream()
                .map(WeeklySummaryDto::reports)
                .reduce(0L, Long::sum);
        Month thisMonth = LocalDate.now().getMonth();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM");
        Long monthlyAccounts = accountRepository.countByCreatedAtMonthly(formatter.format(thisMonth));
        Long monthlyPlubbings = plubbingRepository.countByCreatedAtMonthly(formatter.format(thisMonth));
        Long monthlyInquires = 0L; // TODO
        Long monthlyReports = 0L; // TODO
        return WeeklySummaryResponse.builder()
                .week(week)
                .weeklyTotalPlubbings(weeklyPlubbings)
                .weeklyTotalAccounts(weeklyAccounts)
                .weeklyTotalInquires(weeklyInquires)
                .weeklyTotalReports(weeklyReports)
                .monthlyTotalPlubbings(monthlyPlubbings)
                .monthlyTotalAccounts(monthlyAccounts)
                .monthlyTotalInquires(monthlyInquires)
                .monthlyTotalReports(monthlyReports)
                .build();
    }
    // TODO : 문의, 신고 조회
    public List<InquiryReportResponse> getInquiryReport() {
        return null;
    }

    // 실시간 좋아요 플러빙 현황, 일단 views desc, limit 10
    public List<LikePlubbingStatResponse> getLikePlubbingRanking() {
        return plubbingRepository.findTop10ByOrderByViewsDesc().stream()
                .map(plubbing -> new LikePlubbingStatResponse(plubbing.getId(), plubbing.getName()))
                .toList();
    }

    public AccountPlubbingTotalCountResponse getAccountPlubbingTotalCount() {
        Long totalAccounts = accountRepository.count();
        Long totalPlubbings = plubbingRepository.count();
        return new AccountPlubbingTotalCountResponse(totalAccounts, totalPlubbings);
    }

}
