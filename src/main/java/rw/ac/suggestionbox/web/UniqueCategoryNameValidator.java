package rw.ac.suggestionbox.web;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.validator.FacesValidator;
import jakarta.faces.validator.Validator;
import jakarta.faces.validator.ValidatorException;
import jakarta.inject.Inject;
import rw.ac.suggestionbox.dao.CategoryDao;

/**
 * Validation type 2 - custom JSF validator, CDI managed so the DAO can be injected.
 * Rejects a category name that already exists (case-insensitive).
 */
@FacesValidator(value = "uniqueCategoryNameValidator", managed = true)
public class UniqueCategoryNameValidator implements Validator<String> {

    @Inject
    private CategoryDao categoryDao;

    @Override
    public void validate(FacesContext context, UIComponent component, String value) {
        if (value == null || value.isBlank()) {
            return;
        }
        Long currentId = (Long) component.getAttributes().get("categoryId");
        if (categoryDao.nameTaken(value.trim(), currentId)) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Duplicate category", "A category named '" + value.trim() + "' already exists."));
        }
    }
}
