package plub.plubserver.domain.plubbing.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import plub.plubserver.domain.account.model.Account;
import plub.plubserver.domain.account.model.AccountPlubbing;
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
        Account owner = accountService.getCurrentAccount();

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
                        .days(createPlubbingRequest.days())
                        .onOff(createPlubbingRequest.getOnOff())
                        .maxAccountNum(createPlubbingRequest.maxAccountNum())
                        .plubbingDateList(new ArrayList<>())
                        .plubbingSubCategories(new ArrayList<>())
                        .accountPlubbingList(new ArrayList<>())
                        .timeLineList(new ArrayList<>())
                        .build()
        );
        
        // 오프라인이면 장소도 저장
        if (plubbing.getOnOff().name().equals("OFF")) {
            plubbing.addPlubbingPlace(PlubbingPlace.builder()
                            .placePositionX(createPlubbingRequest.placePositionX())
                            .placePositionY(createPlubbingRequest.placePositionY())
                            .build());
        }

        // Plubbing - PlubbingSubCategory 매핑
        mappingCategoriesToPlubbing(createPlubbingRequest, plubbing);

        // Plubbing - AccountPlubbing 매핑
        plubbing.addAccountPlubbing(AccountPlubbing.builder()
                .isHost(true)
                .account(owner)
                .plubbing(plubbing)
                .build()
        );

        // TODO : 모집글 자동 생성

        return PlubbingResponse.of(plubbing);
    }
}
