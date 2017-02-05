package lemming.api.data;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/**
 * Listener for receiving notification events about ServletContext lifecycle changes.
 */
@WebListener
public class EntityManagerListener implements ServletContextListener {
    /**
     * The entity manager factory.
     */
    private static EntityManagerFactory entityManagerFactory;

    /**
     * Initializes the entity manager factory.
     *
     * @param servletContextEvent event containing the ServletContext that is being initialized
     */
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        entityManagerFactory = Persistence.createEntityManagerFactory("persistence-unit");
    }

    /**
     * Closes the entity manager factory.
     *
     * @param servletContextEvent event containing the ServletContext that is being destroyed
     */
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        entityManagerFactory.close();
    }

    /**
     * Creates an entity manager.
     *
     * @return An entity manager.
     */
    public static EntityManager createEntityManager() {
        if (entityManagerFactory == null) {
            throw new IllegalStateException("Context is not yet initialized.");
        }

        return entityManagerFactory.createEntityManager();
    }
}
