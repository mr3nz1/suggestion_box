package rw.ac.suggestionbox.dao;

import jakarta.enterprise.context.ApplicationScoped;
import org.hibernate.Session;
import org.hibernate.Transaction;
import rw.ac.suggestionbox.model.Category;
import rw.ac.suggestionbox.persistence.HibernateUtil;

import java.io.Serializable;
import java.util.List;

/** CRUD for Category using the native Hibernate Session API. */
@ApplicationScoped
public class CategoryDao implements Serializable {

    public void save(Category category) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            session.persist(category);
            tx.commit();
        }
    }

    public void update(Category category) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            session.merge(category);
            tx.commit();
        }
    }

    public void delete(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            Category managed = session.get(Category.class, id);
            if (managed != null) {
                session.remove(managed);
            }
            tx.commit();
        }
    }

    public Category findById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Category.class, id);
        }
    }

    public List<Category> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from Category order by name", Category.class).list();
        }
    }

    /** Used by the CDI validator that enforces unique category names. */
    public boolean nameTaken(String name, Long excludedId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Long count = session.createQuery(
                            "select count(c.id) from Category c where lower(c.name) = :n and (:id is null or c.id <> :id)",
                            Long.class)
                    .setParameter("n", name == null ? "" : name.toLowerCase())
                    .setParameter("id", excludedId)
                    .uniqueResult();
            return count != null && count > 0;
        }
    }

    public long countSuggestions(Long categoryId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Long count = session.createQuery(
                            "select count(s.id) from Suggestion s where s.category.id = :id", Long.class)
                    .setParameter("id", categoryId)
                    .uniqueResult();
            return count == null ? 0L : count;
        }
    }
}
