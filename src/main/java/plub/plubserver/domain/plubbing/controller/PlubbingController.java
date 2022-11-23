package plub.plubserver.domain.plubbing.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import plub.plubserver.common.dto.ApiResponse;
import plub.plubserver.domain.plubbing.dto.PlubbingDto.CreatePlubbingRequest;
import plub.plubserver.domain.plubbing.dto.PlubbingDto.PlubbingResponse;
import plub.plubserver.domain.plubbing.service.PlubbingService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/plubbing")
@Slf4j
public class PlubbingController {
    private final PlubbingService plubbingService;

    @PostMapping
    public ApiResponse<PlubbingResponse> createPlubbing(
            @ModelAttribute CreatePlubbingRequest createPlubbingRequest) {
        return ApiResponse.success(
                plubbingService.createPlubbing(createPlubbingRequest),
                "plubbing is successfully created."
        );
    }
}
