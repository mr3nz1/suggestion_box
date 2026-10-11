package rw.ac.suggestionbox.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class StudentEmailValidator implements ConstraintValidator<StudentEmail, String> {

    private String domain;

    @Override
    public void initialize(StudentEmail annotation) {
        domain = annotation.domain().toLowerCase();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) {
            return true; // @NotBlank reports the empty case
        }
        return value.trim().toLowerCase().endsWith(domain);
    }
}
