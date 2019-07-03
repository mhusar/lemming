package lemming.context.inbound;

import lemming.context.Context;
import lemming.data.EntityManagerListener;
import lemming.data.GenericDao;
import org.hibernate.StaleObjectStateException;
import org.hibernate.UnresolvableObjectException;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a Data Access Object providing data operations for inbound contexts.
 */
@SuppressWarnings("unused")
public class InboundContextDao extends GenericDao<InboundContext> implements IInboundContextDao {
    /**
     * {@inheritDoc}
     */
    @Override
    public Boolean isTransient(InboundContext context) {
        return context.getId() == null;
    }

    /**
     * {@inheritDoc}
     *
     * @throws RuntimeException
     */
    public void persist(InboundContext context) throws RuntimeException {
        EntityManager entityManager = EntityManagerListener.createEntityManager();
        EntityTransaction transaction = null;

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
    public InboundContext refresh(InboundContext context) throws RuntimeException {
        EntityManager entityManager = EntityManagerListener.createEntityManager();
        EntityTransaction transaction = null;

        try {
            transaction = entityManager.getTransaction();
            transaction.begin();

            if (isTransient(context)) {
                throw new IllegalArgumentException();
            }

            TypedQuery<InboundContext> query = entityManager.createQuery("SELECT i FROM InboundContext i " +
                    "LEFT JOIN FETCH i.match WHERE i.id = :id", InboundContext.class);
            InboundContext refreshedContext = query.setParameter("id", context.getId()).getSingleResult();
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
     * Finds the ancestor of an inbound context with the same package and location.
     *
     * @param entityManager entity manager
     * @param context an inbound context
     * @return An inbound context or null.
     */
    private InboundContext findAncestor(EntityManager entityManager, InboundContext context) {
        TypedQuery<InboundContext> query = entityManager.createQuery("SELECT i FROM InboundContext i " +
                "LEFT JOIN FETCH i.match WHERE i._package = :package AND i.location = :location " +
                "AND i.number < :number ORDER BY i.number DESC", InboundContext.class);
        List<InboundContext> ancestors = query.setParameter("package", context.getPackage())
                .setParameter("location", context.getLocation()).setParameter("number", context.getNumber())
                .setMaxResults(1).getResultList();
        return ancestors.isEmpty() ? null : ancestors.get(0);
    }

    /**
     * Finds the successor of an inbound context with the same package and location.
     *
     * @param entityManager entity manager
     * @param context an inbound context
     * @return An inbound context or null.
     */
    private InboundContext findSuccessor(EntityManager entityManager, InboundContext context) {
        TypedQuery<InboundContext> query = entityManager.createQuery("SELECT i FROM InboundContext i " +
                "LEFT JOIN FETCH i.match WHERE i._package = :package AND i.location = :location " +
                "AND i.number > :number ORDER BY i.number ASC", InboundContext.class);
        List<InboundContext> successors = query.setParameter("package", context.getPackage())
                .setParameter("location", context.getLocation()).setParameter("number", context.getNumber())
                .setMaxResults(1).getResultList();
        return successors.isEmpty() ? null : successors.get(0);
    }

    /**
     * Finds a matching context for an inbound context.
     *
     * @param context an inbound context
     * @return A matching context or null.
     */
    private Context findMatch(InboundContext context) {
        if (context == null) {
            return null;
        } else {
            return context.getMatch();
        }
    }

    /**
     * Finds contexts before a successor.
     *
     * @param entityManager entity manager
     * @param successor successor of contexts
     * @return A list of contexts.
     */
    public List<Context> findBefore(EntityManager entityManager, Context successor) {
        if (successor == null) {
            throw new IllegalStateException();
        }

        TypedQuery<Context> query = entityManager.createQuery("SELECT i FROM Context i " +
                "WHERE i.location = :location AND i.number < :number ORDER BY i.number ASC", Context.class);
        List<Context> contexts = query.setParameter("location", successor.getLocation())
                .setParameter("number", successor.getNumber()).getResultList();
        return contexts;
    }

    /**
     * Finds contexts after an ancestor.
     *
     * @param entityManager entity manager
     * @param ancestor ancestor of contexts
     * @return A list of contexts.
     */
    public List<Context> findAfter(EntityManager entityManager, Context ancestor) {
        if (ancestor == null) {
            throw new IllegalStateException();
        }

        TypedQuery<Context> query = entityManager.createQuery("SELECT i FROM Context i " +
                "WHERE i.location = :location AND i.number > :number ORDER BY i.number ASC", Context.class);
        List<Context> contexts = query.setParameter("location", ancestor.getLocation())
                .setParameter("number", ancestor.getNumber()).getResultList();
        return contexts;
    }

    /**
     * Finds contexts between an ancestor and a successor.
     *
     * @param entityManager entity manager
     * @param ancestor ancestor of contexts
     * @param successor successor of contexts
     * @return A list of contexts.
     */
    private List<Context> findBetween(EntityManager entityManager, Context ancestor, Context successor) {
        if (ancestor == null && successor == null) {
            throw new IllegalArgumentException();
        } else if (ancestor == null) {
            return findBefore(entityManager, successor);
        } else if (successor == null) {
            return findAfter(entityManager, ancestor);
        } else {
            if (!ancestor.getLocation().equals(successor.getLocation())) {
                throw new IllegalArgumentException();
            }

            if (ancestor.getNumber() >= successor.getNumber()) {
                throw new IllegalArgumentException();
            }
        }

        TypedQuery<Context> query = entityManager.createQuery("SELECT i FROM Context i " +
                "WHERE i.location = :location AND i.number > :number1 AND i.number < :number2 " +
                "ORDER BY i.number ASC", Context.class);
        List<Context> contexts = query.setParameter("location", ancestor.getLocation())
                .setParameter("number1", ancestor.getNumber()).setParameter("number2", successor.getNumber())
                .getResultList();
        return contexts;
    }

    /**
     * {@inheritDoc}
     *
     * @throws RuntimeException
     */
    @Override
    public List<Context> findPossibleComplements(List<InboundContext> unmatchedContexts) {
        InboundContext firstUnmatchedContext = unmatchedContexts.get(0);
        InboundContext lastUnmatchedContext = unmatchedContexts.get(unmatchedContexts.size() - 1);
        EntityManager entityManager = EntityManagerListener.createEntityManager();
        EntityTransaction transaction = null;

        try {
            transaction = entityManager.getTransaction();
            transaction.begin();
            InboundContext ancestor = findAncestor(entityManager, firstUnmatchedContext);
            InboundContext successor = findSuccessor(entityManager, lastUnmatchedContext);
            Context ancestorMatch = findMatch(ancestor);
            Context successorMatch = findMatch(successor);
            List<Context> complements = new ArrayList<>();

            if (ancestorMatch != null || successorMatch != null) {
                complements = findBetween(entityManager, ancestorMatch, successorMatch);
            }

            transaction.commit();
            return complements;
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
