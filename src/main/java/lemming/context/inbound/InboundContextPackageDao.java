package lemming.context.inbound;

import lemming.context.Context;
import lemming.data.EntityManagerListener;
import lemming.data.GenericDao;
import org.hibernate.StaleObjectStateException;
import org.hibernate.UnresolvableObjectException;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;
import java.util.Iterator;
import java.util.List;

/**
 * Represents a Data Access Object providing data operations for inbound context packages.
 */
public class InboundContextPackageDao extends GenericDao<InboundContextPackage> implements IInboundContextPackageDao {
    /**
     * Creates an instance of an InboundContextPackageDao.
     */
    public InboundContextPackageDao() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Boolean isTransient(InboundContextPackage contextPackage) {
        return contextPackage.getId() == null;
    }

    /**
     * {@inheritDoc}
     *
     * @throws RuntimeException
     */
    @Override
    public void persist(InboundContextPackage contextPackage) throws RuntimeException {
        EntityManager entityManager = EntityManagerListener.createEntityManager();
        EntityTransaction transaction = null;

        try {
            transaction = entityManager.getTransaction();
            transaction.begin();
            entityManager.persist(contextPackage);
            transaction.commit();
        } catch (RuntimeException e) {
            e.printStackTrace();

            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }

            if (e instanceof StaleObjectStateException) {
                panicOnSaveLockingError(contextPackage, e);
            } else if (e instanceof UnresolvableObjectException) {
                panicOnSaveUnresolvableObjectError(contextPackage, e);
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
    @Override
    public List<InboundContextPackage> getAll() {
        EntityManager entityManager = EntityManagerListener.createEntityManager();
        EntityTransaction transaction = null;

        try {
            transaction = entityManager.getTransaction();
            transaction.begin();
            TypedQuery<InboundContextPackage> query = entityManager.createQuery("SELECT i FROM InboundContextPackage i " +
                    "ORDER BY i.created, i.user.realName", InboundContextPackage.class);
            List<InboundContextPackage> contextPackageList = query.getResultList();
            transaction.commit();
            return contextPackageList;
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
    public List<InboundContext> getContexts(InboundContextPackage contextPackage) {
        EntityManager entityManager = EntityManagerListener.createEntityManager();
        EntityTransaction transaction = null;

        try {
            transaction = entityManager.getTransaction();
            transaction.begin();
            TypedQuery<InboundContext> query = entityManager.createQuery("SELECT i FROM InboundContext i " +
                    "WHERE i._package = :package", InboundContext.class);
            List<InboundContext> contextList = query.setParameter("package", contextPackage).getResultList();
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
    public String getBeginLocation(InboundContextPackage contextPackage) {
        EntityManager entityManager = EntityManagerListener.createEntityManager();
        EntityTransaction transaction = null;

        try {
            transaction = entityManager.getTransaction();
            transaction.begin();
            TypedQuery<String> query = entityManager.createQuery("SELECT i.location FROM InboundContext i " +
                    "WHERE i._package = :package ORDER BY i.location ASC", String.class);
            String beginLocation = query.setParameter("package", contextPackage).setMaxResults(1).getSingleResult();
            transaction.commit();
            return beginLocation;
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
    public String getEndLocation(InboundContextPackage contextPackage) {
        EntityManager entityManager = EntityManagerListener.createEntityManager();
        EntityTransaction transaction = null;

        try {
            transaction = entityManager.getTransaction();
            transaction.begin();
            TypedQuery<String> query = entityManager.createQuery("SELECT i.location FROM InboundContext i " +
                    "WHERE i._package = :package ORDER BY i.location DESC", String.class);
            String beginLocation = query.setParameter("package", contextPackage).setMaxResults(1).getSingleResult();
            transaction.commit();
            return beginLocation;
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
    public void matchContexts(InboundContextPackage contextPackage) {
        EntityManager entityManager = EntityManagerListener.createEntityManager();
        EntityTransaction transaction = null;
        Context context = null;
        Integer batchSize = 50;
        Integer counter = 0;

        try {
            transaction = entityManager.getTransaction();
            transaction.begin();
            TypedQuery<Context> selectQuery = entityManager.createQuery("SELECT c FROM Context c " +
                    "INNER JOIN InboundContext i ON c.hash = i.hash WHERE i._package = :package", Context.class);
            List<Context> contexts = selectQuery.setParameter("package", contextPackage).getResultList();

            for (Iterator<Context> iterator = contexts.iterator(); iterator.hasNext(); context = iterator.next()) {
                javax.persistence.Query updateQuery = entityManager.createQuery("UPDATE InboundContext " +
                        "SET match_id = :id WHERE hash = :hash");

                if (context != null) {
                    updateQuery.setParameter("id", context.getId()).setParameter("hash", context.getHash())
                            .executeUpdate();
                }

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
}
