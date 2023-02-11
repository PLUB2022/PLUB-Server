package plub.plubserver.common.dummy;

import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import plub.plubserver.common.dto.CommentDto;
import plub.plubserver.domain.account.model.Account;
import plub.plubserver.domain.account.service.AccountService;
import plub.plubserver.domain.archive.dto.ArchiveDto.ArchiveRequest;
import plub.plubserver.domain.archive.service.ArchiveService;
import plub.plubserver.domain.feed.dto.FeedDto;
import plub.plubserver.domain.feed.service.FeedService;
import plub.plubserver.domain.plubbing.dto.PlubbingDto.CreatePlubbingRequest;
import plub.plubserver.domain.plubbing.model.Plubbing;
import plub.plubserver.domain.plubbing.service.PlubbingService;
import plub.plubserver.domain.recruit.dto.QuestionDto.AnswerRequest;
import plub.plubserver.domain.recruit.dto.RecruitDto.ApplyRecruitRequest;
import plub.plubserver.domain.recruit.repository.AppliedAccountRepository;
import plub.plubserver.domain.recruit.repository.RecruitRepository;
import plub.plubserver.domain.recruit.service.RecruitService;

import javax.annotation.PostConstruct;
import java.util.List;

import static plub.plubserver.common.dummy.DummyImage.PLUB_MAIN_LOGO;
import static plub.plubserver.common.dummy.DummyImage.PLUB_PROFILE_TEST;

@Order(2)
@Component
@RequiredArgsConstructor
@Transactional
public class PlubbingDummy {
    private final PlubbingService plubbingService;
    private final FeedService feedService;
    private final AccountService accountService;
    private final RecruitService recruitService;
    private final RecruitRepository recruitRepository;
    private final AppliedAccountRepository appliedAccountRepository;
    private final ArchiveService archiveService;

