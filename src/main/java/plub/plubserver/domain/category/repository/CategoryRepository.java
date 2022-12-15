package plub.plubserver.domain.category.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import plub.plubserver.domain.category.model.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
