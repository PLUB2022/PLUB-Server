package plub.plubserver.domain.policy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import plub.plubserver.domain.policy.model.Policy;

public interface PolicyRepository extends JpaRepository<Policy, Long> {
}
