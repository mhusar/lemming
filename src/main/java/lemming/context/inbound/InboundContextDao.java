package lemming.context.inbound;

import lemming.data.EntityManagerListener;
import lemming.data.GenericDao;
import org.hibernate.StaleObjectStateException;
import org.hibernate.UnresolvableObjectException;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Represents a Data Access Object providing data operations for inbound contexts.
 */
public class InboundContextDao extends GenericDao<InboundContext> implements IInboundContextDao {
    /**
     * Creates an instance of an InboundContextDao.
     */
    public InboundContextDao() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Boolean isTransient(InboundContext context) {
        return !(context.getId() instanceof Integer);
    }

    /**
     * {@inheritDoc}
     *
     * @throws RuntimeException
     */
    public void persist(InboundContext context) throws RuntimeException {
        EntityManager entityManager = EntityManagerListener.createEntityManager();
        EntityTransaction transaction = null;

        if (!(context.getUuid() instanceof String)) {
            context.setUuid(UUID.randomUUID().toString());
        }

        try {
            transaction = entityManager.getTransaction();
            transaction.begin();
            entityManager.persist(context);
            transaction.commit();
        } catch (RuntimeException e) {
            e.printStackTrace();

            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }

            if (e instanceof StaleObjectStateException) {
                panicOnSaveLockingError(context, e);
            } else if (e instanceof UnresolvableObjectException) {
                panicOnSaveUnresolvableObjectError(context, e);
            } else {
                throw e;
            }
        } finally {
            entityManager.close();
        }
    }

    /**
     * {@inheritDoc}
     *
     * @throws RuntimeException
     */
    public void batchPersist(List<InboundContext> contexts) throws RuntimeException {
        EntityManager entityManager = EntityManagerListener.createEntityManager();
        EntityTransaction transaction = null;
        InboundContext currentContext = null;
        Integer batchSize = 50;
        Integer counter = 0;

        try {
            transaction = entityManager.getTransaction();
            transaction.begin();

            for (InboundContext context : contexts) {
                currentContext = context;

                if (!(context.getUuid() instanceof String)) {
                    context.setUuid(UUID.randomUUID().toString());
                }

                entityManager.persist(context);
                counter++;

                if (counter % batchSize == 0) {
                    entityManager.flush();
                    entityManager.clear();
                }
            }

            transaction.commit();
        } catch (RuntimeException e) {
            e.printStackTrace();

            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }

            if (e instanceof StaleObjectStateException) {
                panicOnSaveLockingError(currentContext, e);
            } else if (e instanceof UnresolvableObjectException) {
                panicOnSaveUnresolvableObjectError(currentContext, e);
            } else {
                throw e;
            }
        } finally {
            entityManager.close();
        }
    }

    /**
     * {@inheritDoc}
     *
     * @throws RuntimeException
     */
    public InboundContext merge(InboundContext context) throws RuntimeException {
        EntityManager entityManager = EntityManagerListener.createEntityManager();
        EntityTransaction transaction = null;

        try {
            transaction = entityManager.getTransaction();
            transaction.begin();
            InboundContext mergedContext = entityManager.merge(context);
            mergedContext = entityManager.merge(mergedContext);
            transaction.commit();
            return mergedContext;
        } catch (RuntimeException e) {
            e.printStackTrace();

            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }

            if (e instanceof StaleObjectStateException) {
                panicOnSaveLockingError(context, e);
            } else if (e instanceof UnresolvableObjectException) {
                panicOnSaveUnresolvableObjectError(context, e);
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
    public List<InboundContext> findByKeyword(String keyword) throws RuntimeException {
        EntityManager entityManager = EntityManagerListener.createEntityManager();
        EntityTransaction transaction = null;

        try {
            transaction = entityManager.getTransaction();
            transaction.begin();
            TypedQuery<InboundContext> query = entityManager.createQuery("SELECT i FROM InboundContext i " +
                    "WHERE i.keyword = :keyword", InboundContext.class);
            List<InboundContext> contextList = query.setParameter("keyword", keyword).getResultList();
            transaction.commit();
            return contextList;
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
    public List<InboundContext> findByKeywordStart(String substring) throws RuntimeException {
        EntityManager entityManager = EntityManagerListener.createEntityManager();
        EntityTransaction transaction = null;

        try {
            transaction = entityManager.getTransaction();
            transaction.begin();
            TypedQuery<InboundContext> query = entityManager.createQuery("SELECT i FROM InboundContext i " +
                    "WHERE i.keyword LIKE :substring", InboundContext.class);
            List<InboundContext> contextList = query.setParameter("substring", substring + "%").getResultList();
            transaction.commit();
            return contextList;
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
    @Override
    public List<InboundContext> findByLocation(String location) {
        EntityManager entityManager = EntityManagerListener.createEntityManager();
        EntityTransaction transaction = null;

        try {
            transaction = entityManager.getTransaction();
            transaction.begin();
            TypedQuery<InboundContext> query = entityManager.createQuery("SELECT i FROM InboundContext i " +
                    "WHERE i.location = :location", InboundContext.class);
            List<InboundContext> contextList = query.setParameter("location", location).getResultList();
            transaction.commit();
            return contextList;
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
    @Override
    public List<InboundContext> findByLocationStart(String substring) {
        EntityManager entityManager = EntityManagerListener.createEntityManager();
        EntityTransaction transaction = null;

        try {
            transaction = entityManager.getTransaction();
            transaction.begin();
            TypedQuery<InboundContext> query = entityManager.createQuery("SELECT i FROM InboundContext i " +
                    "WHERE i.location LIKE :substring", InboundContext.class);
            List<InboundContext> contextList = query.setParameter("substring", substring + "%").getResultList();
            transaction.commit();
            return contextList;
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
    @Override
    public List<InboundContextSummary> getSummaries() {
        List<InboundContextSummary> summaryList = new ArrayList<InboundContextSummary>();
        EntityManager entityManager = EntityManagerListener.createEntityManager();
        EntityTransaction transaction = null;

        try {
            transaction = entityManager.getTransaction();
            transaction.begin();
            TypedQuery<Object[]> query = entityManager.createQuery("SELECT COUNT(i), i.timestamp, i.user " +
                    "FROM InboundContext i GROUP BY i.timestamp, i.user " +
                    "ORDER BY i.timestamp DESC, i.user ASC", Object[].class);
            List<Object[]> resultList = query.getResultList();
            transaction.commit();

            for (Object[] result : resultList) {
                Long numberOfContexts = (Long) result[0];
                Timestamp timestamp = (Timestamp) result[1];
                String userString = (String) result[2];
                InboundContextSummary summary = new InboundContextSummary(numberOfContexts, timestamp, userString);

                summaryList.add(summary);
            }

            return summaryList;
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
    @Override
    public void removeBySummary(InboundContextSummary summary) {
        EntityManager entityManager = EntityManagerListener.createEntityManager();
        EntityTransaction transaction = null;

        try {
            transaction = entityManager.getTransaction();
            transaction.begin();
            entityManager.createQuery("DELETE FROM InboundContext i " +
                    "WHERE i.timestamp = :timestamp AND i.user = :user")
                    .setParameter("timestamp", summary.getTimestamp()).setParameter("user", summary.getUserString())
                    .executeUpdate();
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
}
