package rw.ac.suggestionbox.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rw.ac.suggestionbox.domain.Category;
import rw.ac.suggestionbox.domain.Suggestion;
import rw.ac.suggestionbox.domain.SuggestionStatus;
import rw.ac.suggestionbox.dto.SuggestionForm;
import rw.ac.suggestionbox.repository.SuggestionRepository;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class SuggestionService {

    private final SuggestionRepository suggestions;
    private final CategoryService categoryService;

    public SuggestionService(SuggestionRepository suggestions, CategoryService categoryService) {
        this.suggestions = suggestions;
        this.categoryService = categoryService;
    }

    /** All suggestions, newest first; {@code status == null} means no filter. */
    public List<Suggestion> findAll(SuggestionStatus status) {
        return status == null
                ? suggestions.findAllByOrderByCreatedAtDesc()
                : suggestions.findByStatusOrderByCreatedAtDesc(status);
    }

    public Suggestion get(Long id) {
        return suggestions.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Suggestion", id));
    }

    @Transactional
    public Suggestion create(SuggestionForm form) {
        Category category = categoryService.get(form.getCategoryId());
        return suggestions.save(new Suggestion(form.getTitle().trim(), form.getBody(),
                form.getSubmitterEmail().trim(), form.getStatus(), category));
    }

    @Transactional
    public Suggestion update(Long id, SuggestionForm form) {
        Suggestion suggestion = get(id);
        Category category = categoryService.get(form.getCategoryId());
        suggestion.update(form.getTitle().trim(), form.getBody(),
                form.getSubmitterEmail().trim(), form.getStatus(), category);
        return suggestion;
    }

    @Transactional
    public void delete(Long id) {
        suggestions.delete(get(id));
    }
}
