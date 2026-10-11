package rw.ac.suggestionbox.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import rw.ac.suggestionbox.service.CategoryInUseException;
import rw.ac.suggestionbox.service.CategoryService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(CategoryController.class)
class CategoryControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private CategoryService categoryService;

    @Test
    void listRendersPage() throws Exception {
        when(categoryService.findAll()).thenReturn(List.of());

        mvc.perform(get("/categories"))
                .andExpect(status().isOk())
                .andExpect(view().name("categories/index"))
                .andExpect(model().attributeExists("form", "categories"));
    }

    @Test
    void createRedirectsWithFlashOnSuccess() throws Exception {
        mvc.perform(post("/categories").param("name", "Sports").param("active", "true"))
                .andExpect(redirectedUrl("/categories"))
                .andExpect(flash().attribute("success", "Category created."));

        verify(categoryService).create(any());
    }

    @Test
    void createRedisplaysFormOnBeanValidationError() throws Exception {
        mvc.perform(post("/categories").param("name", "ab"))
                .andExpect(status().isOk())
                .andExpect(view().name("categories/index"))
                .andExpect(model().attributeHasFieldErrorCode("form", "name", "Size"));

        verify(categoryService, never()).create(any());
    }

    @Test
    void createRejectsDuplicateName() throws Exception {
        when(categoryService.nameTaken("Cafeteria", null)).thenReturn(true);

        mvc.perform(post("/categories").param("name", "Cafeteria"))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrorCode("form", "name", "UniqueCategoryName"));
    }

    @Test
    void updateExcludesOwnIdFromUniquenessCheck() throws Exception {
        mvc.perform(post("/categories/7").param("name", "Cafeteria"))
                .andExpect(redirectedUrl("/categories"));

        verify(categoryService).nameTaken("Cafeteria", 7L);
        verify(categoryService).update(eq(7L), any());
    }

    @Test
    void deleteInUseShowsError() throws Exception {
        doThrow(new CategoryInUseException("Cafeteria", 2)).when(categoryService).delete(3L);

        mvc.perform(post("/categories/3/delete"))
                .andExpect(redirectedUrl("/categories"))
                .andExpect(flash().attribute("error", "Cannot delete 'Cafeteria': 2 suggestion(s) still use it."));
    }
}
