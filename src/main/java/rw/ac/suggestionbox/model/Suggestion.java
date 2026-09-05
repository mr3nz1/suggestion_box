package rw.ac.suggestionbox.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * A suggestion dropped in the box. Mapped with a plain Hibernate hbm.xml file (no JPA).
 */
public class Suggestion implements Serializable {

    public static final String[] STATUSES = {"NEW", "UNDER_REVIEW", "ACCEPTED", "REJECTED"};

    private Long id;

    @NotBlank(message = "Title is required.")
    private String title;

    @NotBlank(message = "Suggestion body is required.")
    @Size(min = 10, max = 1000, message = "Suggestion must be 10 to 1000 characters.")
    private String body;

    @NotBlank(message = "Student e-mail is required.")
    @Email(message = "Student e-mail is not a valid address.")
    private String submitterEmail;

    private String status = "NEW";

    private LocalDateTime createdAt = LocalDateTime.now();

    @NotNull(message = "A category must be selected.")
    private Category category;

    public Suggestion() {
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }
}
