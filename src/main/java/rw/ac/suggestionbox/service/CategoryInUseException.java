package rw.ac.suggestionbox.service;

/** Business rule: a category cannot be deleted while suggestions still reference it. */
public class CategoryInUseException extends RuntimeException {

    public CategoryInUseException(String name, long usages) {
        super("Cannot delete '" + name + "': " + usages + " suggestion(s) still use it.");
    }
}
