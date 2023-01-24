package plub.plubserver.domain.todo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import plub.plubserver.domain.todo.model.PlubbingTodo;

import java.util.Optional;

public interface PlubbingTodoRepository extends JpaRepository<PlubbingTodo, Long> {
    Optional<PlubbingTodo> findByIdAndPlubbingId(Long id, Long plubbingId);

}
