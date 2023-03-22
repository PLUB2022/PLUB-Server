package plub.plubserver.domain.announcement.repository;


import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import plub.plubserver.domain.announcement.model.Announcement;

import static plub.plubserver.domain.announcement.model.QAnnouncement.announcement;

@RequiredArgsConstructor
public class AnnouncementRepositoryImpl implements AnnouncementRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    @Override
    public Page<Announcement> findAllByOrderByCreatedAtDesc(
            Pageable pageable,
            Long cursorId,
            String createdAt
    ) {
        JPAQuery<Announcement> query = queryFactory
                .selectFrom(announcement)
                .where(getCursorId(cursorId, createdAt))
                .orderBy(announcement.createdAt.desc())
                .distinct();

        return PageableExecutionUtils.getPage(
                query.orderBy(announcement.createdAt.desc())
                        .offset(pageable.getOffset())
                        .limit(pageable.getPageSize())
                        .fetch(),
                pageable,
                () -> queryFactory.selectFrom(announcement)
                        .fetch().size());
    }

    private BooleanExpression getCursorId(Long cursorId, String createdAt) {
        return cursorId == null || cursorId == 0 ? null : announcement.createdAt.lt(createdAt)
                .and(announcement.id.gt(cursorId))
                .or(announcement.createdAt.lt(createdAt));
    }
}
