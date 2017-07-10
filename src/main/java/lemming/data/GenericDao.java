package lemming.data;

import lemming.ui.page.LockingErrorPage;
import lemming.ui.page.UnresolvableObjectErrorPage;
import org.apache.wicket.Page;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.request.cycle.RequestCycle;
import org.hibernate.StaleObjectStateException;
import org.hibernate.UnresolvableObjectException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;
import java.lang.reflect.ParameterizedType;
import java.util.List;

/**
 * Implements methods from interface IDao.
 *
 * @param <E>
 *            entity type
 * @see IDao
 */
public abstract class GenericDao<E> implements IDao<E> {
    /**
     * A logger named corresponding to this class.
     */
    private static final Logger logger = LoggerFactory.getLogger(GenericDao.class);

    /**
     * The class of the entity.
     */
    private final Class<E> entityClass;

    /**
     * Initializes a GenericDao as IDao implementation.
     */
    @SuppressWarnings("unchecked")
    protected GenericDao() {
        ParameterizedType genericSuperclass = (ParameterizedType) getClass()
                .getGenericSuperclass();
        entityClass = (Class<E>) genericSuperclass.getActualTypeArguments()[0];
    }

    /**
     * {@inheritDoc}
     *
     * @throws RuntimeException
     */
    public E merge(E entity) throws RuntimeException {
        EntityManager entityManager = EntityManagerListener.createEntityManager();
        EntityTransaction transaction = null;

        try {
            transaction = entityManager.getTransaction();
            transaction.begin();
            E mergedEntity = entityManager.merge(entity);
            transaction.commit();
            return mergedEntity;
        } catch (RuntimeException e) {
            e.printStackTrace();

            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }

            if (e instanceof StaleObjectStateException) {
                panicOnSaveLockingError(entity, e);
            } else if (e instanceof UnresolvableObjectException) {
                panicOnSaveUnresolvableObjectError(entity, e);
            } else {
                throw e;
            }

            return null;
        } finally {
            entityManager.close();
        }
    }

    /**
     * {@inheritDoc}
     *
     * @throws RuntimeException
     */
    public void remove(E entity) throws RuntimeException {
        EntityManager entityManager = EntityManagerListener.createEntityManager();
        EntityTransaction transaction = null;

        try {
            transaction = entityManager.getTransaction();
            transaction.begin();
            entityManager.remove(entityManager.merge(entity));
            transaction.commit();
        } catch (RuntimeException e) {
            e.printStackTrace();

            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }

            throw e;
        } finally {
            entityManager.close();
        }
    }

    /**
     * {@inheritDoc}
     *
     * @throws RuntimeException
     */
    public void removeByPrimaryKey(Object primaryKey) {
        EntityManager entityManager = EntityManagerListener.createEntityManager();
        EntityTransaction transaction = null;

        try {
            transaction = entityManager.getTransaction();
            transaction.begin();
            E entity = entityManager.find(entityClass, primaryKey);
            entityManager.remove(entity);
            transaction.commit();
        } catch (RuntimeException e) {
            e.printStackTrace();

            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }

            throw e;
        } finally {
            entityManager.close();
        }
    }

    /**
     * {@inheritDoc}
     *
     * @throws RuntimeException
     */
    public E refresh(E entity) throws RuntimeException {
        EntityManager entityManager = EntityManagerListener.createEntityManager();
        EntityTransaction transaction = null;

        try {
            transaction = entityManager.getTransaction();
            transaction.begin();
            E mergedEntity = entityManager.merge(entity);
            entityManager.refresh(mergedEntity);
            transaction.commit();
            return mergedEntity;
        } catch (RuntimeException e) {
            e.printStackTrace();

            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }

            throw e;
        } finally {
            entityManager.close();
        }
    }

    /**
     * {@inheritDoc}
     *
     * @throws RuntimeException
     */
    public E find(Object primaryKey) throws RuntimeException {
        EntityManager entityManager = EntityManagerListener.createEntityManager();
        EntityTransaction transaction = null;

        try {
            transaction = entityManager.getTransaction();
            transaction.begin();
            E entity = entityManager.find(entityClass, primaryKey);
            transaction.commit();
            return entity;
        } catch (RuntimeException e) {
            e.printStackTrace();

            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }

            throw e;
        } finally {
            entityManager.close();
        }
    }

    /**
     * {@inheritDoc}
     *
     * @throws RuntimeException
     */
    public List<E> getAll() {
        EntityManager entityManager = EntityManagerListener.createEntityManager();
        EntityTransaction transaction = null;

        try {
            transaction = entityManager.getTransaction();
            transaction.begin();
            TypedQuery<E> query = entityManager.createQuery("FROM " + entityClass.getSimpleName(), entityClass);
            List<E> entityList = query.getResultList();
            transaction.commit();
            return entityList;
        } catch (RuntimeException e) {
            e.printStackTrace();

            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }

            throw e;
        } finally {
            entityManager.close();
        }
    }

    /**
     * {@inheritDoc}
     *
     * @throws RestartResponseException
     */
    public void panicOnSaveLockingError(Object element, RuntimeException exception) {
        logger.error("A locking error occured. Redirect user to error page.");

        if (RequestCycle.get() != null) {
            Page lockingErrorPage = new LockingErrorPage(LockingErrorPage.ActionType.SAVE, element, exception);
            throw new RestartResponseException(lockingErrorPage);
        } else {
            throw exception;
        }
    }

    /**
     * {@inheritDoc}
     *
     * @throws RestartResponseException
     */
    public void panicOnRemoveLockingError(Object element, RuntimeException exception) {
        logger.error("A locking error occured. Redirect user to failure page.");

        if (RequestCycle.get() != null) {
            Page lockingErrorPage = new LockingErrorPage(LockingErrorPage.ActionType.REMOVE, element, exception);
            throw new RestartResponseException(lockingErrorPage);
        } else {
            throw exception;
        }
    }

    /**
     * {@inheritDoc}
     *
     * @throws RestartResponseException
     */
    public void panicOnSaveUnresolvableObjectError(Object element, RuntimeException exception) {
        logger.error("An object was not resolvable. Redirect user to error page.");

        if (RequestCycle.get() != null) {
            Page unresolvableObjectErrorPage = new UnresolvableObjectErrorPage(
                    UnresolvableObjectErrorPage.ActionType.REMOVE, element, exception);
            throw new RestartResponseException(unresolvableObjectErrorPage);
        } else {
            throw exception;
        }
    }

    /**
     * {@inheritDoc}
     *
     * @throws RestartResponseException
     */
    public void panicOnRemoveUnresolvableObjectError(Object element, RuntimeException exception) {
        logger.error("An object was not resolvable. Redirect user to error page.");

        if (RequestCycle.get() != null) {
            Page unresolvableObjectErrorPage = new UnresolvableObjectErrorPage(
                    UnresolvableObjectErrorPage.ActionType.REMOVE, element, exception);
            throw new RestartResponseException(unresolvableObjectErrorPage);
        } else {
            throw exception;
        }
    }
}
