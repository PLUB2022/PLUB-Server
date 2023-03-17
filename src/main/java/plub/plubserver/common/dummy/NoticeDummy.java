package plub.plubserver.common.dummy;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;
import plub.plubserver.common.dto.CommentDto;
import plub.plubserver.domain.account.model.Account;
import plub.plubserver.domain.account.service.AccountService;
import plub.plubserver.domain.notice.dto.NoticeDto;
import plub.plubserver.domain.notice.repository.NoticeRepository;
import plub.plubserver.domain.notice.service.NoticeService;

import javax.annotation.PostConstruct;

@Slf4j
@Component("noticeDummy")
@DependsOn("calendarDummy")
@RequiredArgsConstructor
public class NoticeDummy {
    private final NoticeService noticeService;
    private final AccountService accountService;
    private final NoticeRepository noticeRepository;

    @PostConstruct
    public void init() {
        if (noticeRepository.count() > 0) {
            log.info("[7] 공지가 존재하여 더미를 생성하지 않았습니다.");
            return;
        }

        Account admin1 = accountService.getAccountByEmail("admin1");
        System.out.println(admin1.getEmail());
        Account admin2 = accountService.getAccountByEmail("admin2");

        for (int i = 1; i < 40; i++) {
            NoticeDto.CreateNoticeRequest form = NoticeDto.CreateNoticeRequest.builder()
                    .title("notification title " + i)
                    .content("notification content " + i)
                    .build();
            noticeService.createNotice(1L, admin1, form);
        }

        for (int i = 0; i < 5; i++) {
            CommentDto.CreateCommentRequest form = CommentDto.CreateCommentRequest.builder()
                    .content("좋은 공지네요 " + i)
                    .build();
            noticeService.createNoticeComment(admin1, 1L, 1L, form);
        }
        for (int i = 0; i < 5; i++) {
            CommentDto.CreateCommentRequest form = CommentDto.CreateCommentRequest.builder()
                    .content("전달 감사합니다 " + i)
                    .build();
            noticeService.createNoticeComment(admin2, 1L, 1L, form);
        }
        for (int i = 0; i < 5; i++) {
            CommentDto.CreateCommentRequest form = CommentDto.CreateCommentRequest.builder()
                    .content("대댓글 " + i)
                    .parentCommentId(1L)
                    .build();
            noticeService.createNoticeComment(admin1, 1L, 1L, form);
        }
        for (int i = 0; i < 5; i++) {
            CommentDto.CreateCommentRequest form = CommentDto.CreateCommentRequest.builder()
                    .content("대대댓글 " + i)
                    .parentCommentId(15L + i)
                    .build();
            noticeService.createNoticeComment(admin2, 1L, 1L, form);
        }

        log.info("[7] 공지 더미 생성 완료.");
    }
}
