package rw.ac.suggestionbox.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rw.ac.suggestionbox.domain.Category;
import rw.ac.suggestionbox.dto.CategoryForm;
import rw.ac.suggestionbox.repository.CategoryRepository;
import rw.ac.suggestionbox.repository.SuggestionRepository;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class CategoryService {

    private final CategoryRepository categories;
    private final SuggestionRepository suggestions;

    public CategoryService(CategoryRepository categories, SuggestionRepository suggestions) {
        this.categories = categories;
        this.suggestions = suggestions;
    }

    public List<Category> findAll() {
        return categories.findAllByOrderByNameAsc();
    }

    public Category get(Long id) {
        return categories.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", id));
    }

    @Transactional
    public Category create(CategoryForm form) {
        return categories.save(new Category(form.getName().trim(), form.getDescription(), form.isActive()));
    }

    @Transactional
    public Category update(Long id, CategoryForm form) {
        Category category = get(id);
        category.update(form.getName().trim(), form.getDescription(), form.isActive());
        return category;
    }

    @Transactional
    public void delete(Long id) {
        Category category = get(id);
        long used = suggestions.countByCategoryId(id);
        if (used > 0) {
            throw new CategoryInUseException(category.getName(), used);
        }
        categories.delete(category);
    }

    public boolean nameTaken(String name, Long excludedId) {
        if (name == null || name.isBlank()) {
            return false;
        }
        String trimmed = name.trim();
        return excludedId == null
                ? categories.existsByNameIgnoreCase(trimmed)
                : categories.existsByNameIgnoreCaseAndIdNot(trimmed, excludedId);
    }
}
