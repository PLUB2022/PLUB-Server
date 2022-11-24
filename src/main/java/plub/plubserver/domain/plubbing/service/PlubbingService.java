package plub.plubserver.domain.plubbing.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import plub.plubserver.domain.account.model.Account;
import plub.plubserver.domain.account.model.AccountPlubbing;
import plub.plubserver.domain.account.model.AccountPlubbingStatus;
import plub.plubserver.domain.account.repository.AccountRepository;
import plub.plubserver.domain.account.service.AccountService;
import plub.plubserver.domain.category.model.PlubbingSubCategory;
import plub.plubserver.domain.category.model.SubCategory;
import plub.plubserver.domain.category.service.CategoryService;
import plub.plubserver.domain.plubbing.dto.PlubbingDto.CreatePlubbingRequest;
import plub.plubserver.domain.plubbing.dto.PlubbingDto.PlubbingResponse;
import plub.plubserver.domain.plubbing.model.Plubbing;
import plub.plubserver.domain.plubbing.model.PlubbingPlace;
import plub.plubserver.domain.plubbing.model.PlubbingStatus;
import plub.plubserver.domain.plubbing.repository.PlubbingRepository;
import plub.plubserver.domain.recruit.model.Question;
import plub.plubserver.domain.recruit.model.Recruit;
import plub.plubserver.util.s3.AwsS3Uploader;
import plub.plubserver.util.s3.S3SaveDir;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PlubbingService {
    private final PlubbingRepository plubbingRepository;
    private final CategoryService categoryService;
    private final AwsS3Uploader awsS3Uploader;
    private final AccountService accountService;
    private final AccountRepository accountRepository; // for test

    private void mappingCategoriesToPlubbing(CreatePlubbingRequest createPlubbingRequest, Plubbing plubbing) {
        // 서브 카테고리 가져오기
        List<SubCategory> subCategories = createPlubbingRequest.subCategories()
                .stream()
                .map(categoryService::getSubCategory)
                .toList();

        // 서브 카테고리 - 모임 매핑 (plubbingSubCategory 엔티티 생성)
        List<PlubbingSubCategory> plubbingSubCategories = subCategories.stream()
                .map(subCategory -> PlubbingSubCategory.builder()
                        .subCategory(subCategory)
                        .plubbing(plubbing)
                        .build())
                .toList();
        
        // 플러빙 객체에 추가 - 더티체킹으로 자동 엔티티 저장
        plubbing.addPlubbingSubCategories(plubbingSubCategories);
    }


    @Transactional
    public PlubbingResponse createPlubbing(CreatePlubbingRequest createPlubbingRequest) {
        // 모임 생성자(호스트) 가져오기
//        Account owner = accountService.getCurrentAccount();
        Account owner = accountRepository.save(Account.builder().email("test@test.com").build()); // for test

        // 메인 이미지 업로드 (선택 했을 시)
        String mainImgFileName = null;
        if (createPlubbingRequest.mainImageFile() != null) {
            AwsS3Uploader.S3FileDto s3FileDto = awsS3Uploader.upload(
                    createPlubbingRequest.mainImageFile(), S3SaveDir.PLUBBING_MAIN_IMAGE, owner
            );
            mainImgFileName = s3FileDto.fileName();
        }

        // Plubbing 엔티티 생성 및 저장
        Plubbing plubbing = plubbingRepository.save(
                Plubbing.builder()
                        .name(createPlubbingRequest.name())
                        .goal(createPlubbingRequest.goal())
                        .mainImageFileName(mainImgFileName)
                        .status(PlubbingStatus.ACTIVE)
                        .onOff(createPlubbingRequest.getOnOff())
                        .maxAccountNum(createPlubbingRequest.maxAccountNum())
                        .plubbingDateList(new ArrayList<>())
                        .plubbingSubCategories(new ArrayList<>())
                        .accountPlubbingList(new ArrayList<>())
                        .timeLineList(new ArrayList<>())
                        .build()
        );

        plubbingRepository.flush();
        // days 매핑
        plubbing.addPlubbingMeetingDay(createPlubbingRequest.getPlubbingMeetingDay(plubbing));
        
        // 오프라인이면 장소도 저장 (온라인 이면 기본값 저장)
        switch (plubbing.getOnOff().name()) {
            case "OFF" -> plubbing.addPlubbingPlace(PlubbingPlace.builder()
                        .address(createPlubbingRequest.address())
                        .placePositionX(createPlubbingRequest.placePositionX())
                        .placePositionY(createPlubbingRequest.placePositionY())
                        .build());

            case "ON" -> plubbing.addPlubbingPlace(new PlubbingPlace());
        }

        // Plubbing - PlubbingSubCategory 매핑
        mappingCategoriesToPlubbing(createPlubbingRequest, plubbing);

        // Plubbing - AccountPlubbing 매핑
        plubbing.addAccountPlubbing(AccountPlubbing.builder()
                .isHost(true)
                .account(owner)
                .plubbing(plubbing)
                .accountPlubbingStatus(AccountPlubbingStatus.ACTIVE)
                .build()
        );

        // 모집 질문글 엔티티화
        List<Question> questionList = createPlubbingRequest.questionTitles().stream()
                .map(it -> Question.builder()
                        .questionTitle(it)
                        .build())
                .toList();

        // 모집 자동 생성
        Recruit recruit = Recruit.builder()
                .title(createPlubbingRequest.title())
                .introduce(createPlubbingRequest.introduce())
                .plubbing(plubbing)
                .questions(questionList)
                .build();
        
        // 질문 - 모집 매핑
        questionList.forEach(it -> it.addRecruit(recruit));
        
        // 모임 - 모집 매핑
        plubbing.addRecruit(recruit);

        plubbingRepository.flush(); // flush를 안 하면 recruitId가 null로 들어감
        
        return PlubbingResponse.of(plubbing);
    }
}
