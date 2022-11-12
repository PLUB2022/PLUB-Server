package plub.plubserver.domain.category.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import plub.plubserver.domain.category.service.CategoryService;

import javax.annotation.PostConstruct;

@Component
@RequiredArgsConstructor
@Transactional
public class InitDummyData {
    private final CategoryService categoryService;
    @PostConstruct
    public void init() {
        // 전체 카테고리
        categoryService.createCategory("예술",  1, "https://plub.s3.ap-northeast-2.amazonaws.com/category/artIcon.png");
        categoryService.createCategory("스포츠/피트니스",  2, "https://plub.s3.ap-northeast-2.amazonaws.com/category/computerIcon.png");
        categoryService.createCategory("제테크/투자",  3, "https://plub.s3.ap-northeast-2.amazonaws.com/category/cultureIcon.png");
        categoryService.createCategory("어학",  4, "https://plub.s3.ap-northeast-2.amazonaws.com/category/foodIcon.png");
        categoryService.createCategory("문화",  5, "https://plub.s3.ap-northeast-2.amazonaws.com/category/investmentIcon.png");
        categoryService.createCategory("음식",  6, "https://plub.s3.ap-northeast-2.amazonaws.com/category/languageIcon.png");
        categoryService.createCategory("취업/창업",  7, "https://plub.s3.ap-northeast-2.amazonaws.com/category/sportsIcon.png");
        categoryService.createCategory("컴퓨터",  8, "https://plub.s3.ap-northeast-2.amazonaws.com/category/workIcon.png");

        //예술 세부 카테고리
        categoryService.createCategorySub("미술", 1, Long.valueOf(1));
        categoryService.createCategorySub("켈라그래피", 2, Long.valueOf(1));
        categoryService.createCategorySub("공예", 3, Long.valueOf(1));
        categoryService.createCategorySub("DIY", 4, Long.valueOf(1));
        categoryService.createCategorySub("사진", 5, Long.valueOf(1));
        categoryService.createCategorySub("영상제작", 6, Long.valueOf(1));
        categoryService.createCategorySub("춤", 7, Long.valueOf(1));
        categoryService.createCategorySub("연기", 8, Long.valueOf(1));
        categoryService.createCategorySub("뮤지컬", 9, Long.valueOf(1));
        categoryService.createCategorySub("음악", 10, Long.valueOf(1));
        categoryService.createCategorySub("악기", 11, Long.valueOf(1));
        categoryService.createCategorySub("기타", 12, Long.valueOf(1));

        // 스포츠/피트니스 세부 카테고리
        categoryService.createCategorySub("등산", 1, Long.valueOf(2));
        categoryService.createCategorySub("산책", 2, Long.valueOf(2));
        categoryService.createCategorySub("자전거", 3, Long.valueOf(2));
        categoryService.createCategorySub("배드민턴", 4, Long.valueOf(2));
        categoryService.createCategorySub("볼링", 5, Long.valueOf(2));
        categoryService.createCategorySub("테니스", 6, Long.valueOf(2));
        categoryService.createCategorySub("계절스포츠", 7, Long.valueOf(2));
        categoryService.createCategorySub("골프", 8, Long.valueOf(2));
        categoryService.createCategorySub("클라이밍", 9, Long.valueOf(2));
        categoryService.createCategorySub("헬스", 10, Long.valueOf(2));
        categoryService.createCategorySub("크로스핏", 11, Long.valueOf(2));
        categoryService.createCategorySub("요가", 12, Long.valueOf(2));
        categoryService.createCategorySub("필라테스", 13, Long.valueOf(2));
        categoryService.createCategorySub("탁구", 14, Long.valueOf(2));
        categoryService.createCategorySub("당구", 15, Long.valueOf(2));
        categoryService.createCategorySub("러닝", 16, Long.valueOf(2));
        categoryService.createCategorySub("수영", 17, Long.valueOf(2));
        categoryService.createCategorySub("축구", 18, Long.valueOf(2));
        categoryService.createCategorySub("농구", 19, Long.valueOf(2));
        categoryService.createCategorySub("야구", 20, Long.valueOf(2));
        categoryService.createCategorySub("배구", 21, Long.valueOf(2));
        categoryService.createCategorySub("격투기", 22, Long.valueOf(2));
        categoryService.createCategorySub("스포츠관람", 23, Long.valueOf(2));

        // 제테크/투자 세부 카테고리
        categoryService.createCategorySub("주식", 1, Long.valueOf(3));
        categoryService.createCategorySub("부동산", 2, Long.valueOf(3));
        categoryService.createCategorySub("파생상품", 3, Long.valueOf(3));
        categoryService.createCategorySub("코인/NFT", 4, Long.valueOf(3));
        categoryService.createCategorySub("기타", 5, Long.valueOf(3));

        // 어학 세부 카테고리리
        categoryService.createCategorySub("영어", 1, Long.valueOf(4));
        categoryService.createCategorySub("중국어", 2, Long.valueOf(4));
        categoryService.createCategorySub("일본어", 3, Long.valueOf(4));
        categoryService.createCategorySub("스페인어", 4, Long.valueOf(4));
        categoryService.createCategorySub("프랑스어", 5, Long.valueOf(4));
        categoryService.createCategorySub("베트남어", 6, Long.valueOf(4));
        categoryService.createCategorySub("기타", 7, Long.valueOf(4));

        // 문화 세부 카테고리
        categoryService.createCategorySub("전시", 1, Long.valueOf(5));
        categoryService.createCategorySub("공연", 2, Long.valueOf(5));
        categoryService.createCategorySub("패션", 3, Long.valueOf(5));
        categoryService.createCategorySub("뷰티", 4, Long.valueOf(5));
        categoryService.createCategorySub("기타", 5, Long.valueOf(5));

        // 음식 세부 카테고리
        categoryService.createCategorySub("요리", 1, Long.valueOf(6));
        categoryService.createCategorySub("음료", 2, Long.valueOf(6));
        categoryService.createCategorySub("주류", 3, Long.valueOf(6));
        categoryService.createCategorySub("맛집", 4, Long.valueOf(6));
        categoryService.createCategorySub("기타", 5, Long.valueOf(6));

        // 취업/창업 세부 카테고리
        categoryService.createCategorySub("데이터분석", 1, Long.valueOf(7));
        categoryService.createCategorySub("마케팅", 2, Long.valueOf(7));
        categoryService.createCategorySub("파이낸스", 3, Long.valueOf(7));
        categoryService.createCategorySub("비즈니스", 4, Long.valueOf(7));
        categoryService.createCategorySub("기타", 5, Long.valueOf(7));

        // 컴퓨터 세부 카테고리
        categoryService.createCategorySub("컴퓨터", 1, Long.valueOf(8));
        categoryService.createCategorySub("디자인툴", 2, Long.valueOf(8));
        categoryService.createCategorySub("코딩", 3, Long.valueOf(8));
        categoryService.createCategorySub("프로그래밍", 4, Long.valueOf(8));






    }
    }
