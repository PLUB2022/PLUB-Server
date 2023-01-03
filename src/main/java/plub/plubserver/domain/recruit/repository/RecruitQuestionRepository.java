package plub.plubserver.domain.recruit.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import plub.plubserver.domain.recruit.model.RecruitQuestion;

public interface RecruitQuestionRepository extends JpaRepository<RecruitQuestion, Long> {
}
