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
import plub.plubserver.domain.plubbing.dto.PlubbingDto.CreatePlubbingRequest;
import plub.plubserver.domain.plubbing.model.Plubbing;
import plub.plubserver.domain.plubbing.model.PlubbingOnOff;
import plub.plubserver.domain.plubbing.repository.PlubbingRepository;
import plub.plubserver.domain.plubbing.service.PlubbingService;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
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

    @InjectMocks
    PlubbingService plubbingService;

    static Account host = AccountTemplate.makeAccount1();

    @BeforeEach
    void stubbing() {
        when(accountService.getCurrentAccount()).thenReturn(host);
    }

    @Test @DisplayName("모임 생성 성공")
    void createPlubbing() {
        // given
        CreatePlubbingRequest form = PlubbingMockUtils.createPlubbingRequest;
        Plubbing plubbing = form.toEntity();
        given(plubbingRepository.save(any())).willReturn(plubbing);
        given(categoryService.getSubCategory(any())).willReturn(SubCategory.builder().build());
        doNothing().when(plubbingRepository).flush();
        // when
        plubbingService.createPlubbing(form);

        // then
        assertThat(plubbing.getGoal()).isEqualTo(form.goal());
        assertThat(plubbing.getDays().get(0).getDay().toString()).isEqualTo(form.days().get(0));
        assertThat(plubbing.getOnOff()).isEqualTo(PlubbingOnOff.ON);
        assertThat(plubbing.getPlubbingSubCategories().size()).isEqualTo(2);
        assertThat(plubbing.getAccountPlubbingList().get(0).isHost()).isTrue();
        assertThat(plubbing.getRecruit().getQuestionNum()).isEqualTo(2);
    }
}
