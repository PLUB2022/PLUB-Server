package plub.plubserver.domain.recruit.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import plub.plubserver.domain.recruit.model.RecruitQuestionAnswer;

public interface RecruitQuestionAnswerRepository extends JpaRepository<RecruitQuestionAnswer, Long> {
}
