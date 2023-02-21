package plub.plubserver.domain.recruit.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import plub.plubserver.common.model.SortType;
import plub.plubserver.domain.recruit.model.Recruit;
import plub.plubserver.domain.recruit.model.RecruitSearchType;

import java.util.List;

public interface RecruitRepositoryCustom {
    Page<Recruit> search(
            Pageable pageable,
            SortType sortType,
            RecruitSearchType type,
            String keyword
    );

    List<Long> findAllBookmarkedRecruitIdByAccountId(Long accountId);
}
