package plub.plubserver.domain.plubbing;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import plub.plubserver.domain.account.AccountTemplate;
import plub.plubserver.domain.account.model.Account;
import plub.plubserver.domain.account.service.AccountService;
import plub.plubserver.domain.category.model.SubCategory;
import plub.plubserver.domain.category.service.CategoryService;
import plub.plubserver.domain.plubbing.config.PlubbingCode;
import plub.plubserver.domain.plubbing.dto.PlubbingDto.CreatePlubbingRequest;
import plub.plubserver.domain.plubbing.dto.PlubbingDto.UpdatePlubbingRequest;
import plub.plubserver.domain.plubbing.exception.PlubbingException;
import plub.plubserver.domain.plubbing.model.AccountPlubbing;
import plub.plubserver.domain.plubbing.model.Plubbing;
import plub.plubserver.domain.plubbing.model.PlubbingOnOff;
import plub.plubserver.domain.plubbing.repository.AccountPlubbingRepository;
import plub.plubserver.domain.plubbing.repository.PlubbingRepository;
import plub.plubserver.domain.plubbing.service.PlubbingService;
import plub.plubserver.domain.recruit.dto.RecruitDto;
import plub.plubserver.domain.recruit.dto.RecruitDto.UpdateRecruitQuestionRequest;
import plub.plubserver.domain.recruit.dto.RecruitDto.UpdateRecruitRequest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PlubbingServiceTest {
    @Mock
    PlubbingRepository plubbingRepository;
    @Mock
    AccountService accountService;

    @Mock
    CategoryService categoryService;

    @Mock
    AccountPlubbingRepository accountPlubbingRepository;

    @InjectMocks
    PlubbingService plubbingService;

    static Account host = AccountTemplate.makeAccount1();

    @BeforeEach
    void stubbing() {
        when(accountService.getCurrentAccount()).thenReturn(host);
    }

    /**
     * getPlubbing -> JPA 리포지토리 의존
     * (private) createRecruit,
     * convertRecruitQuestionEntityList
     * connectSubCategories,
     * -> 모임 생성 성공 테스트로 충분
     */


    @Test
    @DisplayName("모임 생성 성공")
    void createPlubbing_success() {
        // given
        CreatePlubbingRequest form = PlubbingMockUtils.createPlubbingRequest;
        Plubbing plubbing = form.toEntity();
        given(plubbingRepository.save(any())).willReturn(plubbing);
        given(categoryService.getSubCategory(any())).willReturn(SubCategory.builder().build());
        doNothing().when(plubbingRepository).flush();

        // when
        plubbingService.createPlubbing(host, form);

        // then
        assertThat(plubbing.getGoal()).isEqualTo(form.goal());
        assertThat(plubbing.getDays().get(0).getDay().toString()).isEqualTo(form.days().get(0));
        assertThat(plubbing.getOnOff()).isEqualTo(PlubbingOnOff.ON);
        assertThat(plubbing.getPlubbingSubCategories().size()).isEqualTo(2);
        assertThat(plubbing.getAccountPlubbingList().get(0).isHost()).isTrue();
        assertThat(plubbing.getRecruit().getQuestionNum()).isEqualTo(2);
    }

    @Test
    @DisplayName("모집글 수정 성공")
    void updateRecruit_success() {
        // given
        Plubbing plubbing = PlubbingMockUtils.getMockPlubbing(host);
        given(plubbingRepository.findById(any()))
                .willReturn(Optional.of(plubbing));

        given(accountPlubbingRepository.findByAccountAndPlubbing(any(), any()))
                .willReturn(Optional.of(plubbing.getAccountPlubbingList().get(0)));

        UpdateRecruitRequest form = RecruitDto.UpdateRecruitRequest.builder()
                .title("새로운 타이틀")
                .name("새로운 모임 이름")
                .mainImage("새로운 사진")
                .introduce("새로운 소개")
                .goal("새로운 목표")
                .build();
        // when
        plubbingService.updateRecruit(1L, form);

        // then
        assertThat(plubbing.getRecruit().getTitle()).isEqualTo(form.title());
        assertThat(plubbing.getName()).isEqualTo(form.name());
        assertThat(plubbing.getMainImage()).isEqualTo(form.mainImage());
        assertThat(plubbing.getRecruit().getIntroduce()).isEqualTo(form.introduce());
        assertThat(plubbing.getGoal()).isEqualTo(form.goal());
    }

    @Test
    @DisplayName("모임 정보 수정 성공 - 온라인")
    void updatePlubbing_success1() {
        // given
        Plubbing plubbing = PlubbingMockUtils.getMockPlubbing(host);
        given(plubbingRepository.findById(any()))
                .willReturn(Optional.of(plubbing));

        given(accountPlubbingRepository.findByAccountAndPlubbing(any(), any()))
                .willReturn(Optional.of(plubbing.getAccountPlubbingList().get(0)));

        UpdatePlubbingRequest onForm = UpdatePlubbingRequest.builder()
                .onOff("ON")
                .maxAccountNum(13)
                .days(List.of("TUE"))
                .build();

        // when
        plubbingService.updatePlubbing(1L, onForm);

        // then
        assertThat(plubbing.getDays().size()).isEqualTo(1);
        assertThat(plubbing.getOnOff().name()).isEqualTo(onForm.onOff());
        assertThat(plubbing.getMaxAccountNum()).isEqualTo(onForm.maxAccountNum());
    }

    @Test
    @DisplayName("모임 정보 수정 성공 - 오프라인")
    void updatePlubbing_success2() {
        // given
        Plubbing plubbing = PlubbingMockUtils.getMockPlubbing(host);
        given(plubbingRepository.findById(any()))
                .willReturn(Optional.of(plubbing));

        given(accountPlubbingRepository.findByAccountAndPlubbing(any(), any()))
                .willReturn(Optional.of(plubbing.getAccountPlubbingList().get(0)));

        UpdatePlubbingRequest offForm = UpdatePlubbingRequest.builder()
                .days(List.of("ALL"))
                .onOff("OFF")
                .address("새로운 주소")
                .build();

        // when
        plubbingService.updatePlubbing(1L, offForm);

        // then
        assertThat(plubbing.getOnOff().name()).isEqualTo(offForm.onOff());
        assertThat(plubbing.getPlubbingPlace().getAddress()).isEqualTo(offForm.address());
    }

    @Test
    @DisplayName("게스트 질문 수정 성공")
    void updateRecruitQuestions_success() {
        // given
        Plubbing plubbing = PlubbingMockUtils.getMockPlubbing(host);
        given(plubbingRepository.findById(any()))
                .willReturn(Optional.of(plubbing));

        given(accountPlubbingRepository.findByAccountAndPlubbing(any(), any()))
                .willReturn(Optional.of(plubbing.getAccountPlubbingList().get(0)));

        UpdateRecruitQuestionRequest form = UpdateRecruitQuestionRequest.builder()
                .questions(List.of("새로운 질문1"))
                .build();

        // when
        plubbingService.updateRecruitQuestion(1L, form);

        // then
        assertThat(plubbing.getRecruit().getQuestionNum()).isEqualTo(1);
        assertThat(plubbing.getRecruit().getRecruitQuestionList().get(0).getQuestionTitle())
                .isEqualTo(form.questions().get(0));
    }

    // 모집글 수정, 모임 정보 수정, 게스트 질문 수정 실패는 checkHost 예외 테스트로 충분
    @Test
    @DisplayName("checkHost - 호스트가 아니면 예외 발생")
    void checkHost() {
        // given
        Plubbing plubbing = PlubbingMockUtils.getMockPlubbing(host);
        AccountPlubbing accountPlubbing = plubbing.getAccountPlubbingList().get(0);
        accountPlubbing.changeHost();
        given(accountPlubbingRepository.findByAccountAndPlubbing(any(), any()))
                .willReturn(Optional.of(accountPlubbing));

        // when - then
        assertThatThrownBy(() -> plubbingService.checkHost(plubbing))
                .isInstanceOf(PlubbingException.class)
                .hasMessage(PlubbingCode.NOT_HOST_ERROR.getMessage());
    }
}
