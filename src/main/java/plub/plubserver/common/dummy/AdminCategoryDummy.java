package plub.plubserver.common.dummy;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import plub.plubserver.domain.account.model.Account;
import plub.plubserver.domain.account.repository.AccountRepository;
import plub.plubserver.domain.category.service.CategoryService;

import javax.annotation.PostConstruct;

import static plub.plubserver.common.dummy.DummyImage.PLUB_MAIN_LOGO;
import static plub.plubserver.common.dummy.DummyImage.PLUB_PROFILE_TEST;
import static plub.plubserver.domain.account.dto.AuthDto.SignUpRequest;

@Component
@Order(1)
@RequiredArgsConstructor
@Transactional
public class AdminCategoryDummy {
    private final CategoryService categoryService;
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    @Value("${admin.secret}")
    private CharSequence ADMIN_PASSWORD;

    @PostConstruct
    public void init() {

        // 테스트용 계정 - 어드민
        SignUpRequest adminAccount = SignUpRequest.builder()
                .profileImage(PLUB_MAIN_LOGO)
                .build();
        Account account = adminAccount.toAdmin(passwordEncoder, ADMIN_PASSWORD, "admin1");
        accountRepository.save(account);

        SignUpRequest adminAccount2 = SignUpRequest.builder()
                .profileImage(PLUB_MAIN_LOGO)
                .build();
        Account account2 = adminAccount2.toAdmin(passwordEncoder, ADMIN_PASSWORD, "admin2");
        accountRepository.save(account2);

        // 테스트용 계정 - 더미 유저
        for (int i = 0; i < 20; i++) {
            SignUpRequest dummyAccountForm = SignUpRequest.builder()
                    .profileImage(PLUB_PROFILE_TEST)
                    .build();
            accountRepository.save(dummyAccountForm.toDummy(passwordEncoder, ADMIN_PASSWORD, "dummy" + i));
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

        //예술 세부 카테고리
        categoryService.createSubCategory("미술", 1, Long.valueOf(1));
        categoryService.createSubCategory("켈라그래피", 2, Long.valueOf(1));
        categoryService.createSubCategory("공예", 3, Long.valueOf(1));
        categoryService.createSubCategory("DIY", 4, Long.valueOf(1));
        categoryService.createSubCategory("사진", 5, Long.valueOf(1));
        categoryService.createSubCategory("영상제작", 6, Long.valueOf(1));
        categoryService.createSubCategory("춤", 7, Long.valueOf(1));
        categoryService.createSubCategory("연기", 8, Long.valueOf(1));
        categoryService.createSubCategory("뮤지컬", 9, Long.valueOf(1));
        categoryService.createSubCategory("음악", 10, Long.valueOf(1));
        categoryService.createSubCategory("악기", 11, Long.valueOf(1));
        categoryService.createSubCategory("기타", 12, Long.valueOf(1));

        // 스포츠/피트니스 세부 카테고리
        categoryService.createSubCategory("등산", 1, Long.valueOf(2));
        categoryService.createSubCategory("산책", 2, Long.valueOf(2));
        categoryService.createSubCategory("자전거", 3, Long.valueOf(2));
        categoryService.createSubCategory("배드민턴", 4, Long.valueOf(2));
        categoryService.createSubCategory("볼링", 5, Long.valueOf(2));
        categoryService.createSubCategory("테니스", 6, Long.valueOf(2));
        categoryService.createSubCategory("계절스포츠", 7, Long.valueOf(2));
        categoryService.createSubCategory("골프", 8, Long.valueOf(2));
        categoryService.createSubCategory("클라이밍", 9, Long.valueOf(2));
        categoryService.createSubCategory("헬스", 10, Long.valueOf(2));
        categoryService.createSubCategory("크로스핏", 11, Long.valueOf(2));
        categoryService.createSubCategory("요가", 12, Long.valueOf(2));
        categoryService.createSubCategory("필라테스", 13, Long.valueOf(2));
        categoryService.createSubCategory("탁구", 14, Long.valueOf(2));
        categoryService.createSubCategory("당구", 15, Long.valueOf(2));
        categoryService.createSubCategory("러닝", 16, Long.valueOf(2));
        categoryService.createSubCategory("수영", 17, Long.valueOf(2));
        categoryService.createSubCategory("축구", 18, Long.valueOf(2));
        categoryService.createSubCategory("농구", 19, Long.valueOf(2));
        categoryService.createSubCategory("야구", 20, Long.valueOf(2));
        categoryService.createSubCategory("배구", 21, Long.valueOf(2));
        categoryService.createSubCategory("격투기", 22, Long.valueOf(2));
        categoryService.createSubCategory("스포츠관람", 23, Long.valueOf(2));

        // 제테크/투자 세부 카테고리
        categoryService.createSubCategory("주식", 1, Long.valueOf(3));
        categoryService.createSubCategory("부동산", 2, Long.valueOf(3));
        categoryService.createSubCategory("파생상품", 3, Long.valueOf(3));
        categoryService.createSubCategory("코인/NFT", 4, Long.valueOf(3));
        categoryService.createSubCategory("기타", 5, Long.valueOf(3));

        // 어학 세부 카테고리리
        categoryService.createSubCategory("영어", 1, Long.valueOf(4));
        categoryService.createSubCategory("중국어", 2, Long.valueOf(4));
        categoryService.createSubCategory("일본어", 3, Long.valueOf(4));
        categoryService.createSubCategory("스페인어", 4, Long.valueOf(4));
        categoryService.createSubCategory("프랑스어", 5, Long.valueOf(4));
        categoryService.createSubCategory("베트남어", 6, Long.valueOf(4));
        categoryService.createSubCategory("기타", 7, Long.valueOf(4));

        // 문화 세부 카테고리
        categoryService.createSubCategory("전시", 1, Long.valueOf(5));
        categoryService.createSubCategory("공연", 2, Long.valueOf(5));
        categoryService.createSubCategory("패션", 3, Long.valueOf(5));
        categoryService.createSubCategory("뷰티", 4, Long.valueOf(5));
        categoryService.createSubCategory("기타", 5, Long.valueOf(5));

        // 음식 세부 카테고리
        categoryService.createSubCategory("요리", 1, Long.valueOf(6));
        categoryService.createSubCategory("음료", 2, Long.valueOf(6));
        categoryService.createSubCategory("주류", 3, Long.valueOf(6));
        categoryService.createSubCategory("맛집", 4, Long.valueOf(6));
        categoryService.createSubCategory("기타", 5, Long.valueOf(6));

        // 취업/창업 세부 카테고리
        categoryService.createSubCategory("데이터분석", 1, Long.valueOf(7));
        categoryService.createSubCategory("마케팅", 2, Long.valueOf(7));
        categoryService.createSubCategory("파이낸스", 3, Long.valueOf(7));
        categoryService.createSubCategory("비즈니스", 4, Long.valueOf(7));
        categoryService.createSubCategory("기타", 5, Long.valueOf(7));

        // 컴퓨터 세부 카테고리
        categoryService.createSubCategory("컴퓨터", 1, Long.valueOf(8));
        categoryService.createSubCategory("디자인툴", 2, Long.valueOf(8));
        categoryService.createSubCategory("코딩", 3, Long.valueOf(8));
        categoryService.createSubCategory("프로그래밍", 4, Long.valueOf(8));
    }
}
