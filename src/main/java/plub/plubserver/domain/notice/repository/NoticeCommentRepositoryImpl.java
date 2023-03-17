package plub.plubserver.domain.notice.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import plub.plubserver.domain.notice.model.Notice;
import plub.plubserver.domain.notice.model.NoticeComment;

import static plub.plubserver.domain.notice.model.QNoticeComment.noticeComment;

@RequiredArgsConstructor
public class NoticeCommentRepositoryImpl implements NoticeCommentRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public Page<NoticeComment> findAllByNotice(
            Notice notice,
            Pageable pageable,
            Long lastCommentGroupId,
            Long lastCommentId
    ) {
        JPQLQuery<NoticeComment> query = queryFactory
                .selectFrom(noticeComment)
                .where(noticeComment.notice.eq(notice),
                        noticeComment.visibility.eq(true),
                        getCursorId(lastCommentGroupId, lastCommentId))
                .distinct();

        return PageableExecutionUtils.getPage(
                query.orderBy(noticeComment.commentGroupId.asc(),
                                noticeComment.createdAt.asc())
                        .offset(pageable.getOffset())
                        .limit(pageable.getPageSize())
                        .fetch(),
                pageable,
                () -> queryFactory.selectFrom(noticeComment)
                        .fetch().size());
    }

    private BooleanExpression getCursorId(Long lastCommentGroupId, Long lastCommentId) {
        if (lastCommentGroupId == null || lastCommentId == null) {
            return null;
        }
        return noticeComment.commentGroupId.goe(lastCommentGroupId)
                .and(noticeComment.id.gt(lastCommentId))
                .or(noticeComment.commentGroupId.gt(lastCommentGroupId));
    }
}