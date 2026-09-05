package rw.ac.suggestionbox.web;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import rw.ac.suggestionbox.dao.CategoryDao;
import rw.ac.suggestionbox.model.Category;

import java.io.Serializable;
import java.util.List;

/** CRUD backing bean for the Category entity. */
@Named
@ViewScoped
public class CategoryBean implements Serializable {

    @Inject
    private CategoryDao categoryDao;

    private List<Category> categories;
    private Category selected = new Category();

    @PostConstruct
    public void init() {
        reload();
    }

    private void reload() {
        categories = categoryDao.findAll();
    }

    private void info(String text) {
        FacesContext.getCurrentInstance()
                .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, text, null));
    }

    private void error(String text) {
        FacesContext.getCurrentInstance()
                .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, text, null));
    }

    // ---- CRUD ----

    public void prepareNew() {
        selected = new Category();
    }

    public void edit(Category category) {
        selected = categoryDao.findById(category.getId());
    }

    public String save() {
        if (selected.getId() == null) {
            categoryDao.save(selected);
            info("Category created.");
        } else {
            categoryDao.update(selected);
            info("Category updated.");
        }
        selected = new Category();
        reload();
        return null;
    }

    public String delete(Category category) {
        long used = categoryDao.countSuggestions(category.getId());
        if (used > 0) {
            error("Cannot delete '" + category.getName() + "': " + used + " suggestion(s) still use it.");
        } else {
            categoryDao.delete(category.getId());
            info("Category deleted.");
        }
        reload();
        return null;
    }

    public String cancel() {
        selected = new Category();
        return null;
    }

    // ---- accessors ----

    public List<Category> getCategories() {
        return categories;
    }

    public Category getSelected() {
        return selected;
    }

    public void setSelected(Category selected) {
        this.selected = selected;
    }

    public boolean isEditing() {
        return selected.getId() != null;
    }
}
