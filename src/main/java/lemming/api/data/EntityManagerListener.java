package lemming.api.data;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.util.logging.Logger;

@WebListener
public class EntityManagerListener implements ServletContextListener {
    /**
     * A logger named corresponding to this class.
     */
    private static final Logger logger = Logger.getLogger(EntityManagerListener.class.getName());

    private static EntityManagerFactory entityManagerFactory;

    public void contextInitialized(ServletContextEvent event) {
        logger.info("context initialized");
        entityManagerFactory = Persistence.createEntityManagerFactory("persistence-unit");
    }

    public void contextDestroyed(ServletContextEvent event) {
        logger.info("context destroyed");
        entityManagerFactory.close();
    }

    public static EntityManager createEntityManager() {
        if (entityManagerFactory == null) {
            throw new IllegalStateException("Context is not yet initialized.");
        }

        return entityManagerFactory.createEntityManager();
    }
}
