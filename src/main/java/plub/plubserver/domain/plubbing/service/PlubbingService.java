package plub.plubserver.domain.plubbing.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import plub.plubserver.common.dto.PageResponse;
import plub.plubserver.common.exception.StatusCode;
import plub.plubserver.common.model.SortType;
import plub.plubserver.domain.account.model.Account;
import plub.plubserver.domain.account.model.AccountCategory;
import plub.plubserver.domain.account.repository.AccountCategoryRepository;
import plub.plubserver.domain.account.service.AccountService;
import plub.plubserver.domain.category.model.PlubbingSubCategory;
import plub.plubserver.domain.category.model.SubCategory;
import plub.plubserver.domain.category.service.CategoryService;
import plub.plubserver.domain.notification.service.NotificationService;
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
import plub.plubserver.domain.recruit.repository.BookmarkRepository;
import plub.plubserver.domain.report.dto.ReportDto.CreateReportRequest;
import plub.plubserver.domain.report.dto.ReportDto.ReportResponse;
import plub.plubserver.domain.report.model.Report;
import plub.plubserver.domain.report.service.ReportService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PlubbingService {
    private final PlubbingRepository plubbingRepository;
    private final CategoryService categoryService;
    private final AccountService accountService;
    private final NotificationService notificationService;
    private final AccountCategoryRepository accountCategoryRepository;
    private final AccountPlubbingRepository accountPlubbingRepository;
    private final BookmarkRepository bookmarkRepository;
    private final ReportService reportService;

    public Plubbing getPlubbing(Long plubbingId) {
        return plubbingRepository.findById(plubbingId)
                .orElseThrow(() -> new PlubbingException(StatusCode.NOT_FOUND_PLUBBING));
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

        // 이미지 설정
        String mainImage = createPlubbingRequest.mainImage();
        if (createPlubbingRequest.mainImage() == null || createPlubbingRequest.mainImage().equals("")) {
            SubCategory subCategory = createPlubbingRequest.subCategoryIds()
                    .stream()
                    .map(categoryService::getSubCategory)
                    .findFirst().orElseThrow(() -> new PlubbingException(StatusCode.NOT_FOUND_SUB_CATEGORY));
            mainImage = subCategory.getDefaultImage();
        }
        plubbing.setMainImage(mainImage);

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
        return accountPlubbingRepository.findByPlubbingIdAndIsHost(plubbingId)
                .orElseThrow(() -> new PlubbingException(StatusCode.NOT_HOST_ERROR))
                .getAccount();
    }

    // 호스트 여부 검사
    public void checkHost(Account account, Plubbing plubbing) {
        AccountPlubbing accountPlubbing = accountPlubbingRepository.findByAccountAndPlubbing(account, plubbing)
                .orElseThrow(() -> new PlubbingException(StatusCode.NOT_MEMBER_ERROR));
        if (!accountPlubbing.isHost()) throw new PlubbingException(StatusCode.NOT_HOST_ERROR);
    }

    public void checkHost(Plubbing plubbing) {
        checkHost(accountService.getCurrentAccount(), plubbing);
    }

    public void checkHost(Long plubbingId) {
        checkHost(getPlubbing(plubbingId));
    }

    public Boolean isHost(Account account, Plubbing plubbing) {
        Optional<AccountPlubbing> accountPlubbing = accountPlubbingRepository.findByAccountAndPlubbing(account, plubbing);
        return accountPlubbing.map(AccountPlubbing::isHost).orElse(false);
    }

    /**
     * 조회
     */
    public MyPlubbingListResponse getMyPlubbing(Boolean isHost) {
        Account currentAccount = accountService.getCurrentAccount();
        List<MyPlubbingResponse> myPlubbingResponses = accountPlubbingRepository.findAllByAccountAndIsHostAndAccountPlubbingStatus(currentAccount, isHost, AccountPlubbingStatus.ACTIVE)
                .stream().map(MyPlubbingResponse::of).toList();
        return MyPlubbingListResponse.of(myPlubbingResponses);
    }

    @Transactional
    public MainPlubbingResponse getMainPlubbing(Long plubbingId) {
        Account currentAccount = accountService.getCurrentAccount();
        if (!accountPlubbingRepository.existsByAccountAndPlubbingId(currentAccount, plubbingId))
            throw new PlubbingException(StatusCode.FORBIDDEN_ACCESS_PLUBBING);

        Plubbing plubbing = plubbingRepository.findById(plubbingId)
                .orElseThrow(() -> new PlubbingException(StatusCode.NOT_FOUND_PLUBBING));
        checkPlubbingStatus(plubbing);

        List<Account> accounts = accountPlubbingRepository.findAllByPlubbingId(plubbingId)
                .stream().map(AccountPlubbing::getAccount).toList();

        plubbing.plusView();

        return MainPlubbingResponse.of(plubbing, accounts);
    }

    // 마이페이지
    public MyProfilePlubbingListResponse getMyPlubbingByStatus(String status) {
        Account currentAccount = accountService.getCurrentAccount();
        PlubbingStatus plubbingStatus = PlubbingStatus.valueOf(status);

        List<MyProfilePlubbingResponse> myPlubbingResponses = accountPlubbingRepository
                .findAllByAccount(currentAccount, plubbingStatus).stream()
                .map((AccountPlubbing accountPlubbing) -> {
                    MyPlubbingStatus myPlubbingStatus =
                            getMyPlubbingStatus(accountPlubbing.isHost(), plubbingStatus);
                    return MyProfilePlubbingResponse.of(accountPlubbing.getPlubbing(), myPlubbingStatus);
                }).toList();
        return MyProfilePlubbingListResponse.of(myPlubbingResponses, plubbingStatus);
    }

    public MyPlubbingStatus getMyPlubbingStatus(boolean isHost, PlubbingStatus plubbingStatus) {
        if(plubbingStatus.equals(PlubbingStatus.END)) return MyPlubbingStatus.END;
        else if (isHost) return MyPlubbingStatus.HOST;
        else return MyPlubbingStatus.GUEST;
    }


    /**
     * 모임 삭제 (soft delete)
     */
    @Transactional
    public PlubbingMessage deletePlubbing(Long plubbingId) {
        Plubbing plubbing = plubbingRepository.findById(plubbingId)
                .orElseThrow(() -> new PlubbingException(StatusCode.NOT_FOUND_PLUBBING));
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
        Plubbing plubbing = plubbingRepository.findById(plubbingId)
                .orElseThrow(() -> new PlubbingException(StatusCode.NOT_FOUND_PLUBBING));
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

    // 모임 정보 수정 : 날짜, 온/오프라인, 최대인원수, 시간
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
            throw new PlubbingException(StatusCode.DELETED_STATUS_PLUBBING);
    }

    public PageResponse<PlubbingCardResponse> getRecommendation(Pageable pageable) {
        Account myAccount = accountService.getCurrentAccount();
        if (!myAccount.getAccountCategories().isEmpty()) {
            List<SubCategory> subCategories = accountCategoryRepository.findAllByAccount(myAccount)
                    .stream().map(AccountCategory::getCategorySub).toList();
            Page<PlubbingCardResponse> plubbingCardResponses = plubbingRepository.findAllBySubCategory(subCategories, pageable)
                    .map(p -> PlubbingCardResponse.of(p, isHost(myAccount, p), isBookmarked(myAccount, p)));
            return PageResponse.of(plubbingCardResponses);
        } else {
            Page<PlubbingCardResponse> plubbingCardResponses = plubbingRepository.findAllByViews(pageable)
                    .map(p -> PlubbingCardResponse.of(p, isHost(myAccount, p), isBookmarked(myAccount, p)));
            return PageResponse.of(plubbingCardResponses);
        }
    }

    public PageResponse<PlubbingCardResponse> getPlubbingByCategory(
            Long categoryId,
            Pageable pageable,
            String sort,
            PlubbingCardRequest plubbingCardRequest
    ) {
        Account myAccount = accountService.getCurrentAccount();
        if (plubbingCardRequest == null) {
            return PageResponse.of(plubbingRepository.findAllByCategoryId(categoryId, pageable, SortType.of(sort))
                    .map(p -> PlubbingCardResponse.of(p, isHost(myAccount, p), isBookmarked(myAccount, p))));
        }

        Integer accountNum = plubbingCardRequest.accountNum();
        List<Long> subCategoryId = plubbingCardRequest.subCategoryId();
        List<String> days = plubbingCardRequest.days();
        List<MeetingDay> meetingDays = new ArrayList<>();
        if (days != null)
            meetingDays = days.stream().map(MeetingDay::valueOf).toList();

        Page<PlubbingCardResponse> plubbingCardResponses = plubbingRepository
                .findAllByCategory(categoryId, subCategoryId, meetingDays, accountNum, pageable, SortType.of(sort))
                .map(p -> PlubbingCardResponse.of(p, isHost(myAccount, p), isBookmarked(myAccount, p)));
        return PageResponse.of(plubbingCardResponses);
    }

    // 모임 나가기
    @Transactional
    public void leavePlubbing(Long plubbingId) {
        Plubbing plubbing = getPlubbing(plubbingId);
        Account account = accountService.getCurrentAccount();
        accountPlubbingRepository.deleteByPlubbingAndAccount(plubbing, account);

        // 호스트에게 푸시 알림
        notificationService.pushMessage(
                plubbing.getHost(),
                plubbing.getName(),
                account.getNickname() + "님이 모임을 나갔어요."
        );
    }

    public void checkMember(Account account, Plubbing plubbing) {
        accountPlubbingRepository.findByAccountAndPlubbing(account, plubbing)
                .orElseThrow(() -> new PlubbingException(StatusCode.NOT_MEMBER_ERROR));
    }

    public Boolean isBookmarked(Account account, Plubbing plubbing) {
        return bookmarkRepository.existsByAccountAndRecruit(account, plubbing.getRecruit());
    }

    /**
     * 모임 신고
     */
    @Transactional
    public ReportResponse reportPlubbing(CreateReportRequest createReportRequest, Account reporter) {
        Report report = reportService.createReport(createReportRequest, reporter);
        Plubbing plubbing = getPlubbing(createReportRequest.reportTargetId()); // 존재여부도 확인
        return reportService.notifyHost(report, plubbing);
    }
}

