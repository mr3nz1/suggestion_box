package rw.ac.suggestionbox.web;

import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.FacesConverter;
import jakarta.inject.Inject;
import rw.ac.suggestionbox.dao.CategoryDao;
import rw.ac.suggestionbox.model.Category;

/** Turns the category drop-down value into a Category instance and back. */
@FacesConverter(value = "categoryConverter", managed = true)
public class CategoryConverter implements Converter<Category> {

    @Inject
    private CategoryDao categoryDao;

    @Override
    public Category getAsObject(FacesContext context, UIComponent component, String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return categoryDao.findById(Long.valueOf(value));
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Category value) {
        return (value == null || value.getId() == null) ? "" : String.valueOf(value.getId());
    }
}
