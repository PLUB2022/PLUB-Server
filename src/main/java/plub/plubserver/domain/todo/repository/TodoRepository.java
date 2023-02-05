package plub.plubserver.domain.todo.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import plub.plubserver.domain.account.model.Account;
import plub.plubserver.domain.todo.model.Todo;

import java.util.List;
import java.util.Optional;

public interface TodoRepository extends JpaRepository<Todo, Long> {

    Optional<Todo> findByIdAndAccount(Long id, Account account);
    Page<Todo> findByAndAccountId(Long accountId, Pageable pageable);
    List<Todo> findByAccountId(Long accountId);
}
