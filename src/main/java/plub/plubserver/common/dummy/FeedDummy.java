package plub.plubserver.common.dummy;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;
import plub.plubserver.common.dto.CommentDto;
import plub.plubserver.domain.account.model.Account;
import plub.plubserver.domain.account.service.AccountService;
import plub.plubserver.domain.feed.dto.FeedDto;
import plub.plubserver.domain.feed.repository.FeedRepository;
import plub.plubserver.domain.feed.service.FeedService;

import javax.annotation.PostConstruct;

import static plub.plubserver.common.dummy.DummyImage.PLUB_MAIN_LOGO;

@Slf4j
@Component("feedDummy")
@DependsOn("plubbingDummy")
@RequiredArgsConstructor
public class FeedDummy {
    private final FeedService feedService;
    private final AccountService accountService;
    private final FeedRepository feedRepository;

    @PostConstruct
    public void init() {
        if (feedRepository.count() > 21) {
            log.info("[3] 피드가 존재하여 더미를 생성하지 않았습니다.");
            return;
        }

        Account admin1 = accountService.getAccountByEmail("admin1");
        Account admin2 = accountService.getAccountByEmail("admin2");

        // 피드 더미 (plubbingId = 1)
        for (int i = 1; i < 11; i++) {
            FeedDto.CreateFeedRequest form = FeedDto.CreateFeedRequest.builder()
                    .title("title" + i)
                    .content("줄글 내용 ~~~ 어쩌구 게시글 " + i)
                    .feedImage(PLUB_MAIN_LOGO)
                    .feedType("PHOTO_LINE")
                    .build();
            feedService.createFeed(1L, admin1, form);
//            if (i % 4 == 0)
//                feedService.pinFeed(admin1, 1L, (long) i);
        }

        for (int i = 11; i < 21; i++) {
            FeedDto.CreateFeedRequest form = FeedDto.CreateFeedRequest.builder()
                    .title("title" + i)
                    .content("줄글만 있는 게시글 ~~~ 즐거운 플러빙 ~~~ 저쩌구 게시글 " + i)
                    .feedImage("")
                    .feedType("LINE")
                    .build();
            feedService.createFeed(1L, admin1, form);
//            if (i % 4 == 0)
//                feedService.pinFeed(admin1, 1L, (long) i);
        }

        for (int i = 21; i < 31; i++) {
            FeedDto.CreateFeedRequest form = FeedDto.CreateFeedRequest.builder()
                    .title("title" + i)
                    .content("")
                    .feedImage(PLUB_MAIN_LOGO)
                    .feedType("PHOTO")
                    .build();
            feedService.createFeed(1L, admin1, form);
//            if (i % 4 == 0)
//                feedService.pinFeed(admin1, 1L, (long) i);
        }

        for (int i = 1; i < 6; i++) {
            FeedDto.CreateFeedRequest form = FeedDto.CreateFeedRequest.builder()
                    .title(i + "번째 멤버와 함께 갑니다.")
                    .content("<b>김밥먹고싶다</b> 님이 <b>요란한 한줄</b> 에 들어왔어요")
                    .feedImage("")
                    .feedType("LINE")
                    .build();
            feedService.createFeed(1L, admin1, form);
            feedService.makeSystem(30L + i);
        }

        for (int i = 0; i < 5; i++) {
            CommentDto.CreateCommentRequest form = CommentDto.CreateCommentRequest.builder()
                    .content("아자자 댓글 " + i)
                    .build();
            feedService.createFeedComment(admin1, 1L, 22L, form);
        }
        for (int i = 0; i < 5; i++) {
            CommentDto.CreateCommentRequest form = CommentDto.CreateCommentRequest.builder()
                    .content("플러빙 댓글 " + i)
                    .build();
            feedService.createFeedComment(admin2, 1L, 22L, form);
        }
        for (int i = 0; i < 5; i++) {
            CommentDto.CreateCommentRequest form = CommentDto.CreateCommentRequest.builder()
                    .content("대댓글 " + i)
                    .parentCommentId(1L)
                    .build();
            feedService.createFeedComment(admin1, 1L, 22L, form);
        }
        for (int i = 0; i < 5; i++) {
            CommentDto.CreateCommentRequest form = CommentDto.CreateCommentRequest.builder()
                    .content("대대댓글 " + i)
                    .parentCommentId(15L + i)
                    .build();
            feedService.createFeedComment(admin1, 1L, 22L, form);
        }

        log.info("[3] 피드 더미 생성 완료.");
    }
}