package plub.plubserver.domain.policy.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import plub.plubserver.common.exception.StatusCode;
import plub.plubserver.domain.plubbing.exception.PlubbingException;
import plub.plubserver.domain.policy.dto.PolicyDto;
import plub.plubserver.domain.policy.repository.PolicyRepository;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PolicyService {

    private final PolicyRepository policyRepository;

    public PolicyDto getPolicyName(String name) {
        return policyRepository.findByName(name).map(PolicyDto::of)
                .orElseThrow(()-> new PlubbingException(StatusCode.POLICY_NOT_FOUND));
    }
}
