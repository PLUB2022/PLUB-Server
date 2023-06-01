package plub.plubserver.domain.announcement.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;
import plub.plubserver.common.dto.PageResponse;
import plub.plubserver.domain.account.model.Account;
import plub.plubserver.domain.account.service.AccountService;
import plub.plubserver.domain.announcement.service.AnnouncementService;

import javax.validation.Valid;

import static plub.plubserver.domain.announcement.dto.AnnouncementDto.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/announcements")
@Slf4j
@Api(tags = "앱 공지사항 API")
public class AnnouncementController {

    private final AnnouncementService announcementService;
    private final AccountService accountService;

    @ApiOperation(value = "앱 공지사항 생성")
    @PostMapping("")
    public AnnouncementIdResponse createAnnouncement(
            @Valid @RequestBody AnnouncementRequest createAnnouncementRequest
    ) {
        Account currentAccount = accountService.getCurrentAccount();
        return announcementService.createAnnouncement(currentAccount, createAnnouncementRequest);
    }

    @ApiOperation(value = "앱 공지사항 전체 조회")
    @GetMapping("")
    public AnnouncementListResponse getAnnouncementList(
            @PageableDefault(direction = Sort.Direction.DESC) Pageable pageable,
            @RequestParam(required = false) Long cursorId
    ) {
        return announcementService.getAnnouncementList(pageable, cursorId);
    }

    @ApiOperation(value = "앱 공지사항 전체 조회 (WEB)")
    @GetMapping("/web")
    public  PageResponse<AnnouncementResponse> getAnnouncementListWithWeb(
            @PageableDefault(direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return announcementService.getAnnouncementListWithWeb(pageable);
    }


    @ApiOperation(value = "앱 공지사항 상세 조회")
    @GetMapping("/{announcementId}")
    public AnnouncementDataResponse getAnnouncement(
            @PathVariable Long announcementId
    ) {
        return announcementService.getAnnouncementDetails(announcementId);
    }

    @ApiOperation(value = "앱 공지사항 삭제")
    @DeleteMapping("/{announcementId}")
    public AnnouncementMessage deleteAnnouncement(
            @PathVariable Long announcementId
    ) {
        Account currentAccount = accountService.getCurrentAccount();
        return announcementService.softDeleteAnnouncement(announcementId, currentAccount);
    }

    @ApiOperation(value = "앱 공지사항 수정")
    @PutMapping("/{announcementId}")
    public AnnouncementResponse updateAnnouncement(
            @PathVariable Long announcementId,
            @Valid @RequestBody AnnouncementRequest updateAnnouncementRequest
    ) {
        Account currentAccount = accountService.getCurrentAccount();
        return announcementService.updateAnnouncement(announcementId, currentAccount, updateAnnouncementRequest);
    }
}
