package plub.plubserver.domain.archive.repository

import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.jpa.JPQLQuery
import com.querydsl.jpa.impl.JPAQueryFactory
import java.util.Optional
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.support.PageableExecutionUtils
import plub.plubserver.domain.archive.model.Archive
import plub.plubserver.domain.archive.model.QArchive.archive
import plub.plubserver.util.CursorUtils


interface ArchiveRepository : JpaRepository<Archive, Long>, ArchiveRepositoryCustom {
    fun findFirstByPlubbingIdOrderBySequenceDesc(plubbingId: Long?): Optional<Archive>
    fun countAllByPlubbingId(plubbingId: Long?): Long
}

interface ArchiveRepositoryCustom {
    fun findAllByPlubbingId(plubbingId: Long, pageable: Pageable, cursorId: Long): Page<Archive>
}

class ArchiveRepositoryImpl(
    private val queryFactory: JPAQueryFactory
) : ArchiveRepositoryCustom {
    override fun findAllByPlubbingId(plubbingId: Long, pageable: Pageable, cursorId: Long): Page<Archive> {
        val query: JPQLQuery<Archive> = queryFactory
            .selectFrom(archive)
            .where(
                archive.plubbing.id.eq(plubbingId),
                getCursorId(cursorId)
            )
            .distinct()
        return PageableExecutionUtils.getPage(
            query.orderBy(archive.sequence.desc())
                .limit(CursorUtils.TEN_AMOUNT.toLong())
                .fetch(),
            pageable
        ) {
            queryFactory.selectFrom(archive)
                .where(archive.plubbing.id.eq(plubbingId))
                .fetch().size.toLong()
        }
    }

    private fun getCursorId(cursorId: Long?): BooleanExpression? {
        return if (cursorId == null || cursorId == 0L) null
        else archive.sequence.lt(cursorId)
            .and(archive.id.gt(cursorId))
            .or(archive.sequence.lt(cursorId))
    }
}
