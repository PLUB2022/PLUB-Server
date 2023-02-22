package plub.plubserver.domain.policy.dto;

import lombok.Builder;
import plub.plubserver.domain.policy.model.Policy;

public record PolicyDto(
        String title,
        String content
) {
    @Builder
    public PolicyDto {
    }

    public static PolicyDto of(Policy policy) {
        return new PolicyDto(
                policy.getTitle(),
                policy.getContent()
        );
    }
}

