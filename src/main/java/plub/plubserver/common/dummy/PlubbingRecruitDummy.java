package plub.plubserver.common.dummy;

import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import plub.plubserver.domain.account.model.Account;
import plub.plubserver.domain.account.service.AccountService;
import plub.plubserver.domain.plubbing.dto.PlubbingDto.CreatePlubbingRequest;
import plub.plubserver.domain.plubbing.service.PlubbingService;
import plub.plubserver.domain.recruit.service.RecruitService;

import javax.annotation.PostConstruct;
import java.util.List;

@Order(2)
@Component
@RequiredArgsConstructor
public class PlubbingRecruitDummy {
    private final PlubbingService plubbingService;
    private final RecruitService recruitService;
    private final AccountService accountService;

    @Transactional
    @PostConstruct
    public void init() {
        Account admin1 = accountService.getAccountByEmail("admin1");
        Account admin2 = accountService.getAccountByEmail("admin2");
        for (int i = 0; i < 10; i++) {
            CreatePlubbingRequest form = CreatePlubbingRequest.builder()
                    .subCategoryIds(List.of(1L, 2L))
                    .title("운동" + i)
                    .name("테스트 이름" + i)
                    .goal("플럽 1차 MVP 제작 완료")
                    .introduce("테스트 모임 소개")
                    .days(List.of("THR", "FRI", "SAT", "SUN"))
                    .onOff("ON")
                    .address("서울특별시 강남구 테헤란로 427")
                    .time("1400")
                    .maxAccountNum(5)
                    .questions(List.of("질문1"))
                    .build();
            plubbingService.createPlubbing(admin1, form);
            plubbingService.createPlubbing(admin2, form);
        }

        for (int i = 0; i < 5; i++) {
            CreatePlubbingRequest form = CreatePlubbingRequest.builder()
                    .subCategoryIds(List.of(66L)) // 프로그래밍?
                    .title("프로그래밍 모임" + i)
                    .name("코딩 모임" + i)
                    .goal("코틀린 마스터")
                    .introduce("모각코해요" + i)
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
                    .goal("배드민턴 스매쉬 잘 치기")
                    .introduce("우리는 배민 이다")
                    .days(List.of("THR", "FRI", "SAT", "SUN"))
                    .onOff("ON")
                    .address("서울특별시 강남구 테헤란로 427")
                    .time("1700")
                    .maxAccountNum(5)
                    .questions(List.of("질문1", "질문2"))
                    .build();
            plubbingService.createPlubbing(admin1, form);
        }
    }

}
