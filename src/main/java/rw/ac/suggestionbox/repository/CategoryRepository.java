package rw.ac.suggestionbox.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rw.ac.suggestionbox.domain.Category;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    List<Category> findAllByOrderByNameAsc();

    boolean existsByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);
}
