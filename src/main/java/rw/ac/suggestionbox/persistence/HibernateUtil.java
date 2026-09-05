package rw.ac.suggestionbox.persistence;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/**
 * Builds the single native Hibernate SessionFactory from hibernate.cfg.xml.
 * Connection settings can be overridden by environment variables so the same
 * WAR runs locally and inside the Docker Compose stack.
 */
public final class HibernateUtil {

    private static final SessionFactory SESSION_FACTORY = build();

    private HibernateUtil() {
    }

    private static SessionFactory build() {
        try {
            Configuration cfg = new Configuration().configure("hibernate.cfg.xml");
            override(cfg, "hibernate.connection.url", System.getenv("DB_URL"));
            override(cfg, "hibernate.connection.username", System.getenv("DB_USER"));
            override(cfg, "hibernate.connection.password", System.getenv("DB_PASSWORD"));
            return cfg.buildSessionFactory();
        } catch (RuntimeException ex) {
            throw new ExceptionInInitializerError(ex);
        }
    }

    private static void override(Configuration cfg, String key, String value) {
        if (value != null && !value.isBlank()) {
            cfg.setProperty(key, value);
        }
    }

    public static SessionFactory getSessionFactory() {
        return SESSION_FACTORY;
    }

    public static void shutdown() {
        SESSION_FACTORY.close();
    }
}
