package plub.plubserver.common.dummy;

import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import plub.plubserver.domain.account.model.Account;
import plub.plubserver.domain.account.service.AccountService;
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

@Order(2)
@Component
@RequiredArgsConstructor
public class PlubbingRecruitDummy {
    private final PlubbingService plubbingService;
    private final AccountService accountService;
    private final RecruitService recruitService;
    private final RecruitRepository recruitRepository;
    private final AppliedAccountRepository appliedAccountRepository;

    @Transactional
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

        recruitRepository.flush();

        Plubbing plubbing1 = plubbingService.getPlubbing(1L);
        List<Long> appliedAccountIds = appliedAccountRepository.findAllByRecruitId(plubbing1.getRecruit().getId())
                .stream().map(it -> it.getAccount().getId()).toList();

        // 전체 승인
        appliedAccountIds.forEach(it -> recruitService.acceptApplicant(admin1, 1L, it));
    }
}
