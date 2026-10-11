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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import rw.ac.suggestionbox.domain.SuggestionStatus;
import rw.ac.suggestionbox.dto.SuggestionForm;
import rw.ac.suggestionbox.service.CategoryService;
import rw.ac.suggestionbox.service.SuggestionService;

/** CRUD for suggestions, plus filtering the list by status ({@code ?status=ACCEPTED}). */
@Controller
@RequestMapping("/suggestions")
public class SuggestionController {

    private static final String VIEW = "suggestions/index";
    private static final String REDIRECT = "redirect:/suggestions";

    private final SuggestionService suggestionService;
    private final CategoryService categoryService;

    public SuggestionController(SuggestionService suggestionService, CategoryService categoryService) {
        this.suggestionService = suggestionService;
        this.categoryService = categoryService;
    }

    @ModelAttribute("statuses")
    public SuggestionStatus[] statuses() {
        return SuggestionStatus.values();
    }

    @GetMapping
    public String list(@RequestParam(required = false) SuggestionStatus status, Model model) {
        return render(model, new SuggestionForm(), status);
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable Long id, Model model) {
        return render(model, SuggestionForm.from(suggestionService.get(id)), null);
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("form") SuggestionForm form, BindingResult result,
                         Model model, RedirectAttributes redirect) {
        if (result.hasErrors()) {
            return render(model, form, null);
        }
        suggestionService.create(form);
        redirect.addFlashAttribute("success", "Suggestion submitted.");
        return REDIRECT;
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("form") SuggestionForm form, BindingResult result,
                         Model model, RedirectAttributes redirect) {
        if (result.hasErrors()) {
            return render(model, form, null);
        }
        suggestionService.update(id, form);
        redirect.addFlashAttribute("success", "Suggestion updated.");
        return REDIRECT;
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirect) {
        suggestionService.delete(id);
        redirect.addFlashAttribute("success", "Suggestion deleted.");
        return REDIRECT;
    }

    private String render(Model model, SuggestionForm form, SuggestionStatus filter) {
        model.addAttribute("form", form);
        model.addAttribute("filter", filter);
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("suggestions", suggestionService.findAll(filter));
        return VIEW;
    }
}
