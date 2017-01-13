package lemming.api.data;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class EntityManagerListener implements ServletContextListener {

    private static EntityManagerFactory entityManagerFactory;

    public void contextInitialized(ServletContextEvent event) {
        entityManagerFactory = Persistence.createEntityManagerFactory("persistence-unit");
    }

    public void contextDestroyed(ServletContextEvent event) {
        entityManagerFactory.close();
    }

    public static EntityManager createEntityManager() {
        if (entityManagerFactory == null) {
            throw new IllegalStateException("Context is not yet initialized.");
        }

        return entityManagerFactory.createEntityManager();
    }
}
