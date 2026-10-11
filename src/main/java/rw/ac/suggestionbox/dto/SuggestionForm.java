package rw.ac.suggestionbox.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import rw.ac.suggestionbox.domain.Suggestion;
import rw.ac.suggestionbox.domain.SuggestionStatus;
import rw.ac.suggestionbox.validation.StudentEmail;

/** Create/edit form for a suggestion; {@code id} is null when creating. */
public class SuggestionForm {

    private Long id;

    @NotBlank(message = "Title is required.")
    @Size(min = 5, max = 120, message = "Title must be 5 to 120 characters.")
    private String title;

    @NotBlank(message = "Suggestion body is required.")
    @Size(min = 10, max = 1000, message = "Suggestion must be 10 to 1000 characters.")
    private String body;

    @NotBlank(message = "Student e-mail is required.")
    @Email(message = "Student e-mail is not a valid address.")
    @StudentEmail
    private String submitterEmail;

    @NotNull(message = "A category must be selected.")
    private Long categoryId;

    @NotNull
    private SuggestionStatus status = SuggestionStatus.NEW;

    public static SuggestionForm from(Suggestion suggestion) {
        SuggestionForm form = new SuggestionForm();
        form.id = suggestion.getId();
        form.title = suggestion.getTitle();
        form.body = suggestion.getBody();
        form.submitterEmail = suggestion.getSubmitterEmail();
        form.categoryId = suggestion.getCategory().getId();
        form.status = suggestion.getStatus();
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getSubmitterEmail() {
        return submitterEmail;
    }

    public void setSubmitterEmail(String submitterEmail) {
        this.submitterEmail = submitterEmail;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public SuggestionStatus getStatus() {
        return status;
    }

    public void setStatus(SuggestionStatus status) {
        this.status = status;
    }
}
