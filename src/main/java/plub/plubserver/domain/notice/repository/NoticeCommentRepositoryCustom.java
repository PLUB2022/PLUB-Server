package plub.plubserver.domain.notice.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import plub.plubserver.domain.notice.model.Notice;
import plub.plubserver.domain.notice.model.NoticeComment;


public interface NoticeCommentRepositoryCustom {
    Page<NoticeComment> findAllByNotice(Notice notice, Pageable pageable, Long lastCommentGroupId, Long lastCommentId);
}