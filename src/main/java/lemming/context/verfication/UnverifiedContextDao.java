package lemming.context.verfication;

import lemming.context.ContextHashing;
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
 * Represents a Data Access Object providing data operations for unverified contexts.
 */
public class UnverifiedContextDao extends GenericDao<UnverifiedContext> implements IUnverifiedContextDao {
    /**
     * Creates an instance of an UnverifiedContextDao.
     */
    public UnverifiedContextDao() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Boolean isTransient(UnverifiedContext context) {
        return !(context.getId() instanceof Integer);
    }

    /**
     * {@inheritDoc}
     *
     * @throws RuntimeException
     */
    public void persist(UnverifiedContext context) throws RuntimeException {
        EntityManager entityManager = EntityManagerListener.createEntityManager();
        EntityTransaction transaction = null;

        if (!(context.getUuid() instanceof String)) {
            context.setUuid(UUID.randomUUID().toString());
        }

        // set checksum for context
        context.setChecksum(ContextHashing.getSha512(context));

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
    public void batchPersist(List<UnverifiedContext> contexts) throws RuntimeException {
        EntityManager entityManager = EntityManagerListener.createEntityManager();
        EntityTransaction transaction = null;
        UnverifiedContext currentContext = null;
        Integer batchSize = 50;
        Integer counter = 0;

        try {
            transaction = entityManager.getTransaction();
            transaction.begin();

            for (UnverifiedContext context : contexts) {
                currentContext = context;

                if (!(context.getUuid() instanceof String)) {
                    context.setUuid(UUID.randomUUID().toString());
                }

                // set checksum for context
                context.setChecksum(ContextHashing.getSha512(context));
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
    public UnverifiedContext merge(UnverifiedContext context) throws RuntimeException {
        EntityManager entityManager = EntityManagerListener.createEntityManager();
        EntityTransaction transaction = null;

        // set checksum for context
        context.setChecksum(ContextHashing.getSha512(context));

        try {
            transaction = entityManager.getTransaction();
            transaction.begin();
            UnverifiedContext mergedContext = entityManager.merge(context);
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
    public List<UnverifiedContext> findByKeyword(String keyword) throws RuntimeException {
        EntityManager entityManager = EntityManagerListener.createEntityManager();
        EntityTransaction transaction = null;

        try {
            transaction = entityManager.getTransaction();
            transaction.begin();
            TypedQuery<UnverifiedContext> query = entityManager.createQuery("SELECT i FROM UnverifiedContext i " +
                    "WHERE i.keyword = :keyword", UnverifiedContext.class);
            List<UnverifiedContext> contextList = query.setParameter("keyword", keyword).getResultList();
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
    public List<UnverifiedContext> findByKeywordStart(String substring) throws RuntimeException {
        EntityManager entityManager = EntityManagerListener.createEntityManager();
        EntityTransaction transaction = null;

        try {
            transaction = entityManager.getTransaction();
            transaction.begin();
            TypedQuery<UnverifiedContext> query = entityManager.createQuery("SELECT i FROM UnverifiedContext i " +
                    "WHERE i.keyword LIKE :substring", UnverifiedContext.class);
            List<UnverifiedContext> contextList = query.setParameter("substring", substring + "%").getResultList();
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
    public List<UnverifiedContext> findByLocation(String location) {
        EntityManager entityManager = EntityManagerListener.createEntityManager();
        EntityTransaction transaction = null;

        try {
            transaction = entityManager.getTransaction();
            transaction.begin();
            TypedQuery<UnverifiedContext> query = entityManager.createQuery("SELECT i FROM UnverifiedContext i " +
                    "WHERE i.location = :location", UnverifiedContext.class);
            List<UnverifiedContext> contextList = query.setParameter("location", location).getResultList();
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
    public List<UnverifiedContext> findByLocationStart(String substring) {
        EntityManager entityManager = EntityManagerListener.createEntityManager();
        EntityTransaction transaction = null;

        try {
            transaction = entityManager.getTransaction();
            transaction.begin();
            TypedQuery<UnverifiedContext> query = entityManager.createQuery("SELECT i FROM UnverifiedContext i " +
                    "WHERE i.location LIKE :substring", UnverifiedContext.class);
            List<UnverifiedContext> contextList = query.setParameter("substring", substring + "%").getResultList();
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
    public List<UnverifiedContextOverview> getOverviews() {
        List<UnverifiedContextOverview> overviewList = new ArrayList<UnverifiedContextOverview>();
        EntityManager entityManager = EntityManagerListener.createEntityManager();
        EntityTransaction transaction = null;

        try {
            transaction = entityManager.getTransaction();
            transaction.begin();
            TypedQuery<Object[]> query = entityManager.createQuery("SELECT COUNT(i), i.timestamp, i.user " +
                    "FROM UnverifiedContext i GROUP BY i.timestamp, i.user " +
                    "ORDER BY i.timestamp DESC, i.user ASC", Object[].class);
            List<Object[]> resultList = query.getResultList();
            transaction.commit();

            for (Object[] result : resultList) {
                Long numberOfContexts = (Long) result[0];
                Timestamp timestamp = (Timestamp) result[1];
                String userString = (String) result[2];

                TypedQuery<Object[]> locationQuery = entityManager.createQuery("SELECT " +
                        "SUBSTRING(MIN(i.location), 1, 15), SUBSTRING(MAX(i.location), 1, 15) FROM UnverifiedContext i " +
                        "WHERE i.timestamp = :timestamp AND i.user = :user", Object[].class);
                List<Object[]> locationList = locationQuery.setParameter("timestamp", timestamp)
                        .setParameter("user", userString).setMaxResults(1).getResultList();
                String beginLocation = (String) locationList.get(0)[0];
                String endLocation = (String) locationList.get(0)[1];
                UnverifiedContextOverview overview = new UnverifiedContextOverview(numberOfContexts, timestamp, userString,
                        beginLocation, endLocation);

                overviewList.add(overview);
            }

            return overviewList;
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
    public void removeByOverview(UnverifiedContextOverview overview) {
        EntityManager entityManager = EntityManagerListener.createEntityManager();
        EntityTransaction transaction = null;

        try {
            transaction = entityManager.getTransaction();
            transaction.begin();
            entityManager.createQuery("DELETE FROM UnverifiedContext i " +
                    "WHERE i.timestamp = :timestamp AND i.user = :user")
                    .setParameter("timestamp", overview.getTimestamp()).setParameter("user", overview.getUserString())
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
