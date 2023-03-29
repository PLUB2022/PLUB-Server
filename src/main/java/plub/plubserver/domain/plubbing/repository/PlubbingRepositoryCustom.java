package plub.plubserver.domain.plubbing.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import plub.plubserver.common.model.SortType;
import plub.plubserver.domain.plubbing.model.MeetingDay;
import plub.plubserver.domain.plubbing.model.Plubbing;

import java.util.List;

public interface PlubbingRepositoryCustom {
    Page<Plubbing> findAllBySubCategory(List<Long> subCategoryId, Pageable pageable, Long cursorId);

    Page<Plubbing> findAllByViews(Pageable pageable, Long cursorId, Integer views);

    Page<Plubbing> findAllByCategory(Long categoryId, Pageable pageable, SortType sortType, Long cursorId);

    Page<Plubbing> findAllByCategoryAndFilter(Long categoryId, List<Long> subCategoryId, List<MeetingDay> meetingDays, Integer accountNum, Pageable pageable, SortType sortType, Long cursorId);
}