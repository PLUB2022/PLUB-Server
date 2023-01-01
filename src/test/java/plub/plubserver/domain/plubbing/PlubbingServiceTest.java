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
import plub.plubserver.domain.category.service.CategoryService;
import plub.plubserver.domain.plubbing.dto.PlubbingDto.CreatePlubbingRequest;
import plub.plubserver.domain.plubbing.repository.AccountPlubbingRepository;
import plub.plubserver.domain.plubbing.repository.PlubbingRepository;
import plub.plubserver.domain.plubbing.service.PlubbingService;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PlubbingServiceTest {
    @Mock
    PlubbingRepository plubbingRepository;

    @Mock // Mock 객체 생성 (메소드를 호출 해도 아무 일을 하지 않는다.)
    CategoryService categoryService;

    @Mock
    AccountService accountService;

    @Mock
    AccountPlubbingRepository accountPlubbingRepository;

    @InjectMocks // 의존성이 필요한 것들을 위에 명시한 Mock 객체들로 넣어 준다.
    PlubbingService plubbingService;

    static Account mockAccount = AccountTemplate.makeAccount1();

    @BeforeEach // BeforeAll 은 static으로 만들어야 해서 BeforeAll을 사용
    void stubbing() {
        // stubbing == Mock 객체의 메소드의 호출 결과를 정의하는 행위
        // 여기서는 accountService의 getCurrentAccount() 가 호출되면 무조건 testAccount를 반환한다고 stubbing 한 것이다.
        // createPlubbing 에서 accountService의 getCurrentAccount는 테스트 할 필요가 없으므로 정의를 해주는 것이다.
        when(accountService.getCurrentAccount()).thenReturn(mockAccount);
    }

    @Test @DisplayName("모임 생성 성공")
    void createPlubbing() {
        // given
        CreatePlubbingRequest form = PlubbingMockUtils.createPlubbingRequest;
        when(plubbingRepository.save(form.toEntity())).thenReturn(form.toEntity());

        // when
        plubbingService.createPlubbing(form);
    }
}
