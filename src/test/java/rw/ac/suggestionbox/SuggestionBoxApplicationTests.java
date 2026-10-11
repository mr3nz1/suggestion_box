package rw.ac.suggestionbox;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import rw.ac.suggestionbox.domain.Category;
import rw.ac.suggestionbox.domain.SuggestionStatus;
import rw.ac.suggestionbox.repository.CategoryRepository;
import rw.ac.suggestionbox.repository.SuggestionRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** Full stack (controllers, services, JPA, Thymeleaf) against an in-memory H2 database. */
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:suggestionbox;DB_CLOSE_DELAY=-1",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
class SuggestionBoxApplicationTests {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private CategoryRepository categories;

    @Autowired
    private SuggestionRepository suggestions;

    @Test
    void suggestionLifecycle() throws Exception {
        Category library = categories.save(new Category("Library Services", "Books", true));

        mvc.perform(post("/suggestions")
                        .param("title", "Longer opening hours")
                        .param("body", "Please open the library until midnight during exams.")
                        .param("submitterEmail", "jane@student.ac.rw")
                        .param("categoryId", library.getId().toString())
                        .param("status", "NEW"))
                .andExpect(redirectedUrl("/suggestions"));

        assertThat(suggestions.findAll()).singleElement()
                .satisfies(s -> assertThat(s.getCreatedAt()).isNotNull());

        mvc.perform(get("/suggestions").param("status", "NEW"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Longer opening hours")))
                .andExpect(content().string(containsString("Library Services")));

        mvc.perform(get("/suggestions").param("status", "ACCEPTED"))
                .andExpect(content().string(containsString("No suggestions yet.")));

        Long id = suggestions.findAll().getFirst().getId();
        mvc.perform(post("/suggestions/{id}", id)
                        .param("title", "Longer opening hours")
                        .param("body", "Please open the library until midnight during exams.")
                        .param("submitterEmail", "jane@student.ac.rw")
                        .param("categoryId", library.getId().toString())
                        .param("status", "ACCEPTED"))
                .andExpect(redirectedUrl("/suggestions"));
        assertThat(suggestions.findById(id)).get()
                .extracting(s -> s.getStatus()).isEqualTo(SuggestionStatus.ACCEPTED);

        // Category still in use: delete is refused.
        mvc.perform(post("/categories/{id}/delete", library.getId()))
                .andExpect(redirectedUrl("/categories"));
        assertThat(categories.existsById(library.getId())).isTrue();

        mvc.perform(post("/suggestions/{id}/delete", id)).andExpect(redirectedUrl("/suggestions"));
        mvc.perform(post("/categories/{id}/delete", library.getId())).andExpect(redirectedUrl("/categories"));
        assertThat(categories.count()).isZero();
    }

    @Test
    void rejectsNonStudentEmail() throws Exception {
        mvc.perform(post("/suggestions")
                        .param("title", "Hello there")
                        .param("body", "This body is long enough.")
                        .param("submitterEmail", "jane@gmail.com"))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrorCode("form", "submitterEmail", "StudentEmail"))
                .andExpect(model().attributeHasFieldErrorCode("form", "categoryId", "NotNull"));
    }

    @Test
    void unknownIdIs404() throws Exception {
        mvc.perform(get("/categories/{id}/edit", 999_999)).andExpect(status().isNotFound());
    }

    @Test
    void pagesRender() throws Exception {
        mvc.perform(get("/")).andExpect(status().isOk());
        mvc.perform(get("/categories")).andExpect(status().isOk());
        mvc.perform(get("/css/app.css")).andExpect(status().isOk());
    }
}
