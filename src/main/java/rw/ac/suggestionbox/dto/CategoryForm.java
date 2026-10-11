package rw.ac.suggestionbox.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import rw.ac.suggestionbox.domain.Category;
import rw.ac.suggestionbox.validation.UniqueCategoryName;

/** Create/edit form for a category; {@code id} is null when creating. */
@UniqueCategoryName
public class CategoryForm {

    private Long id;

    @NotBlank(message = "Category name is required.")
    @Size(min = 3, max = 60, message = "Category name must be 3 to 60 characters.")
    private String name;

    @Size(max = 255, message = "Description cannot exceed 255 characters.")
    private String description;

    private boolean active = true;

    public static CategoryForm from(Category category) {
        CategoryForm form = new CategoryForm();
        form.id = category.getId();
        form.name = category.getName();
        form.description = category.getDescription();
        form.active = category.isActive();
        return form;
    }

    public boolean isEditing() {
        return id != null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