    @PostConstruct
    public void init() {
        Account admin1 = accountService.getAccountByEmail("admin1");
        Account admin2 = accountService.getAccountByEmail("admin2");
        for (int i = 0; i < 5; i++) {
            CreatePlubbingRequest form = CreatePlubbingRequest.builder()
                    .subCategoryIds(List.of(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L))
                    .title("운동" + i)
                    .name("운동 모임" + i)
                    .goal("3대300치기" + i)
                    .introduce("운동 소개" + i)
                    .mainImage(PLUB_MAIN_LOGO)
                    .days(List.of("THR", "FRI", "SAT", "SUN"))
                    .onOff("OFF")
                    .roadAddress("서울특별시 강남구 테헤란로 427")
                    .address("서울특별시 강남구 테헤란로 333")
                    .placeName("강남공원")
                    .placePositionX(37.4981)
                    .placePositionY(127.02761)
                    .time("1400")
                    .maxAccountNum(30)
                    .questions(List.of("질문1", "질문2", "질문3"))
                    .build();
            plubbingService.createPlubbing(admin1, form);
            plubbingService.createPlubbing(admin2, form);
        }

        for (int i = 0; i < 5; i++) {
            CreatePlubbingRequest form = CreatePlubbingRequest.builder()
                    .subCategoryIds(List.of(66L)) // 프로그래밍?
                    .title("프로그래밍 모임" + i)
                    .name("코딩 모임" + i)
                    .goal("코틀린 마스터" + i)
                    .introduce("모각코해요" + i)
                    .mainImage(PLUB_MAIN_LOGO)
                    .days(List.of("SAT", "SUN"))
                    .onOff("ON")
                    .address("서울특별시 강남구 테헤란로 427")
                    .time("2200")
                    .maxAccountNum(5)
                    .questions(List.of("질문1", "질문2", "질문3"))
                    .build();
            plubbingService.createPlubbing(admin1, form);
        }

        for (int i = 0; i < 5; i++) {
            CreatePlubbingRequest form = CreatePlubbingRequest.builder()
                    .subCategoryIds(List.of(16L))
                    .title("배드민턴 모임" + i)
                    .name("배민모임" + i)
                    .goal("배드민턴 스매쉬 잘 치기" + i)
                    .introduce("우리는 배민 이다")
                    .mainImage(PLUB_MAIN_LOGO)
                    .days(List.of("THR", "FRI", "SAT", "SUN"))
                    .onOff("OFF")
                    .roadAddress("서울특별시 강남구 테헤란로 427")
                    .address("서울특별시 강남구 테헤란로 333")
                    .placeName("강남공원")
                    .placePositionX(37.4981)
                    .placePositionY(127.02761)
                    .time("1700")
                    .maxAccountNum(5)
                    .questions(List.of("질문1", "질문2", "질문3"))
                    .build();
            plubbingService.createPlubbing(admin1, form);
        }

        // 1번 모임에 더미 모임 멤버 추가
        List<AnswerRequest> answerRequests = List.of(
                AnswerRequest.builder()
                        .questionId(1L)
                        .answer("답변1")
                        .build(),
                AnswerRequest.builder()
                        .questionId(2L)
                        .answer("답변2")
                        .build(),
                AnswerRequest.builder()
                        .questionId(3L)
                        .answer("답변3")
                        .build()
        );
        ApplyRecruitRequest applyRecruitRequest = ApplyRecruitRequest.builder()
                .answers(answerRequests)
                .build();
        for (int i = 0; i < 15; i++) {
            Account account = accountService.getAccountByEmail("dummy" + i);
            recruitService.applyRecruit(account, 1L, applyRecruitRequest);
        }
        recruitService.applyRecruit(admin2, 1L, applyRecruitRequest);

        recruitRepository.flush();

        Plubbing plubbing1 = plubbingService.getPlubbing(1L);
        List<Long> appliedAccountIds = appliedAccountRepository.findAllByRecruitId(plubbing1.getRecruit().getId())
                .stream().map(it -> it.getAccount().getId()).toList();

        // 전체 승인
        appliedAccountIds.forEach(it -> recruitService.acceptApplicant(admin1, 1L, it));

        // 피드 더미 (plubbingId = 1)
        for (int i = 1; i < 11; i++) {
            FeedDto.CreateFeedRequest form = FeedDto.CreateFeedRequest.builder()
                    .title("title" + i)
                    .content("줄글 내용 ~~~ 어쩌구 게시글 " + i)
                    .feedImage(PLUB_MAIN_LOGO)
                    .feedType("PHOTO_LINE")
                    .build();
            feedService.createFeed(1L, admin1, form);
            if (i % 4 == 0)
                feedService.pinFeed(admin1, (long) i);
        }

        for (int i = 11; i < 21; i++) {
            FeedDto.CreateFeedRequest form = FeedDto.CreateFeedRequest.builder()
                    .title("title" + i)
                    .content("줄글만 있는 게시글 ~~~ 즐거운 플러빙 ~~~ 저쩌구 게시글 " + i)
                    .feedImage("")
                    .feedType("LINE")
                    .build();
            feedService.createFeed(1L, admin1, form);
            if (i % 4 == 0)
                feedService.pinFeed(admin1, (long) i);
        }

        for (int i = 21; i < 31; i++) {
            FeedDto.CreateFeedRequest form = FeedDto.CreateFeedRequest.builder()
                    .title("title" + i)
                    .content("")
                    .feedImage(PLUB_MAIN_LOGO)
                    .feedType("PHOTO")
                    .build();
            feedService.createFeed(1L, admin1, form);
            if (i % 4 == 0)
                feedService.pinFeed(admin1, (long) i);
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
            feedService.createFeedComment(admin1, 1L, form);
        }
        for (int i = 0; i < 5; i++) {
            CommentDto.CreateCommentRequest form = CommentDto.CreateCommentRequest.builder()
                    .content("플러빙 댓글 " + i)
                    .build();
            feedService.createFeedComment(admin2, 1L, form);
        }

        for (int i = 0; i < 10; i++) {
            ArchiveRequest archiveRequest = new ArchiveRequest(
                    "테스트 아카이브" + i,
                    List.of(PLUB_MAIN_LOGO, PLUB_MAIN_LOGO, PLUB_PROFILE_TEST)
            );
            archiveService.createArchive(1L, archiveRequest);
        }
    }
}