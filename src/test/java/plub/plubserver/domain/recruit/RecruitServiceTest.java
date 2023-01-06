package plub.plubserver.domain.recruit;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import plub.plubserver.domain.account.AccountTemplate;
import plub.plubserver.domain.account.model.Account;
import plub.plubserver.domain.account.service.AccountService;
import plub.plubserver.domain.plubbing.PlubbingMockUtils;
import plub.plubserver.domain.plubbing.model.AccountPlubbing;
import plub.plubserver.domain.plubbing.model.Plubbing;
import plub.plubserver.domain.plubbing.repository.AccountPlubbingRepository;
import plub.plubserver.domain.recruit.config.RecruitCode;
import plub.plubserver.domain.recruit.dto.QuestionDto.AnswerRequest;
import plub.plubserver.domain.recruit.dto.RecruitDto.ApplyRecruitRequest;
import plub.plubserver.domain.recruit.dto.RecruitDto.BookmarkResponse;
import plub.plubserver.domain.recruit.exception.RecruitException;
import plub.plubserver.domain.recruit.model.Bookmark;
import plub.plubserver.domain.recruit.model.Recruit;
import plub.plubserver.domain.recruit.repository.AppliedAccountRepository;
import plub.plubserver.domain.recruit.repository.RecruitRepository;
import plub.plubserver.domain.recruit.service.RecruitService;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class RecruitServiceTest {
    @Mock
    RecruitRepository recruitRepository;

    @Mock
    AccountService accountService;

    @Mock
    AccountPlubbingRepository accountPlubbingRepository;

    @Mock
    AppliedAccountRepository appliedAccountRepository;

    @InjectMocks
    RecruitService recruitService;

    @Test
    @DisplayName("모집 지원 성공")
    void applyRecruit_success() {
        // given
        Account host = AccountTemplate.makeAccount1();
        Account applicant = AccountTemplate.makeAccount2();
        Plubbing plubbing = PlubbingMockUtils.getMockPlubbing(host);

        given(accountService.getCurrentAccount()).willReturn(applicant);

        given(recruitRepository.findById(any()))
                .willReturn(Optional.of(plubbing.getRecruit()));

        given(appliedAccountRepository.existsByAccountAndRecruit(any(), any()))
                .willReturn(false);

        ApplyRecruitRequest applyRecruitRequest = ApplyRecruitRequest.builder()
                .answers(List.of(
                        new AnswerRequest(1L, "answer1"),
                        new AnswerRequest(2L, "answer2")
                ))
                .build();

        // when
        recruitService.applyRecruit(1L, applyRecruitRequest);

        // then
        assertThat(plubbing.getRecruit().getAppliedAccountList().size()).isEqualTo(1);
        assertThat(plubbing.getRecruit().getAppliedAccountList().get(0).getAccount()).isEqualTo(applicant);
    }

    @Test
    @DisplayName("모집 생성 실패 - 호스트가 본인거에 지원")
    void applyRecruit_fail1() {
        // given
        Account host = AccountTemplate.makeAccount1();
        Plubbing plubbing = PlubbingMockUtils.getMockPlubbing(host);
        given(accountService.getCurrentAccount()).willReturn(host);

        given(recruitRepository.findById(any()))
                .willReturn(Optional.of(plubbing.getRecruit()));

        given(accountPlubbingRepository.findByAccount(any()))
                .willReturn(Optional.of(AccountPlubbing.builder()
                        .isHost(true)
                        .build()));

        // when - then
        assertThatThrownBy(() -> recruitService.applyRecruit(1L, ApplyRecruitRequest.builder().build()))
                .isInstanceOf(RecruitException.class)
                .hasMessage(RecruitCode.HOST_RECRUIT_ERROR.getMessage());
    }

    @Test
    @DisplayName("모집 생성 실패 - 이미 지원한 경우")
    void applyRecruit_fail2() {
        // given
        Account host = AccountTemplate.makeAccount1();
        Account applicant = AccountTemplate.makeAccount1();
        Plubbing plubbing = PlubbingMockUtils.getMockPlubbing(host);

        given(accountService.getCurrentAccount()).willReturn(applicant);

        given(recruitRepository.findById(any()))
                .willReturn(Optional.of(plubbing.getRecruit()));

        given(appliedAccountRepository.existsByAccountAndRecruit(any(), any()))
                .willReturn(true);

        // when - then
        assertThatThrownBy(() -> recruitService.applyRecruit(1L, ApplyRecruitRequest.builder().build()))
                .isInstanceOf(RecruitException.class)
                .hasMessage(RecruitCode.ALREADY_APPLIED_RECRUIT.getMessage());
    }

    @Test
    @DisplayName("북마크 - 등록")
    void bookmark_enroll() {
        // given
        Account host = AccountTemplate.makeAccount1();
        Plubbing plubbing = PlubbingMockUtils.getMockPlubbing(host);

        given(accountService.getCurrentAccount()).willReturn(host);

        given(recruitRepository.findById(any()))
                .willReturn(Optional.of(plubbing.getRecruit()));

        host.addBookmark(Bookmark.builder()
                .recruit(Recruit.builder().build())
                .build());

        // when
        BookmarkResponse bookmark = recruitService.bookmark(1L);

        // then
        assertThat(bookmark.isBookmarked()).isTrue();
    }

    @Test
    @DisplayName("북마크 - 취소")
    void bookmark_cancel() {
        // given
        Account host = AccountTemplate.makeAccount1();
        Plubbing plubbing = PlubbingMockUtils.getMockPlubbing(host);

        given(accountService.getCurrentAccount()).willReturn(host);

        given(recruitRepository.findById(any()))
                .willReturn(Optional.of(plubbing.getRecruit()));

        host.addBookmark(Bookmark.builder().recruit(plubbing.getRecruit()).build());

        // when
        BookmarkResponse bookmark = recruitService.bookmark(1L);

        // then
        assertThat(bookmark.isBookmarked()).isFalse();
    }
}
