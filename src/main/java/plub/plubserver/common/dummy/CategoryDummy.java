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
        categoryService.createCategory("예술", 1, "https://plub.s3.ap-northeast-2.amazonaws.com/category/art.png");
        categoryService.createCategory("스포츠", 2, "https://plub.s3.ap-northeast-2.amazonaws.com/category/sports.png");
        categoryService.createCategory("제테크/투자", 3, "https://plub.s3.ap-northeast-2.amazonaws.com/category/investment.png");
        categoryService.createCategory("어학", 4, "https://plub.s3.ap-northeast-2.amazonaws.com/category/language.png");
        categoryService.createCategory("문화", 5, "https://plub.s3.ap-northeast-2.amazonaws.com/category/culture.png");
        categoryService.createCategory("음식", 6, "https://plub.s3.ap-northeast-2.amazonaws.com/category/food.png");
        categoryService.createCategory("취업/창업", 7, "https://plub.s3.ap-northeast-2.amazonaws.com/category/work.png");
        categoryService.createCategory("컴퓨터", 8, "https://plub.s3.ap-northeast-2.amazonaws.com/category/computer.png");

        // 예술 세부 카테고리
        categoryService.createSubCategory("미술", 1, 1L, "https://plub.s3.ap-northeast-2.amazonaws.com/category/subCategory/art/art.jpeg");
        categoryService.createSubCategory("켈라그래피", 2, 1L, "https://plub.s3.ap-northeast-2.amazonaws.com/category/subCategory/art/calligraphy.jpeg");
        categoryService.createSubCategory("공예", 3, 1L, "https://plub.s3.ap-northeast-2.amazonaws.com/category/subCategory/art/crafts.jpeg");
        categoryService.createSubCategory("DIY", 4, 1L, "https://plub.s3.ap-northeast-2.amazonaws.com/category/subCategory/art/DIY.jpeg");
        categoryService.createSubCategory("사진", 5, 1L, "https://plub.s3.ap-northeast-2.amazonaws.com/category/subCategory/art/photograph.jpeg");
        categoryService.createSubCategory("영상제작", 6, 1L, "");
        categoryService.createSubCategory("춤", 7, 1L, "");
        categoryService.createSubCategory("연기", 8, 1L, "https://plub.s3.ap-northeast-2.amazonaws.com/category/subCategory/art/act.jpeg");
        categoryService.createSubCategory("뮤지컬", 9,1L, "");
        categoryService.createSubCategory("음악", 10, 1L, "https://plub.s3.ap-northeast-2.amazonaws.com/category/subCategory/art/music.jpeg");
        categoryService.createSubCategory("기타", 11, 1L, "");

        // 스포츠 세부 카테고리
        categoryService.createSubCategory("구기종목", 1, 2L, "https://plub.s3.ap-northeast-2.amazonaws.com/category/subCategory/sport/ballgame.jpeg");
        categoryService.createSubCategory("투기종목", 2, 2L, "https://plub.s3.ap-northeast-2.amazonaws.com/category/subCategory/sport/martialarts.jpeg");
        categoryService.createSubCategory("1인스포츠", 3, 2L, "https://plub.s3.ap-northeast-2.amazonaws.com/category/subCategory/sport/solosports.jpeg");
        categoryService.createSubCategory("계절스포츠", 4, 2L, "https://plub.s3.ap-northeast-2.amazonaws.com/category/subCategory/sport/seasonalsports.jpeg");
        categoryService.createSubCategory("그룹스포츠", 5, 2L, "https://plub.s3.ap-northeast-2.amazonaws.com/category/subCategory/sport/groupsports.jpeg");
        categoryService.createSubCategory("스포츠 관람", 6, 2L, "https://plub.s3.ap-northeast-2.amazonaws.com/category/subCategory/sport/watchingsports.jpeg");
        categoryService.createSubCategory("축구", 7, 2L, "https://plub.s3.ap-northeast-2.amazonaws.com/category/subCategory/sport/soccer.jpeg");
        categoryService.createSubCategory("야구", 8, 2L, "https://plub.s3.ap-northeast-2.amazonaws.com/category/subCategory/sport/baseball.jpeg");
        categoryService.createSubCategory("농구", 9, 2L, "https://plub.s3.ap-northeast-2.amazonaws.com/category/subCategory/sport/basketball.jpeg");
        categoryService.createSubCategory("러닝", 10, 2L, "https://plub.s3.ap-northeast-2.amazonaws.com/category/subCategory/sport/running.jpeg");
        categoryService.createSubCategory("기타", 11, 2L, "https://plub.s3.ap-northeast-2.amazonaws.com/category/subCategory/sport/other.jpeg");

        // 제테크/투자 세부 카테고리
        categoryService.createSubCategory("주식", 1, 3L, "https://plub.s3.ap-northeast-2.amazonaws.com/category/subCategory/investment/stock.jpeg");
        categoryService.createSubCategory("부동산", 2, 3L, "https://plub.s3.ap-northeast-2.amazonaws.com/category/subCategory/investment/house.jpeg");
        categoryService.createSubCategory("파생상품", 3, 3L, "");
        categoryService.createSubCategory("코인/NFT", 4, 3L, "https://plub.s3.ap-northeast-2.amazonaws.com/category/subCategory/investment/crypto.jpeg");
        categoryService.createSubCategory("기타", 5, 3L, "https://plub.s3.ap-northeast-2.amazonaws.com/category/subCategory/investment/other.jpeg");

        // 어학 세부 카테고리리
        categoryService.createSubCategory("영어", 1, 4L, "https://plub.s3.ap-northeast-2.amazonaws.com/category/subCategory/language/english.jpeg");
        categoryService.createSubCategory("중국어", 2, 4L, "https://plub.s3.ap-northeast-2.amazonaws.com/category/subCategory/language/chinese.jpeg");
        categoryService.createSubCategory("일본어", 3, 4L, "https://plub.s3.ap-northeast-2.amazonaws.com/category/subCategory/language/japanese.jpeg");
        categoryService.createSubCategory("스페인어", 4, 4L, "https://plub.s3.ap-northeast-2.amazonaws.com/category/subCategory/language/spanish.jpeg");
        categoryService.createSubCategory("프랑스어", 5, 4L, "https://plub.s3.ap-northeast-2.amazonaws.com/category/subCategory/language/france.jpeg");
        categoryService.createSubCategory("베트남어", 6, 4L, "https://plub.s3.ap-northeast-2.amazonaws.com/category/subCategory/language/vietnam.jpeg");
        categoryService.createSubCategory("기타", 7, 4L, "https://plub.s3.ap-northeast-2.amazonaws.com/category/subCategory/language/others.jpeg");

        // 문화 세부 카테고리
        categoryService.createSubCategory("전시", 1, 5L, "https://plub.s3.ap-northeast-2.amazonaws.com/category/subCategory/culture/exhibiyion.jpeg");
        categoryService.createSubCategory("공연", 2, 5L, "https://plub.s3.ap-northeast-2.amazonaws.com/category/subCategory/culture/consert.jpeg");
        categoryService.createSubCategory("패션", 3, 5L, "https://plub.s3.ap-northeast-2.amazonaws.com/category/subCategory/culture/fashon.jpeg");
        categoryService.createSubCategory("뷰티", 4, 5L, "https://plub.s3.ap-northeast-2.amazonaws.com/category/subCategory/culture/beuaty.jpeg");
        categoryService.createSubCategory("기타", 5, 5L, "https://plub.s3.ap-northeast-2.amazonaws.com/category/subCategory/culture/other.jpeg");

        // 음식 세부 카테고리
        categoryService.createSubCategory("요리", 1, 6L, "https://plub.s3.ap-northeast-2.amazonaws.com/category/subCategory/food/cooking.jpeg");
        categoryService.createSubCategory("음료", 2, 6L, "https://plub.s3.ap-northeast-2.amazonaws.com/category/subCategory/food/drink.jpeg");
        categoryService.createSubCategory("주류", 3, 6L, "https://plub.s3.ap-northeast-2.amazonaws.com/category/subCategory/food/alchol.jpeg");
        categoryService.createSubCategory("맛집", 4, 6L, "https://plub.s3.ap-northeast-2.amazonaws.com/category/subCategory/food/matgip.jpeg");
        categoryService.createSubCategory("기타", 5, 6L, "https://plub.s3.ap-northeast-2.amazonaws.com/category/subCategory/food/other.jpeg");

        // 취업/창업 세부 카테고리
        categoryService.createSubCategory("데이터분석", 1, 7L, "https://plub.s3.ap-northeast-2.amazonaws.com/category/subCategory/job/data+analysis.jpeg");
        categoryService.createSubCategory("마케팅", 2, 7L, "https://plub.s3.ap-northeast-2.amazonaws.com/category/subCategory/job/marketing.jpeg");
        categoryService.createSubCategory("파이낸스", 3, 7L, "https://plub.s3.ap-northeast-2.amazonaws.com/category/subCategory/job/finence.jpeg");
        categoryService.createSubCategory("비즈니스", 4, 7L, "https://plub.s3.ap-northeast-2.amazonaws.com/category/subCategory/job/business.jpeg");
        categoryService.createSubCategory("기타", 5, 7L, "https://plub.s3.ap-northeast-2.amazonaws.com/category/subCategory/job/other.jpeg");

        // 컴퓨터 세부 카테고리
        categoryService.createSubCategory("컴퓨터", 1, 8L, "https://plub.s3.ap-northeast-2.amazonaws.com/category/subCategory/computer/computer.jpeg");
        categoryService.createSubCategory("디자인툴", 2, 8L, "https://plub.s3.ap-northeast-2.amazonaws.com/category/subCategory/computer/tool.jpeg");
        categoryService.createSubCategory("코딩", 3, 8L, "https://plub.s3.ap-northeast-2.amazonaws.com/category/subCategory/computer/coding.jpeg");
        categoryService.createSubCategory("기타", 4, 8L, "https://plub.s3.ap-northeast-2.amazonaws.com/category/subCategory/computer/other.jpeg");

        log.info("[1] 카테고리 더미 생성 완료.");
    }
}
