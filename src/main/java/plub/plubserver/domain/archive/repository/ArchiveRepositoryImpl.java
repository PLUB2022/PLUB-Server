package plub.plubserver.domain.archive.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import plub.plubserver.domain.archive.model.Archive;
import plub.plubserver.util.CursorUtils;

import static plub.plubserver.domain.archive.model.QArchive.archive;

@RequiredArgsConstructor
public class ArchiveRepositoryImpl implements ArchiveRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Archive> findAllByPlubbingId(Long plubbingId, Pageable pageable, Long cursorId) {
        JPQLQuery<Archive> query = queryFactory
                .selectFrom(archive)
                .where(archive.plubbing.id.eq(plubbingId),
                        getCursorId(cursorId))
                .distinct();

        return PageableExecutionUtils.getPage(
                query.orderBy(archive.sequence.desc())
                        .limit(CursorUtils.TEN_AMOUNT)
                        .fetch(),
                pageable,
                () -> queryFactory.selectFrom(archive)
                        .where(archive.plubbing.id.eq(plubbingId))
                        .fetch().size());
    }

    private BooleanExpression getCursorId(Long cursorId) {
        return cursorId == null || cursorId == 0 ? null : archive.sequence.lt(cursorId)
                .and(archive.id.gt(cursorId))
                .or(archive.sequence.lt(cursorId));

    }
}
