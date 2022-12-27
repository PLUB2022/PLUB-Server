package plub.plubserver.domain.plubbing;

import plub.plubserver.domain.plubbing.dto.PlubbingDto.CreatePlubbingRequest;

import java.util.List;

public class PlubbingMockUtils {
    public static CreatePlubbingRequest createPlubbingRequest =
            CreatePlubbingRequest.builder()
                    .subCategoryIds(List.of(1L, 2L))
                    .title("테스트 모임")
                    .name("테스트 이름")
                    .goal("플럽 1차 MVP 제작 완료")
                    .introduce("테스트 모임 소개")
                    .mainImageUrl("https://plub.s3.ap-northeast-2.amazonaws.com/plubbing/1/mainImage")
                    .days(List.of("MON", "TUE", "WED", "THR", "FRI", "SAT", "SUN"))
                    .onOff("ON")
                    .address("서울특별시 강남구 테헤란로 427")
                    .placePositionX(37.4979)
                    .placePositionY(127.02761)
                    .maxAccountNum(5)
                    .questions(List.of("질문1", "질문2", "질문3", "질문4"))
                    .build();
}
