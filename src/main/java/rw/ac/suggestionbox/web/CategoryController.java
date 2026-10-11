package rw.ac.suggestionbox.web;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import rw.ac.suggestionbox.dto.CategoryForm;
import rw.ac.suggestionbox.service.CategoryInUseException;
import rw.ac.suggestionbox.service.CategoryService;

/**
 * CRUD for categories. Form and list share one page; every successful POST
 * redirects back (Post/Redirect/Get) with a flash message.
 *
 * <p>On {@code POST /categories/{id}} the {id} path variable binds to {@code form.id},
 * so {@code @UniqueCategoryName} knows which category is being edited.
 */
@Controller
@RequestMapping("/categories")
public class CategoryController {

    private static final String VIEW = "categories/index";
    private static final String REDIRECT = "redirect:/categories";

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public String list(Model model) {
        return render(model, new CategoryForm());
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable Long id, Model model) {
        return render(model, CategoryForm.from(categoryService.get(id)));
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("form") CategoryForm form, BindingResult result,
                         Model model, RedirectAttributes redirect) {
        if (result.hasErrors()) {
            return render(model, form);
        }
        categoryService.create(form);
        redirect.addFlashAttribute("success", "Category created.");
        return REDIRECT;
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("form") CategoryForm form, BindingResult result,
                         Model model, RedirectAttributes redirect) {
        if (result.hasErrors()) {
            return render(model, form);
        }
        categoryService.update(id, form);
        redirect.addFlashAttribute("success", "Category updated.");
        return REDIRECT;
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirect) {
        try {
            categoryService.delete(id);
            redirect.addFlashAttribute("success", "Category deleted.");
        } catch (CategoryInUseException ex) {
            redirect.addFlashAttribute("error", ex.getMessage());
        }
        return REDIRECT;
    }

    private String render(Model model, CategoryForm form) {
        model.addAttribute("form", form);
        model.addAttribute("categories", categoryService.findAll());
        return VIEW;
    }
}
