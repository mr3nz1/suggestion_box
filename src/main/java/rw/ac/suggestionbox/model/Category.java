package rw.ac.suggestionbox.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.io.Serializable;

/**
 * Suggestion category. Mapped with a plain Hibernate hbm.xml file (no JPA annotations).
 * The jakarta.validation annotations below are validation type 3 (Bean Validation);
 * JSF picks them up automatically during the Process Validations phase.
 */
public class Category implements Serializable {

    private Long id;

    @NotBlank(message = "Category name is required.")
    @Size(min = 3, max = 60, message = "Category name must be 3 to 60 characters.")
    private String name;

    private String description;

    private boolean active = true;

    public Category() {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Category other) || id == null) {
            return false;
        }
        return id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return id == null ? 0 : id.hashCode();
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
