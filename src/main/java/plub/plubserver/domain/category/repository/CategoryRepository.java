package plub.plubserver.domain.category.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import plub.plubserver.domain.category.model.Category;

import java.sql.Timestamp;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    @Query(value = "SELECT MAX(modifiedAt) FROM Category")
    Optional<Timestamp> getLatestDate();
}
