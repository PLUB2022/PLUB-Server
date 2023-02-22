package plub.plubserver.domain.policy.controller;

import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import plub.plubserver.domain.policy.dto.PolicyDto;
import plub.plubserver.domain.policy.service.PolicyService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/policys")
@Slf4j
@Api(tags = "정책 API", hidden = true)
public class PolicyController {

    private final PolicyService policyService;

    @GetMapping("/{policyName}")
    public PolicyDto getPolicy(@PathVariable String policyName) {
        return policyService.getPolicyName(policyName);
    }
}
