package plub.plubserver.domain.plubbing.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import plub.plubserver.domain.plubbing.model.Plubbing;

import java.util.List;

public interface PlubbingRepository extends JpaRepository<Plubbing, Long>, JpaSpecificationExecutor<Plubbing> {
    @Query(value = "SELECT p FROM Plubbing p JOIN PlubbingSubCategory psc ON p.id = psc.plubbing.id JOIN SubCategory sc ON sc.id = psc.subCategory.id WHERE sc.category.id = ?1")
    List<Plubbing> findAllByCategoryId(Long categoryId);

    @Query(value = "SELECT p FROM Plubbing p JOIN PlubbingSubCategory psc ON p.id = psc.plubbing.id WHERE psc.subCategory.id = ?1")
    List<Plubbing> findAllBySubCategoryId(Long categoryId);
}

