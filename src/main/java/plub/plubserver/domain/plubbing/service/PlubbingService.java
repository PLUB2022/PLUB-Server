package plub.plubserver.domain.plubbing.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import plub.plubserver.common.dto.PageResponse;
import plub.plubserver.common.model.SortType;
import plub.plubserver.domain.account.config.AccountCode;
import plub.plubserver.domain.account.exception.AccountException;
import plub.plubserver.domain.account.model.Account;
import plub.plubserver.domain.account.model.AccountCategory;
import plub.plubserver.domain.account.repository.AccountCategoryRepository;
import plub.plubserver.domain.account.service.AccountService;
import plub.plubserver.domain.category.model.PlubbingSubCategory;
import plub.plubserver.domain.category.model.SubCategory;
import plub.plubserver.domain.category.service.CategoryService;
import plub.plubserver.domain.plubbing.config.PlubbingCode;
import plub.plubserver.domain.plubbing.dto.PlubbingDto.*;
import plub.plubserver.domain.plubbing.exception.PlubbingException;
import plub.plubserver.domain.plubbing.model.*;
import plub.plubserver.domain.plubbing.repository.AccountPlubbingRepository;
import plub.plubserver.domain.plubbing.repository.PlubbingRepository;
import plub.plubserver.domain.recruit.dto.RecruitDto.UpdateRecruitQuestionRequest;
import plub.plubserver.domain.recruit.dto.RecruitDto.UpdateRecruitRequest;
import plub.plubserver.domain.recruit.model.Recruit;
import plub.plubserver.domain.recruit.model.RecruitQuestion;
import plub.plubserver.domain.recruit.model.RecruitStatus;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PlubbingService {
    private final PlubbingRepository plubbingRepository;
    private final AccountCategoryRepository accountCategoryRepository;
    private final CategoryService categoryService;
    private final AccountService accountService;
    private final AccountPlubbingRepository accountPlubbingRepository;

    public Plubbing getPlubbing(Long plubbingId) {
        return plubbingRepository.findById(plubbingId)
                .orElseThrow(() -> new PlubbingException(PlubbingCode.NOT_FOUND_PLUBBING));
    }

    private void createRecruit(CreatePlubbingRequest createPlubbingRequest, Plubbing plubbing) {
        // 모집 질문글 엔티티화
        List<RecruitQuestion> recruitQuestionList = convertRecruitQuestionEntityList(createPlubbingRequest.questions());

        // 모집 자동 생성
        Recruit recruit = Recruit.builder()
                .title(createPlubbingRequest.title())
                .introduce(createPlubbingRequest.introduce())
                .plubbing(plubbing)
                .recruitQuestionList(recruitQuestionList)
                .questionNum(recruitQuestionList.size())
                .status(RecruitStatus.RECRUITING)
                .visibility(true)
                .build();

        // 질문 - 모집 매핑
        recruitQuestionList.forEach(it -> it.addRecruit(recruit));

        // 모임 - 모집 매핑
        plubbing.addRecruit(recruit);
    }

    private static List<RecruitQuestion> convertRecruitQuestionEntityList(List<String> questions) {
        return questions.stream()
                .map(it -> RecruitQuestion.builder()
                        .questionTitle(it)
                        .build())
                .toList();
    }

    private void connectSubCategories(CreatePlubbingRequest createPlubbingRequest, Plubbing plubbing) {
        // 서브 카테고리 가져오기
        List<SubCategory> subCategories = createPlubbingRequest.subCategoryIds()
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

    /**
     * 모임 생성
     */
    @Transactional
    public PlubbingIdResponse createPlubbing(Account owner, CreatePlubbingRequest createPlubbingRequest) {
        // Plubbing 엔티티 생성 및 저장
        Plubbing plubbing = plubbingRepository.save(createPlubbingRequest.toEntity());

        // days 매핑
        plubbing.addPlubbingMeetingDay(createPlubbingRequest.getPlubbingMeetingDay(plubbing));

        // 오프라인이면 장소도 저장 (온라인 이면 기본값 저장)
        switch (plubbing.getOnOff().name()) {
            case "OFF" -> plubbing.addPlubbingPlace(PlubbingPlace.builder()
                    .address(createPlubbingRequest.address())
                    .roadAddress(createPlubbingRequest.roadAddress())
                    .placeName(createPlubbingRequest.placeName())
                    .placePositionX(createPlubbingRequest.placePositionX())
                    .placePositionY(createPlubbingRequest.placePositionY())
                    .build());

            case "ON" -> plubbing.addPlubbingPlace(new PlubbingPlace());
        }

        // Plubbing - PlubbingSubCategory 매핑
        connectSubCategories(createPlubbingRequest, plubbing);

        // Plubbing - AccountPlubbing 매핑
        plubbing.addAccountPlubbing(AccountPlubbing.builder()
                .isHost(true)
                .account(owner)
                .plubbing(plubbing)
                .accountPlubbingStatus(AccountPlubbingStatus.ACTIVE)
                .build()
        );

        // 모집 자동 생성 및 매핑
        createRecruit(createPlubbingRequest, plubbing);

        plubbingRepository.flush(); // flush를 안 하면 recruitId가 null로 들어감

        return PlubbingIdResponse.of(plubbing);
    }

    /**
     * 호스트
     */
    public Account getHost(Long plubbingId) {
        return accountPlubbingRepository.findByPlubbingId(plubbingId)
                .stream()
                .filter(AccountPlubbing::isHost)
                .findFirst()
                .orElseThrow(() -> new PlubbingException(PlubbingCode.NOT_HOST))
                .getAccount();
    }

    // 호스트 여부 검사
    public void checkHost(Plubbing plubbing) {
        Account currentAccount = accountService.getCurrentAccount();
        AccountPlubbing accountPlubbing = accountPlubbingRepository.findByAccountAndPlubbing(currentAccount, plubbing)
                .orElseThrow(() -> new AccountException(AccountCode.NOT_FOUND_ACCOUNT));
        if (!accountPlubbing.isHost()) throw new PlubbingException(PlubbingCode.NOT_HOST);
    }

    /**
     * 조회
     */
    public MyPlubbingListResponse getMyPlubbing(Boolean isHost) {
        Account currentAccount = accountService.getCurrentAccount();
        List<MyPlubbingResponse> myPlubbingResponses = accountPlubbingRepository.findAllByAccountAndIsHostAndAccountPlubbingStatus(currentAccount, isHost, AccountPlubbingStatus.ACTIVE)
                .stream().map(MyPlubbingResponse::of).collect(Collectors.toList());
        return MyPlubbingListResponse.of(myPlubbingResponses);
    }

    @Transactional
    public MainPlubbingResponse getMainPlubbing(Long plubbingId) {
        Account currentAccount = accountService.getCurrentAccount();
        if (!accountPlubbingRepository.existsByAccountAndPlubbingId(currentAccount, plubbingId))
            throw new PlubbingException(PlubbingCode.FORBIDDEN_ACCESS_PLUBBING);

        Plubbing plubbing = plubbingRepository.findById(plubbingId).orElseThrow(() -> new PlubbingException(PlubbingCode.NOT_FOUND_PLUBBING));
        checkPlubbingStatus(plubbing);

        List<Account> accounts = accountPlubbingRepository.findAllByPlubbingId(plubbingId)
                .stream().map(AccountPlubbing::getAccount).collect(Collectors.toList());

        plubbing.plusView();

        return MainPlubbingResponse.of(plubbing, accounts);
    }

    /**
     * 모임 삭제 (soft delete)
     */
    @Transactional
    public PlubbingMessage deletePlubbing(Long plubbingId) {
        Plubbing plubbing = plubbingRepository.findById(plubbingId).orElseThrow(() -> new PlubbingException(PlubbingCode.NOT_FOUND_PLUBBING));
        checkPlubbingStatus(plubbing);
        checkHost(plubbing);

        plubbing.deletePlubbing();

        accountPlubbingRepository.findAllByPlubbingId(plubbingId)
                .forEach(a -> a.changeStatus(AccountPlubbingStatus.END));

        return new PlubbingMessage(true);
    }

    /**
     * 모임 종료
     */
    @Transactional
    public PlubbingMessage endPlubbing(Long plubbingId) {
        Plubbing plubbing = plubbingRepository.findById(plubbingId).orElseThrow(() -> new PlubbingException(PlubbingCode.NOT_FOUND_PLUBBING));
        checkHost(plubbing);
        List<AccountPlubbing> accountPlubbingList = accountPlubbingRepository.findAllByPlubbingId(plubbingId);
        if (plubbing.getStatus().equals(PlubbingStatus.END)) {
            plubbing.endPlubbing(PlubbingStatus.ACTIVE);
            accountPlubbingList.forEach(a -> a.changeStatus(AccountPlubbingStatus.ACTIVE));
        } else if (plubbing.getStatus().equals(PlubbingStatus.ACTIVE)) {
            plubbing.endPlubbing(PlubbingStatus.END);
            accountPlubbingList.forEach(a -> a.changeStatus(AccountPlubbingStatus.END));
        }
        return new PlubbingMessage(plubbing.getStatus());
    }

    /**
     * 수정
     */
    // 모집글 수정 : 타이틀, 모임 이름, 목표, 모임 소개글, 메인이미지
    @Transactional
    public PlubbingIdResponse updateRecruit(Long plubbingId, UpdateRecruitRequest updateRecruitRequest) {
        Plubbing plubbing = getPlubbing(plubbingId);
        checkHost(plubbing);
        plubbing.updateRecruit(updateRecruitRequest);
        return PlubbingIdResponse.of(plubbing);
    }

    // 모임 정보 수정 : 날짜, 온/오프라인, 최대인원수
    @Transactional
    public PlubbingIdResponse updatePlubbing(Long plubbingId, UpdatePlubbingRequest updatePlubbingRequest) {
        Plubbing plubbing = getPlubbing(plubbingId);
        checkHost(plubbing);
        plubbing.updatePlubbing(updatePlubbingRequest);

        // 오프라인이면 장소도 저장 (온라인 이면 기본값 저장)
        switch (plubbing.getOnOff().name()) {
            case "OFF" -> plubbing.addPlubbingPlace(PlubbingPlace.builder()
                    .address(updatePlubbingRequest.address())
                    .roadAddress(updatePlubbingRequest.roadAddress())
                    .placeName(updatePlubbingRequest.placeName())
                    .placePositionX(updatePlubbingRequest.placePositionX())
                    .placePositionY(updatePlubbingRequest.placePositionY())
                    .build());

            case "ON" -> plubbing.addPlubbingPlace(new PlubbingPlace());
        }
        return PlubbingIdResponse.of(plubbing);
    }

    // 게스트 질문 수정
    @Transactional
    public PlubbingIdResponse updateRecruitQuestion(Long plubbingId, UpdateRecruitQuestionRequest updateRecruitQuestionRequest) {
        Plubbing plubbing = getPlubbing(plubbingId);
        checkHost(plubbing);
        plubbing.getRecruit().updateQuestions(
                convertRecruitQuestionEntityList(updateRecruitQuestionRequest.questions())
        );
        return PlubbingIdResponse.of(plubbing);
    }

    private void checkPlubbingStatus(Plubbing plubbing) {
        if (plubbing.getStatus().equals(PlubbingStatus.END) || !plubbing.isVisibility())
            throw new PlubbingException(PlubbingCode.DELETED_STATUS_PLUBBING);
    }

    public PageResponse<PlubbingCardResponse> getRecommendation(Pageable pageable) {
        Account myAccount = accountService.getCurrentAccount();
        if (!myAccount.getAccountCategories().isEmpty()) {
            List<SubCategory> subCategories = accountCategoryRepository.findAllByAccount(myAccount)
                    .stream().map(AccountCategory::getCategorySub).toList();
            Page<PlubbingCardResponse> plubbingCardResponses = plubbingRepository.findAllBySubCategory(subCategories, pageable).map(PlubbingCardResponse::of);
            return PageResponse.of(plubbingCardResponses);
        } else {
            Page<PlubbingCardResponse> plubbingCardResponses = plubbingRepository.findAllByViews(pageable).map(PlubbingCardResponse::of);
            return PageResponse.of(plubbingCardResponses);
        }
    }

    public PageResponse<PlubbingCardResponse> getPlubbingByCategory(Long categoryId, Pageable pageable, String sort) {
        Page<PlubbingCardResponse> plubbingCardResponses = plubbingRepository.findAllByCategoryId(categoryId, pageable, SortType.of(sort)).map(PlubbingCardResponse::of);
        return PageResponse.of(plubbingCardResponses);
    }
}

