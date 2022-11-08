package plub.plubserver.domain.category.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import plub.plubserver.domain.category.model.CategorySub;

import java.util.List;
import java.util.Optional;

public interface CategorySubRepository extends JpaRepository<CategorySub, Long> {
    @Query(value = "SELECT MAX(modifiedAt) FROM CategorySub")
    Optional<String> getLatestDate();

    List<CategorySub> findAllByCategoryId(Long category_id);
}
