package plub.plubserver.domain.notice.repository;

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
    public Page<NoticeComment> findAllByNotice(Notice notice, Pageable pageable) {
        JPQLQuery<NoticeComment> query = queryFactory
                .selectFrom(noticeComment)
                .where(noticeComment.notice.eq(notice),
                        noticeComment.visibility.eq(true))
                .distinct();

        return PageableExecutionUtils.getPage(
                query.orderBy(noticeComment.groupId.desc(),
                                noticeComment.depth.asc())
                        .offset(pageable.getOffset())
                        .limit(pageable.getPageSize())
                        .fetch(),
                pageable,
                query::fetchCount);
    }
}