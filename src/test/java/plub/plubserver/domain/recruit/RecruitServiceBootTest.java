package plub.plubserver.domain.recruit;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import plub.plubserver.domain.account.dto.AuthDto;
import plub.plubserver.domain.account.model.Account;
import plub.plubserver.domain.account.service.AccountService;
import plub.plubserver.domain.account.service.AuthService;
import plub.plubserver.domain.plubbing.dto.PlubbingDto.CreatePlubbingRequest;
import plub.plubserver.domain.plubbing.service.PlubbingService;
import plub.plubserver.domain.recruit.dto.RecruitDto.AnswerRequest;
import plub.plubserver.domain.recruit.dto.RecruitDto.ApplyRecruitRequest;
import plub.plubserver.domain.recruit.service.RecruitService;

import java.util.List;

@SpringBootTest
public class RecruitServiceBootTest {
    @Autowired
    private RecruitService recruitService;

    @Autowired
    private PlubbingService plubbingService;

    @Autowired
    private AuthService authService;

    @Autowired
    private AccountService accountService;

    @Test @DisplayName("모집 지원 성공")
    void applyRecruit() {
        authService.loginAdmin(AuthDto.LoginRequest.builder()
                .email("admin1").password("plubplub2023").build());
        CreatePlubbingRequest createPlubbingRequest = CreatePlubbingRequest
                .builder()
                .subCategoryIds(List.of(1L, 2L))
                .title("테스트 플럽")
                .name("테스트 플럽 이름")
                .goal("테스트 플럽 목표")
                .introduce("테스트 플럽 소개")
                .mainImage("테스트 플럽 메인 이미지")
                .days(List.of("MON", "TUE"))
                .onOff("ON")
                .maxAccountNum(5)
                .questions(List.of("질문1", "질문2"))
                .build();
        Account currentAccount = accountService.getCurrentAccount();
        System.out.println(currentAccount.getId());
        plubbingService.createPlubbing(createPlubbingRequest);

        ApplyRecruitRequest applyRecruitRequest = ApplyRecruitRequest
                .builder()
                .answers(List.of(
                        new AnswerRequest(1L, "answer1"),
                        new AnswerRequest(2L, "answer2")
                ))
                .build();
        recruitService.applyRecruit(1L, applyRecruitRequest);
    }
}
