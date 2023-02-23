package plub.plubserver.domain.plubbing.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import plub.plubserver.domain.plubbing.model.Plubbing;

import java.util.List;

public interface PlubbingRepository extends JpaRepository<Plubbing, Long>, PlubbingRepositoryCustom {
    /**
     * 관리자페이지
     */
    @Query("select count(b) from Plubbing b where b.createdAt like concat(:createdAt, '%')")
    Long countByCreatedAt(@Param("createdAt") String createdAt);

    List<Plubbing> findTop10ByOrderByViewsDesc();

    @Query("select count(b) from Plubbing b where b.createdAt like %:thisMonth%")
    Long countByCreatedAtMonthly(@Param("thisMonth") String thisMonth);
}

