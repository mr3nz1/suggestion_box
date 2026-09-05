package rw.ac.suggestionbox.web;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import rw.ac.suggestionbox.dao.CategoryDao;
import rw.ac.suggestionbox.dao.SuggestionDao;
import rw.ac.suggestionbox.model.Category;
import rw.ac.suggestionbox.model.Suggestion;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/** CRUD backing bean for the Suggestion entity. */
@Named
@ViewScoped
public class SuggestionBean implements Serializable {

    @Inject
    private SuggestionDao suggestionDao;

    @Inject
    private CategoryDao categoryDao;

    private List<Suggestion> suggestions;
    private List<Category> categories;
    private Suggestion selected = new Suggestion();
    private String statusFilter = "";

    @PostConstruct
    public void init() {
        categories = categoryDao.findAll();
        reload();
    }

    private void reload() {
        suggestions = suggestionDao.findAll(statusFilter);
    }

    private void info(String text) {
        FacesContext.getCurrentInstance()
                .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, text, null));
    }

    // ---- CRUD ----

    public void prepareNew() {
        selected = new Suggestion();
    }

    public void edit(Suggestion suggestion) {
        selected = suggestionDao.findById(suggestion.getId());
    }

    public String save() {
        if (selected.getId() == null) {
            selected.setCreatedAt(LocalDateTime.now());
            suggestionDao.save(selected);
            info("Suggestion submitted.");
        } else {
            suggestionDao.update(selected);
            info("Suggestion updated.");
        }
        selected = new Suggestion();
        reload();
        return null;
    }

    public String delete(Suggestion suggestion) {
        suggestionDao.delete(suggestion.getId());
        info("Suggestion deleted.");
        reload();
        return null;
    }

    public String cancel() {
        selected = new Suggestion();
        return null;
    }

    public void applyFilter() {
        reload();
    }

    // ---- accessors ----

    public List<Suggestion> getSuggestions() {
        return suggestions;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public String[] getStatuses() {
        return Suggestion.STATUSES;
    }

    public Suggestion getSelected() {
        return selected;
    }

    public void setSelected(Suggestion selected) {
        this.selected = selected;
    }

    public String getStatusFilter() {
        return statusFilter;
    }

    public void setStatusFilter(String statusFilter) {
        this.statusFilter = statusFilter;
    }

    public boolean isEditing() {
        return selected.getId() != null;
    }
}
