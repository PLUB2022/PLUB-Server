//package plub.plubserver.domain.recruit;
//
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import plub.plubserver.common.exception.StatusCode;
//import plub.plubserver.domain.account.AccountTemplate;
//import plub.plubserver.domain.account.model.Account;
//import plub.plubserver.domain.account.service.AccountService;
//import plub.plubserver.domain.notification.service.NotificationService;
//import plub.plubserver.domain.plubbing.PlubbingMockUtils;
//import plub.plubserver.domain.plubbing.model.AccountPlubbing;
//import plub.plubserver.domain.plubbing.model.Plubbing;
//import plub.plubserver.domain.plubbing.repository.AccountPlubbingRepository;
//import plub.plubserver.domain.plubbing.service.PlubbingService;
//import plub.plubserver.domain.recruit.dto.QuestionDto.AnswerRequest;
//import plub.plubserver.domain.recruit.dto.RecruitDto.ApplyRecruitRequest;
//import plub.plubserver.domain.recruit.dto.RecruitDto.BookmarkResponse;
//import plub.plubserver.domain.recruit.exception.RecruitException;
//import plub.plubserver.domain.recruit.model.Bookmark;
//import plub.plubserver.domain.recruit.model.Recruit;
//import plub.plubserver.domain.recruit.repository.AppliedAccountRepository;
//import plub.plubserver.domain.recruit.service.RecruitService;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Optional;
//
//import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
//import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.anyLong;
//import static org.mockito.BDDMockito.given;
//
//@ExtendWith(MockitoExtension.class)
//public class RecruitServiceTest {
//    @Mock
//    AccountService accountService;
//
//    @Mock
//    PlubbingService plubbingService;
//
//    @Mock
//    AccountPlubbingRepository accountPlubbingRepository;
//
//    @Mock
//    NotificationService notificationService;
//
//    @Mock
//    AppliedAccountRepository appliedAccountRepository;
//
//    @InjectMocks
//    RecruitService recruitService;
//
//    @Test
//    @DisplayName("모집 지원 성공")
//    void applyRecruit_success() {
//        // given
//        Account host = AccountTemplate.makeAccount1();
//        Account applicant = AccountTemplate.makeAccount2();
//        Plubbing plubbing = PlubbingMockUtils.getMockPlubbing(host);
//        given(plubbingService.getPlubbing(any())).willReturn(plubbing);
//        //doNothing().when(notificationService).pushMessage(any(), any(), any());
//        ApplyRecruitRequest applyRecruitRequest = ApplyRecruitRequest.builder()
//                .answers(List.of(
//                        new AnswerRequest(1L, "answer1"),
//                        new AnswerRequest(2L, "answer2")
//                ))
//                .build();
//
//        // when
//        recruitService.applyRecruit(applicant, 1L, applyRecruitRequest);
//
//        // then
//        assertThat(plubbing.getRecruit().getAppliedAccountList().size()).isEqualTo(1);
//        assertThat(plubbing.getRecruit().getAppliedAccountList().get(0).getAccount()).isEqualTo(applicant);
//    }
//
//    @Test
//    @DisplayName("모집 지원 실패 - 호스트가 본인거에 지원")
//    void applyRecruit_fail1() {
//        // given
//        Account host = AccountTemplate.makeAccount1();
//        Plubbing plubbing = PlubbingMockUtils.getMockPlubbing(host);
//        given(plubbingService.getPlubbing(any())).willReturn(plubbing);
//        given(accountPlubbingRepository.findByAccountAndPlubbing(any(), any()))
//                .willReturn(Optional.of(AccountPlubbing.builder().account(host).isHost(true).build()));
//        ApplyRecruitRequest request = ApplyRecruitRequest.builder().answers(new ArrayList<>()).build();
//
//        // when - then
//        assertThatThrownBy(() -> recruitService.applyRecruit(host, 1L, request))
//                .isInstanceOf(RecruitException.class)
//                .hasMessage(StatusCode.HOST_RECRUIT_ERROR.getMessage());
//    }
//
//    @Test
//    @DisplayName("모집 지원 실패 - 이미 지원한 경우")
//    void applyRecruit_fail2() {
//        // given
//        Account host = AccountTemplate.makeAccount1();
//        Account applicant = AccountTemplate.makeAccount1();
//        Plubbing plubbing = PlubbingMockUtils.getMockPlubbing(host);
//        given(plubbingService.getPlubbing(any())).willReturn(plubbing);
//        given(appliedAccountRepository.existsByAccountAndRecruit(any(), any())).willReturn(true);
//        ApplyRecruitRequest request = ApplyRecruitRequest.builder().answers(new ArrayList<>()).build();
//
//        // when - then
//        assertThatThrownBy(() -> recruitService.applyRecruit(applicant, 1L, request))
//                .isInstanceOf(RecruitException.class)
//                .hasMessage(StatusCode.ALREADY_APPLIED_RECRUIT.getMessage());
//    }
//
//    @Test
//    @DisplayName("북마크 - 등록")
//    void bookmark_enroll() {
//        // given
//        Account host = AccountTemplate.makeAccount1();
//        Plubbing plubbing = PlubbingMockUtils.getMockPlubbing(host);
//        given(plubbingService.getPlubbing(any())).willReturn(plubbing);
//        given(accountService.getAccount(anyLong())).willReturn(host);
//        host.addBookmark(Bookmark.builder()
//                .recruit(Recruit.builder().build())
//                .build());
//
//        // when
//        BookmarkResponse bookmark = recruitService.bookmark(host, 1L);
//
//        // then
//        assertThat(bookmark.isBookmarked()).isTrue();
//    }
//
//    @Test
//    @DisplayName("북마크 - 취소")
//    void bookmark_cancel() {
//        // given
//        Account host = AccountTemplate.makeAccount1();
//        Plubbing plubbing = PlubbingMockUtils.getMockPlubbing(host);
//        given(plubbingService.getPlubbing(any())).willReturn(plubbing);
//        given(accountService.getAccount(anyLong())).willReturn(host);
//        host.addBookmark(Bookmark.builder().recruit(plubbing.getRecruit()).build());
//
//        // when
//        BookmarkResponse bookmark = recruitService.bookmark(host,1L);
//
//        // then
//        assertThat(bookmark.isBookmarked()).isFalse();
//    }
//}
//
