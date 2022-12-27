package plub.plubserver.domain.category.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import plub.plubserver.domain.category.model.SubCategory;

import java.util.List;
import java.util.Optional;

public interface SubCategoryRepository extends JpaRepository<SubCategory, Long> {
    List<SubCategory> findAllByCategoryId(Long categoryId);

    Optional<SubCategory> findByName(String name);
}