package plub.plubserver.domain.plubbing;

import plub.plubserver.domain.account.model.Account;
import plub.plubserver.domain.plubbing.dto.PlubbingDto.CreatePlubbingRequest;
import plub.plubserver.domain.plubbing.model.AccountPlubbing;
import plub.plubserver.domain.plubbing.model.AccountPlubbingStatus;
import plub.plubserver.domain.plubbing.model.Plubbing;
import plub.plubserver.domain.plubbing.model.PlubbingPlace;
import plub.plubserver.domain.recruit.model.Recruit;
import plub.plubserver.domain.recruit.model.RecruitQuestion;

import java.util.ArrayList;
import java.util.List;

public class PlubbingMockUtils {
    public static CreatePlubbingRequest createPlubbingRequest =
            CreatePlubbingRequest.builder()
                    .subCategoryIds(List.of(1L, 2L))
                    .title("테스트 모임")
                    .name("테스트 이름")
                    .goal("플럽 1차 MVP 제작 완료")
                    .introduce("테스트 모임 소개")
                    .mainImage("https://plub.s3.ap-northeast-2.amazonaws.com/plubbing/1/mainImage")
                    .days(List.of("MON", "TUE", "WED", "THR", "FRI", "SAT", "SUN"))
                    .onOff("ON")
                    .address("서울특별시 강남구 테헤란로 427")
                    .placePositionX(37.4979)
                    .placePositionY(127.02761)
                    .maxAccountNum(5)
                    .questions(List.of("질문1", "질문2"))
                    .build();
    
    public static Plubbing getMockPlubbing(Account host) {
        Plubbing plubbing = createPlubbingRequest.toEntity();
        
        // 날짜 매핑
        plubbing.addPlubbingMeetingDay(createPlubbingRequest.getPlubbingMeetingDay(plubbing));
        
        // 장소 매핑
        plubbing.addPlubbingPlace(new PlubbingPlace());

        plubbing.addPlubbingSubCategories(new ArrayList<>());
        
        // AccountPlubbing 생성
        plubbing.addAccountPlubbing(AccountPlubbing.builder()
                .isHost(true)
                .account(host)
                .plubbing(plubbing)
                .accountPlubbingStatus(AccountPlubbingStatus.ACTIVE)
                .build());

        // 모집 질문글 엔티티화 (stream을 사용하면 불변객체로 돌려줘서 이렇게 구성 함)
        List<RecruitQuestion> recruitQuestionList = new ArrayList<>();
        List<String> q = createPlubbingRequest.questions();
        for (String s : q) {
            recruitQuestionList.add(RecruitQuestion.builder()
                    .id((long) (q.indexOf(s) + 1))
                    .questionTitle(s)
                    .build());
        }

        // 모집 자동 생성
        Recruit recruit = Recruit.builder()
                .id(1L)
                .title(createPlubbingRequest.title())
                .introduce(createPlubbingRequest.introduce())
                .plubbing(plubbing)
                .recruitQuestionList(recruitQuestionList)
                .questionNum(recruitQuestionList.size())
                .visibility(true)
                .build();

        // 질문 - 모집 매핑
        recruitQuestionList.forEach(it -> it.addRecruit(recruit));

        // 모임 - 모집 매핑
        plubbing.addRecruit(recruit);
        return plubbing;
    } 
}
