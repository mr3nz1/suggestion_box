package rw.ac.suggestionbox.web;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.validator.FacesValidator;
import jakarta.faces.validator.Validator;
import jakarta.faces.validator.ValidatorException;

/**
 * Validation type 2 - custom JSF validator.
 * Only institutional student addresses may post a suggestion.
 */
@FacesValidator("studentEmailValidator")
public class StudentEmailValidator implements Validator<String> {

    private static final String DOMAIN = "@student.ac.rw";

    @Override
    public void validate(FacesContext context, UIComponent component, String value) {
        if (value == null || value.isBlank()) {
            return; // let required="true" report the empty case
        }
        if (!value.toLowerCase().endsWith(DOMAIN)) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Invalid e-mail", "Use your institutional address ending in " + DOMAIN + "."));
        }
    }
}
