package rw.ac.suggestionbox.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Class-level constraint for a category form: the name must not already belong to
 * another category (case-insensitive). Class-level because it needs both name and id.
 */
@Documented
@Constraint(validatedBy = UniqueCategoryNameValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface UniqueCategoryName {

    String message() default "A category with this name already exists.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
