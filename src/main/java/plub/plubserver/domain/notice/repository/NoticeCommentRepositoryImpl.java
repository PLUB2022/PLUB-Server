package plub.plubserver.domain.notice.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import plub.plubserver.domain.notice.model.Notice;
import plub.plubserver.domain.notice.model.NoticeComment;

import java.util.List;

import static plub.plubserver.domain.notice.model.QNoticeComment.noticeComment;

@RequiredArgsConstructor
public class NoticeCommentRepositoryImpl implements NoticeCommentRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public Page<NoticeComment> findAllByNotice(Notice notice, Pageable pageable) {
        List<NoticeComment> result = queryFactory
                .selectFrom(noticeComment)
                .where(noticeComment.notice.eq(notice),
                        noticeComment.visibility.eq(true))
                .orderBy(noticeComment.groupId.desc(),
                        noticeComment.depth.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .distinct().fetch();

        return PageableExecutionUtils.getPage(
                result,
                pageable,
                result::size
        );
    }
}