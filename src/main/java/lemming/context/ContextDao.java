package lemming.context;

import lemming.data.EntityManagerListener;
import lemming.data.GenericDao;
import lemming.lemma.Lemma;
import lemming.pos.Pos;
import lemming.sense.Sense;
import org.hibernate.StaleObjectStateException;
import org.hibernate.UnresolvableObjectException;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;
import java.util.List;

/**
 * Represents a Data Access Object providing data operations for contexts.
 */
public class ContextDao extends GenericDao<Context> implements IContextDao {
    /**
     * Creates an instance of a ContextDao.
     */
    public ContextDao() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Boolean isTransient(Context context) {
        return context.getId() == null;
    }

    /**
     * Refreshes foreign key strings of a context.
     *
     * @param context the refreshed context
     */
    private void refreshForeignKeyStrings(Context context) {
        if (context.getLemma() != null) {
            context.setLemmaString(context.getLemma().getName());
        }

        if (context.getPos() != null) {
            context.setPosString(context.getPos().getName());
        }
    }

    /**
     * {@inheritDoc}
     *
     * @throws RuntimeException
     */
    public Context refresh(Context context) throws RuntimeException {
        EntityManager entityManager = EntityManagerListener.createEntityManager();
        EntityTransaction transaction = null;

        try {
            transaction = entityManager.getTransaction();
            transaction.begin();
            context = entityManager.merge(context);
            TypedQuery<Context> query = entityManager.createQuery("SELECT c FROM Context c LEFT JOIN FETCH c.lemma " +
                    "LEFT JOIN FETCH c.pos LEFT JOIN FETCH c.sense WHERE c.id = :id", Context.class);
            Context refreshedContext = query.setParameter("id", context.getId()).getSingleResult();
            transaction.commit();
            return refreshedContext;
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
    public void persist(Context context) throws RuntimeException {
        EntityManager entityManager = EntityManagerListener.createEntityManager();
        EntityTransaction transaction = null;

        try {
            transaction = entityManager.getTransaction();
            transaction.begin();
            refreshForeignKeyStrings(context);
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
    public void batchPersist(List<Context> contexts) throws RuntimeException {
        EntityManager entityManager = EntityManagerListener.createEntityManager();
        EntityTransaction transaction = null;
        Context currentContext = null;
        Integer batchSize = 50;
        Integer counter = 0;

        try {
            transaction = entityManager.getTransaction();
            transaction.begin();

            for (Context context : contexts) {
                currentContext = context;
                refreshForeignKeyStrings(context);
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
    public Context merge(Context context) throws RuntimeException {
        EntityManager entityManager = EntityManagerListener.createEntityManager();
        EntityTransaction transaction = null;

        try {
            transaction = entityManager.getTransaction();
            transaction.begin();
            Context mergedContext = entityManager.merge(context);
            refreshForeignKeyStrings(mergedContext);
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
    public List<Context> findByKeyword(String keyword) throws RuntimeException {
        EntityManager entityManager = EntityManagerListener.createEntityManager();
        EntityTransaction transaction = null;

        try {
            transaction = entityManager.getTransaction();
            transaction.begin();
            TypedQuery<Context> query = entityManager.createQuery("SELECT c FROM Context c LEFT JOIN FETCH c.lemma " +
                    "LEFT JOIN FETCH c.pos LEFT JOIN FETCH c.sense WHERE c.keyword = :keyword", Context.class);
            List<Context> contextList = query.setParameter("keyword", keyword).getResultList();
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
    public List<Context> findByKeywordStart(String substring) throws RuntimeException {
        EntityManager entityManager = EntityManagerListener.createEntityManager();
        EntityTransaction transaction = null;

        try {
            transaction = entityManager.getTransaction();
            transaction.begin();
            TypedQuery<Context> query = entityManager.createQuery("SELECT c FROM Context c LEFT JOIN FETCH c.lemma " +
                    "LEFT JOIN FETCH c.pos LEFT JOIN FETCH c.sense WHERE c.keyword LIKE :substring", Context.class);
            List<Context> contextList = query.setParameter("substring", substring + "%").getResultList();
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
    public List<Context> findByLocation(String location) {
        EntityManager entityManager = EntityManagerListener.createEntityManager();
        EntityTransaction transaction = null;

        try {
            transaction = entityManager.getTransaction();
            transaction.begin();
            TypedQuery<Context> query = entityManager.createQuery("SELECT c FROM Context c LEFT JOIN FETCH c.lemma " +
                    "LEFT JOIN FETCH c.pos LEFT JOIN FETCH c.sense WHERE c.location = :location", Context.class);
            List<Context> contextList = query.setParameter("location", location).getResultList();
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
    public List<Context> findByLocationStart(String substring) {
        EntityManager entityManager = EntityManagerListener.createEntityManager();
        EntityTransaction transaction = null;

        try {
            transaction = entityManager.getTransaction();
            transaction.begin();
            TypedQuery<Context> query = entityManager.createQuery("SELECT c FROM Context c LEFT JOIN FETCH c.lemma " +
                    "LEFT JOIN FETCH c.pos LEFT JOIN FETCH c.sense WHERE c.location LIKE :substring", Context.class);
            List<Context> contextList = query.setParameter("substring", substring + "%").getResultList();
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
    public List<Context> findByLemma(Lemma lemma) throws RuntimeException {
        EntityManager entityManager = EntityManagerListener.createEntityManager();
        EntityTransaction transaction = null;

        try {
            transaction = entityManager.getTransaction();
            transaction.begin();
            TypedQuery<Context> query = entityManager.createQuery("SELECT c FROM Context c LEFT JOIN FETCH c.lemma " +
                    "LEFT JOIN FETCH c.pos LEFT JOIN FETCH c.sense WHERE c.lemma = :lemma", Context.class);
            List<Context> contextList = query.setParameter("lemma", lemma).getResultList();
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
    public List<Context> findByPos(Pos pos) {
        EntityManager entityManager = EntityManagerListener.createEntityManager();
        EntityTransaction transaction = null;

        try {
            transaction = entityManager.getTransaction();
            transaction.begin();
            TypedQuery<Context> query = entityManager
                    .createQuery("SELECT c FROM Context c LEFT JOIN FETCH c.lemma LEFT JOIN FETCH c.pos " +
                            "LEFT JOIN FETCH c.sense WHERE c.pos = :pos ORDER BY c.keyword", Context.class);
            List<Context> contextList = query.setParameter("pos", pos).getResultList();
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
    public List<Context> findBySense(Sense sense) {
        EntityManager entityManager = EntityManagerListener.createEntityManager();
        EntityTransaction transaction = null;

        try {
            transaction = entityManager.getTransaction();
            transaction.begin();
            TypedQuery<Context> query = entityManager
                    .createQuery("SELECT c FROM Context c LEFT JOIN FETCH c.lemma LEFT JOIN FETCH c.pos " +
                            "LEFT JOIN FETCH c.sense WHERE c.sense = :sense ORDER BY c.keyword", Context.class);
            List<Context> contextList = query.setParameter("sense", sense).getResultList();
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
}
