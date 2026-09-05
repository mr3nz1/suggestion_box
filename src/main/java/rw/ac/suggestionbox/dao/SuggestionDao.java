package rw.ac.suggestionbox.dao;

import jakarta.enterprise.context.ApplicationScoped;
import org.hibernate.Session;
import org.hibernate.Transaction;
import rw.ac.suggestionbox.model.Suggestion;
import rw.ac.suggestionbox.persistence.HibernateUtil;

import java.io.Serializable;
import java.util.List;

/** CRUD for Suggestion using the native Hibernate Session API. */
@ApplicationScoped
public class SuggestionDao implements Serializable {

    public void save(Suggestion suggestion) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();

            session.persist(suggestion);
            tx.commit();
        }
    }

    public void update(Suggestion suggestion) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();

            session.merge(suggestion);

            tx.commit();
        }
    }

    public void delete(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();

            Suggestion managed = session.get(Suggestion.class, id);
            if (managed != null) {
                session.remove(managed);
            }

            tx.commit();
        }
    }

    public Suggestion findById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Suggestion.class, id);
        }
    }

    public List<Suggestion> findAll(String statusFilter) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "from Suggestion s"
                    + (statusFilter == null || statusFilter.isBlank() ? "" : " where s.status = :st")
                    + " order by s.createdAt desc";

            var query = session.createQuery(hql, Suggestion.class);

            if (statusFilter != null && !statusFilter.isBlank()) {
                query.setParameter("st", statusFilter);
            }
            return query.list();
        }
    }
}
