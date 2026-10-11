package rw.ac.suggestionbox.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import rw.ac.suggestionbox.domain.Suggestion;
import rw.ac.suggestionbox.domain.SuggestionStatus;

import java.util.List;

public interface SuggestionRepository extends JpaRepository<Suggestion, Long> {

    /** The list page always shows the category name, so fetch it in the same query. */
    @EntityGraph(attributePaths = "category")
    List<Suggestion> findAllByOrderByCreatedAtDesc();

    @EntityGraph(attributePaths = "category")
    List<Suggestion> findByStatusOrderByCreatedAtDesc(SuggestionStatus status);

    long countByCategoryId(Long categoryId);
}
