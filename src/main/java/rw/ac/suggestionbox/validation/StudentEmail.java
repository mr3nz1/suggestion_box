package rw.ac.suggestionbox.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** Only institutional student addresses may post a suggestion. */
@Documented
@Constraint(validatedBy = StudentEmailValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface StudentEmail {

    String domain() default "@student.ac.rw";

    String message() default "Use your institutional address ending in {domain}.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
