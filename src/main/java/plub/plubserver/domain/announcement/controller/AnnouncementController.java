package plub.plubserver.domain.announcement.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;
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
    @ApiOperation(value = "앱 공지사항 생성")
    @PostMapping("")
    public AnnouncementIdResponse createAnnouncement(
            @Valid @RequestBody AnnouncementRequest createAnnouncementRequest
    ) {
        return announcementService.createAnnouncement(createAnnouncementRequest);
    }

    @ApiOperation(value = "앱 공지사항 전체 조회")
    @GetMapping("")
    public AnnouncementListResponse getAnnouncementList(
            @PageableDefault(direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return announcementService.getAnnouncementList(pageable);
    }

    @ApiOperation(value = "앱 공지사항 상세 조회")
    @GetMapping("/{announcementId}")
    public AnnouncementResponse getAnnouncement(
            @PathVariable Long announcementId
    ) {
        return announcementService.getAnnouncement(announcementId);
    }

    @ApiOperation(value = "앱 공지사항 삭제")
    @DeleteMapping("/{announcementId}")
    public AnnouncementMessage deleteAnnouncement(
            @PathVariable Long announcementId
    ) {
        return announcementService.softDeleteAnnouncement(announcementId);
    }

    @ApiOperation(value = "앱 공지사항 수정")
    @PutMapping("/{announcementId}")
    public AnnouncementIdResponse updateAnnouncement(
            @PathVariable Long announcementId,
            @Valid @RequestBody AnnouncementRequest updateAnnouncementRequest
    ) {
        return announcementService.updateAnnouncement(announcementId, updateAnnouncementRequest);
    }

}
