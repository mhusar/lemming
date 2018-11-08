package lemming.context.inbound;

import lemming.context.Context;
import lemming.data.EntityManagerListener;
import lemming.data.GenericDao;
import org.hibernate.StaleObjectStateException;
import org.hibernate.UnresolvableObjectException;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
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
    public List<InboundContext> findUnmatchedContexts(InboundContextPackage contextPackage) {
        EntityManager entityManager = EntityManagerListener.createEntityManager();
        EntityTransaction transaction = null;

        try {
            transaction = entityManager.getTransaction();
            transaction.begin();
            TypedQuery<InboundContext> query = entityManager.createQuery("SELECT i FROM InboundContext i " +
                            "WHERE i._package = :package AND i.match IS NULL ORDER BY i.location, i.number",
                    InboundContext.class);
            List<InboundContext> contexts = query.setParameter("package", contextPackage).getResultList();
            transaction.commit();
            return contexts;
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
    public List<String> findUnmatchedContextLocations(InboundContextPackage contextPackage) {
        EntityManager entityManager = EntityManagerListener.createEntityManager();
        EntityTransaction transaction = null;

        try {
            transaction = entityManager.getTransaction();
            transaction.begin();
            TypedQuery<String> query = entityManager.createQuery("SELECT DISTINCT(i.location) " +
                            "FROM InboundContext i WHERE i._package = :package AND i.match IS NULL ORDER BY i.location",
                    String.class);
            List<String> locations = query.setParameter("package", contextPackage).getResultList();
            transaction.commit();
            return locations;
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
     * Private helper method for findUnmatchedContextsByLocation(InboundContextPackage, String) and
     * groupUnmatchedContextsByLocation(InboundContextPackage, String).
     *
     * @param contextPackage a package of inbound contexts
     * @param location       a context location
     * @return List of unmatched inbound contexts.
     * @see #findUnmatchedContextsByLocation(InboundContextPackage, String)
     * @see #groupUnmatchedContextsByLocation(InboundContextPackage, String)
     */
    private List<InboundContext> findUnmatchedContextsByLocation(EntityManager entityManager,
                                                                 InboundContextPackage contextPackage,
                                                                 String location) {
        TypedQuery<InboundContext> query = entityManager.createQuery("SELECT i FROM InboundContext i " +
                "WHERE i.match IS NULL AND i._package = :package AND i.location = :location " +
                "ORDER BY i.number", InboundContext.class);
        List<InboundContext> contexts = query.setParameter("package", contextPackage)
                .setParameter("location", location).getResultList();
        return contexts;
    }

    /**
     * {@inheritDoc}
     *
     * @throws RuntimeException
     */
    @Override
    public List<InboundContext> findUnmatchedContextsByLocation(InboundContextPackage contextPackage,
                                                                String location) {
        EntityManager entityManager = EntityManagerListener.createEntityManager();
        EntityTransaction transaction = null;

        try {
            transaction = entityManager.getTransaction();
            transaction.begin();
            List<InboundContext> contexts = findUnmatchedContextsByLocation(entityManager, contextPackage, location);
            transaction.commit();
            return contexts;
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
     * Finds contexts from first context on.
     *
     * @param entityManager entity manager
     * @param first first context
     * @return A list of contexts.
     */
    public List<InboundContext> findFrom(EntityManager entityManager, InboundContext first) {
        if (first == null) {
            throw new IllegalStateException();
        }

        TypedQuery<InboundContext> query = entityManager.createQuery("SELECT i FROM InboundContext i " +
                "WHERE i.location = :location AND i.number >= :number ORDER BY i.number ASC", InboundContext.class);
        List<InboundContext> contexts = query.setParameter("location", first.getLocation())
                .setParameter("number", first.getNumber()).getResultList();
        return contexts;
    }

    /**
     * Finds contexts until last context.
     *
     * @param entityManager entity manager
     * @param last last context
     * @return A list of contexts.
     */
    public List<InboundContext> findTo(EntityManager entityManager, InboundContext last) {
        if (last == null) {
            throw new IllegalStateException();
        }

        TypedQuery<InboundContext> query = entityManager.createQuery("SELECT i FROM InboundContext i " +
                "WHERE i.location = :location AND i.number <= :number ORDER BY i.number ASC", InboundContext.class);
        List<InboundContext> contexts = query.setParameter("location", last.getLocation())
                .setParameter("number", last.getNumber()).getResultList();
        return contexts;
    }

    /**
     * Finds contexts between the first (inclusive) and the last context (inclusive).
     *
     * @param entityManager entity manager
     * @param first first context
     * @param last last context
     * @return A list of contexts.
     */
    public List<InboundContext> findBetween(EntityManager entityManager, InboundContext first, InboundContext last) {
        if (first == null) {
            return findTo(entityManager, last);
        } else if (last == null) {
            return findFrom(entityManager, first);
        } else {
            if (!first.getLocation().equals(last.getLocation())) {
                throw new IllegalArgumentException();
            }

            if (first.getNumber() > last.getNumber()) {
                throw new IllegalArgumentException();
            }
        }

        TypedQuery<InboundContext> query = entityManager.createQuery("SELECT i FROM InboundContext i " +
                "WHERE i.location = :location AND i.number >= :number1 AND i.number <= :number2 " +
                "ORDER BY i.number ASC", InboundContext.class);
        List<InboundContext> contexts = query.setParameter("location", first.getLocation())
                .setParameter("number1", first.getNumber()).setParameter("number2", last.getNumber())
                .getResultList();
        return contexts;
    }

    /**
     * {@inheritDoc}
     *
     * @throws RuntimeException
     */
    @Override
    public MultivaluedMap<Integer, InboundContext> groupUnmatchedContextsByLocation(
            InboundContextPackage contextPackage, String location) {
        MultivaluedMap<Integer, InboundContext> groupedContexts = new MultivaluedHashMap<>();
        EntityManager entityManager = EntityManagerListener.createEntityManager();
        EntityTransaction transaction = null;

        try {
            transaction = entityManager.getTransaction();
            transaction.begin();
            List<InboundContext> contexts = findUnmatchedContextsByLocation(entityManager, contextPackage, location);
            // Find contexts between the first and the last unmatched context (unmatched and matched).
            // This approach is needed because there are sometimes gaps in the context numbering.
            List<InboundContext> contexts2 = findBetween(entityManager, contexts.get(0),
                    contexts.get(contexts.size() - 1));
            Integer key = 0;

            for (InboundContext context : contexts2) {
                if (context.getMatch() != null) {
                    if (groupedContexts.getFirst(key) != null) {
                        key++;
                    }

                    continue;
                }

                groupedContexts.add(key, context);
            }

            transaction.commit();
            return groupedContexts;
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
     * Removes matches from inbound contexts with multiple possible matches.
     *
     * @param entityManager entityManager entity manager
     * @param contextPackage a package of inbound contexts
     */
    private void removeDuplicateMatches(EntityManager entityManager, InboundContextPackage contextPackage) {
        TypedQuery<String> selectQuery = entityManager.createQuery("SELECT c.hash FROM Context c " +
                "GROUP BY c.hash HAVING COUNT(c.hash) > 1", String.class);
        List<String> duplicateHashes = selectQuery.getResultList();

        for (String hash : duplicateHashes) {
            javax.persistence.Query updateQuery = entityManager.createQuery("UPDATE InboundContext " +
                    "SET match_id = NULL WHERE hash = :hash AND package_id = :package");
            updateQuery.setParameter("hash", hash).setParameter("package", contextPackage).executeUpdate();
        }
    }

    /**
     * Removes matches from inbound contexts with duplicate hashes.
     *
     * @param entityManager entityManager entity manager
     * @param contextPackage a package of inbound contexts
     */
    private void removeMatchesFromDuplicateContexts(EntityManager entityManager, InboundContextPackage contextPackage) {
        TypedQuery<String> selectQuery = entityManager.createQuery("SELECT i.hash FROM InboundContext i " +
                "GROUP BY i.hash HAVING COUNT(i.hash) > 1", String.class);
        List<String> duplicateHashes = selectQuery.getResultList();

        for (String hash : duplicateHashes) {
            javax.persistence.Query updateQuery = entityManager.createQuery("UPDATE InboundContext " +
                    "SET match_id = NULL WHERE hash = :hash AND package_id = :package");
            updateQuery.setParameter("hash", hash).setParameter("package", contextPackage).executeUpdate();
        }
    }

    /**
     * {@inheritDoc}
     *
     * @throws RuntimeException
     */
    @Override
    public void matchContextsByHash(InboundContextPackage contextPackage) {
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
                        "SET match_id = :id WHERE hash = :hash AND package_id = :package");

                if (context != null) {
                    updateQuery.setParameter("id", context.getId()).setParameter("hash", context.getHash())
                            .setParameter("package", contextPackage).executeUpdate();
                    counter++;
                }

                if (counter % batchSize == 0) {
                    entityManager.flush();
                    entityManager.clear();
                }
            }

            removeDuplicateMatches(entityManager, contextPackage);
            removeMatchesFromDuplicateContexts(entityManager, contextPackage);
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
