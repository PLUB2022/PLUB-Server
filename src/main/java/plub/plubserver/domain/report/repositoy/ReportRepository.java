package plub.plubserver.domain.report.repositoy;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import plub.plubserver.domain.report.model.Report;
import plub.plubserver.domain.report.model.ReportTarget;

public interface ReportRepository extends JpaRepository<Report, Long> {

    @Query("select count(r) from Report r where r.targetId = :targetId and r.reportTarget = :target")
    Long countByReportTargetIdAndReportTarget(@Param("targetId") Long targetId, @Param("target") ReportTarget target);
}
