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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
     * Initializes UUIDs of comments.
     *
     * @param context a context
     */
    private void initializeComments(Context context) {
        if (context.getComments() != null) {
            for (Comment comment : context.getComments()) {
                if (comment.getUuid() == null) {
                    comment.setUuid(UUID.randomUUID().toString());
                }
            }
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
                    "LEFT JOIN FETCH c.pos LEFT JOIN FETCH c.sense LEFT JOIN FETCH c.comments " +
                    "WHERE c.id = :id", Context.class);
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

        if (context.getUuid() == null) {
            context.setUuid(UUID.randomUUID().toString());
        }

        try {
            transaction = entityManager.getTransaction();
            transaction.begin();
            refreshForeignKeyStrings(context);
            initializeComments(context);
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

                if (context.getUuid() == null) {
                    context.setUuid(UUID.randomUUID().toString());
                }

                refreshForeignKeyStrings(context);
                initializeComments(context);
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
            initializeComments(mergedContext);
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
    public void batchMerge(List<Context> contexts) throws RuntimeException {
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
                Context mergedContext = entityManager.merge(currentContext);
                refreshForeignKeyStrings(mergedContext);
                initializeComments(mergedContext);
                entityManager.merge(mergedContext);
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

    /**
     * {@inheritDoc}
     *
     * @throws RuntimeException
     */
    @Override
    public List<Context> addComment(List<Context> contexts, Comment comment) {
        EntityManager entityManager = EntityManagerListener.createEntityManager();
        EntityTransaction transaction = null;
        Context currentContext = null;
        Integer batchSize = 50;
        Integer counter = 0;
        List<Context> changedContexts = new ArrayList<>();

        if (comment.getUuid() == null) {
            comment.setUuid(UUID.randomUUID().toString());
        }

        try {
            transaction = entityManager.getTransaction();
            transaction.begin();
            entityManager.persist(comment);

            for (Context context : contexts) {
                currentContext = context;
                Context mergedContext = entityManager.merge(currentContext);

                mergedContext.getComments().add(comment);
                changedContexts.add(entityManager.merge(mergedContext));
                counter++;

                if (counter % batchSize == 0) {
                    entityManager.flush();
                    entityManager.clear();
                }
            }

            transaction.commit();
            return changedContexts;
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
    @Override
    public Context removeComment(Context context, Comment comment) {
        EntityManager entityManager = EntityManagerListener.createEntityManager();
        EntityTransaction transaction = null;

        try {
            transaction = entityManager.getTransaction();
            transaction.begin();
            Context mergedContext = entityManager.merge(context);
            mergedContext.getComments().remove(comment);
            Context mergedContext2 = entityManager.merge(mergedContext);
            entityManager.flush();

            TypedQuery<Comment> query = entityManager
                    .createQuery("SELECT c FROM Comment c LEFT JOIN FETCH c.contexts WHERE c.id = :id", Comment.class);
            Comment refreshedComment = query.setParameter("id", comment.getId()).getSingleResult();

            if (refreshedComment.getContexts().size() == 0) {
                entityManager.remove(refreshedComment);
            }

            transaction.commit();
            return mergedContext2;
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
}
