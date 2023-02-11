package plub.plubserver.common.dummy;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import plub.plubserver.domain.category.repository.CategoryRepository;
import plub.plubserver.domain.category.service.CategoryService;

import javax.annotation.PostConstruct;

@Slf4j
@Component("categoryDummy")
@DependsOn("accountDummy")
@RequiredArgsConstructor
@Transactional
public class CategoryDummy {
    private final CategoryService categoryService;
    private final CategoryRepository categoryRepository;

    @PostConstruct
    public void init() {
        if (categoryRepository.count() > 0) {
            log.info("[1] 카테고리가 존재하여 더미를 생성하지 않았습니다.");
            return;
        }

        // 전체 카테고리
        categoryService.createCategory("예술", 1, "https://plub.s3.ap-northeast-2.amazonaws.com/category/artIcon.png");
        categoryService.createCategory("스포츠/피트니스", 2, "https://plub.s3.ap-northeast-2.amazonaws.com/category/computerIcon.png");
        categoryService.createCategory("제테크/투자", 3, "https://plub.s3.ap-northeast-2.amazonaws.com/category/cultureIcon.png");
        categoryService.createCategory("어학", 4, "https://plub.s3.ap-northeast-2.amazonaws.com/category/foodIcon.png");
        categoryService.createCategory("문화", 5, "https://plub.s3.ap-northeast-2.amazonaws.com/category/investmentIcon.png");
        categoryService.createCategory("음식", 6, "https://plub.s3.ap-northeast-2.amazonaws.com/category/languageIcon.png");
        categoryService.createCategory("취업/창업", 7, "https://plub.s3.ap-northeast-2.amazonaws.com/category/sportsIcon.png");
        categoryService.createCategory("컴퓨터", 8, "https://plub.s3.ap-northeast-2.amazonaws.com/category/workIcon.png");

        // 예술 세부 카테고리
        categoryService.createSubCategory("미술", 1, 1L, "");
        categoryService.createSubCategory("켈라그래피", 2, 1L, "");
        categoryService.createSubCategory("공예", 3, 1L, "");
        categoryService.createSubCategory("DIY", 4, 1L, "");
        categoryService.createSubCategory("사진", 5, 1L, "");
        categoryService.createSubCategory("영상제작", 6, 1L, "");
        categoryService.createSubCategory("춤", 7, 1L, "");
        categoryService.createSubCategory("연기", 8, 1L, "");
        categoryService.createSubCategory("뮤지컬", 9,1L, "");
        categoryService.createSubCategory("음악", 10, 1L, "");
        categoryService.createSubCategory("악기", 11, 1L, "");
        categoryService.createSubCategory("기타", 12, 1L, "");

        // 스포츠/피트니스 세부 카테고리
        categoryService.createSubCategory("등산", 1, 2L, "https://plub.s3.ap-northeast-2.amazonaws.com/category/subCategory/sport/mountain_climbing.jpeg");
        categoryService.createSubCategory("산책", 2, 2L, "https://plub.s3.ap-northeast-2.amazonaws.com/category/subCategory/sport/walk.jpeg");
        categoryService.createSubCategory("자전거", 3, 2L, "https://plub.s3.ap-northeast-2.amazonaws.com/category/subCategory/sport/bicycle.jpeg");
        categoryService.createSubCategory("배드민턴", 4, 2L, "https://plub.s3.ap-northeast-2.amazonaws.com/category/subCategory/sport/badminton.jpeg");
        categoryService.createSubCategory("볼링", 5, 2L, "https://plub.s3.ap-northeast-2.amazonaws.com/category/subCategory/sport/bowling.jpeg");
        categoryService.createSubCategory("테니스", 6, 2L, "https://plub.s3.ap-northeast-2.amazonaws.com/category/subCategory/sport/tenis.jpeg");
        categoryService.createSubCategory("계절스포츠", 7, 2L, "");
        categoryService.createSubCategory("골프", 8, 2L, "https://plub.s3.ap-northeast-2.amazonaws.com/category/subCategory/sport/golf.jpeg");
        categoryService.createSubCategory("클라이밍", 9, 2L, "https://plub.s3.ap-northeast-2.amazonaws.com/category/subCategory/sport/climbing.jpeg");
        categoryService.createSubCategory("헬스", 10, 2L, "https://plub.s3.ap-northeast-2.amazonaws.com/category/subCategory/sport/health.jpeg");
        categoryService.createSubCategory("크로스핏", 11, 2L, "https://plub.s3.ap-northeast-2.amazonaws.com/category/subCategory/sport/crossfit.jpeg");
        categoryService.createSubCategory("요가", 12, 2L, "https://plub.s3.ap-northeast-2.amazonaws.com/category/subCategory/sport/yoga.jpeg");
        categoryService.createSubCategory("필라테스", 13, 2L, "https://plub.s3.ap-northeast-2.amazonaws.com/category/subCategory/sport/pilates.jpeg");
        categoryService.createSubCategory("탁구", 14, 2L, "https://plub.s3.ap-northeast-2.amazonaws.com/category/subCategory/sport/pingpong.jpeg");
        categoryService.createSubCategory("당구", 15, 2L, "https://plub.s3.ap-northeast-2.amazonaws.com/category/subCategory/sport/billiard.jpeg");
        categoryService.createSubCategory("러닝", 16, 2L, "https://plub.s3.ap-northeast-2.amazonaws.com/category/subCategory/sport/running.jpeg");
        categoryService.createSubCategory("수영", 17, 2L, "https://plub.s3.ap-northeast-2.amazonaws.com/category/subCategory/sport/swimming.jpeg");
        categoryService.createSubCategory("축구", 18, 2L, "https://plub.s3.ap-northeast-2.amazonaws.com/category/subCategory/sport/soccer.jpeg");
        categoryService.createSubCategory("농구", 19, 2L, "https://plub.s3.ap-northeast-2.amazonaws.com/category/subCategory/sport/basketball.jpeg");
        categoryService.createSubCategory("야구", 20, 2L, "https://plub.s3.ap-northeast-2.amazonaws.com/category/subCategory/sport/baseball.jpeg");
        categoryService.createSubCategory("배구", 21, 2L, "https://plub.s3.ap-northeast-2.amazonaws.com/category/subCategory/sport/volleyball.jpeg");
        categoryService.createSubCategory("격투기", 22, 2L, "https://plub.s3.ap-northeast-2.amazonaws.com/category/subCategory/sport/mma.jpeg");
        categoryService.createSubCategory("스포츠관람", 23, 2L, "https://plub.s3.ap-northeast-2.amazonaws.com/category/subCategory/sport/watching_sports.jpeg");

        // 제테크/투자 세부 카테고리
        categoryService.createSubCategory("주식", 1, 3L, "");
        categoryService.createSubCategory("부동산", 2, 3L, "");
        categoryService.createSubCategory("파생상품", 3, 3L, "");
        categoryService.createSubCategory("코인/NFT", 4, 3L, "");
        categoryService.createSubCategory("기타", 5, 3L, "");

        // 어학 세부 카테고리리
        categoryService.createSubCategory("영어", 1, 4L, "");
        categoryService.createSubCategory("중국어", 2, 4L, "");
        categoryService.createSubCategory("일본어", 3, 4L, "");
        categoryService.createSubCategory("스페인어", 4, 4L, "");
        categoryService.createSubCategory("프랑스어", 5, 4L, "");
        categoryService.createSubCategory("베트남어", 6, 4L, "");
        categoryService.createSubCategory("기타", 7, 4L, "");

        // 문화 세부 카테고리
        categoryService.createSubCategory("전시", 1, 5L, "");
        categoryService.createSubCategory("공연", 2, 5L, "");
        categoryService.createSubCategory("패션", 3, 5L, "");
        categoryService.createSubCategory("뷰티", 4, 5L, "");
        categoryService.createSubCategory("기타", 5, 5L, "");

        // 음식 세부 카테고리
        categoryService.createSubCategory("요리", 1, 6L, "");
        categoryService.createSubCategory("음료", 2, 6L, "");
        categoryService.createSubCategory("주류", 3, 6L, "");
        categoryService.createSubCategory("맛집", 4, 6L, "");
        categoryService.createSubCategory("기타", 5, 6L, "");

        // 취업/창업 세부 카테고리
        categoryService.createSubCategory("데이터분석", 1, 7L, "");
        categoryService.createSubCategory("마케팅", 2, 7L, "");
        categoryService.createSubCategory("파이낸스", 3, 7L, "");
        categoryService.createSubCategory("비즈니스", 4, 7L, "");
        categoryService.createSubCategory("기타", 5, 7L, "");

        // 컴퓨터 세부 카테고리
        categoryService.createSubCategory("컴퓨터", 1, 8L, "");
        categoryService.createSubCategory("디자인툴", 2, 8L, "");
        categoryService.createSubCategory("코딩", 3, 8L, "");
        categoryService.createSubCategory("프로그래밍", 4, 8L, "");

        log.info("[1] 카테고리 더미 생성 완료.");
    }
}
