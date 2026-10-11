package rw.ac.suggestionbox.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import rw.ac.suggestionbox.dto.CategoryForm;
import rw.ac.suggestionbox.service.CategoryService;

/** Spring creates this validator, so the service is constructor-injected. */
public class UniqueCategoryNameValidator implements ConstraintValidator<UniqueCategoryName, CategoryForm> {

    private final CategoryService categoryService;

    public UniqueCategoryNameValidator(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @Override
    public boolean isValid(CategoryForm form, ConstraintValidatorContext context) {
        if (form == null || !categoryService.nameTaken(form.getName(), form.getId())) {
            return true;
        }
        // Attach the error to the name field so the page shows it next to the input.
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(
                        "A category named '" + form.getName().trim() + "' already exists.")
                .addPropertyNode("name")
                .addConstraintViolation();
        return false;
    }
}
