package rw.ac.suggestionbox.validation;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class StudentEmailValidatorTest {

    record Target(@StudentEmail String email) {
    }

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void acceptsInstitutionalAddressCaseInsensitively() {
        assertThat(validator.validate(new Target("Jane.Doe@Student.AC.rw"))).isEmpty();
    }

    @Test
    void leavesBlankToNotBlank() {
        assertThat(validator.validate(new Target(null))).isEmpty();
        assertThat(validator.validate(new Target(" "))).isEmpty();
    }

    @Test
    void rejectsOtherDomainsWithInterpolatedMessage() {
        var violations = validator.validate(new Target("jane@gmail.com"));
        assertThat(violations).singleElement()
                .extracting(v -> v.getMessage())
                .isEqualTo("Use your institutional address ending in @student.ac.rw.");
    }
}
