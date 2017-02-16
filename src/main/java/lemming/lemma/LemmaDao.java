package lemming.lemma;

import lemming.data.EntityManagerListener;
import lemming.data.GenericDao;
import lemming.data.Source;
import org.hibernate.StaleObjectStateException;
import org.hibernate.UnresolvableObjectException;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.UUID;

/**
 * Represents a Data Access Object providing data operations for lemmata.
 */
public class LemmaDao extends GenericDao<Lemma> implements ILemmaDao {
    /**
     * Creates an instance of a LemmaDao.
     */
    public LemmaDao() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Boolean isTransient(Lemma lemma) {
        return !(lemma.getId() instanceof Integer);
    }

    /**
     * {@inheritDoc}
     *
     * @throws RuntimeException
     */
    public Lemma refresh(Lemma lemma) throws RuntimeException {
        EntityManager entityManager = EntityManagerListener.createEntityManager();
        EntityTransaction transaction = null;

        try {
            transaction = entityManager.getTransaction();
            transaction.begin();
            lemma = entityManager.merge(lemma);
            entityManager.refresh(lemma);
            lemma.getSenses().size();
            transaction.commit();
            return lemma;
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
    public void persist(Lemma lemma) throws RuntimeException {
        EntityManager entityManager = EntityManagerListener.createEntityManager();
        EntityTransaction transaction = null;

        if (!(lemma.getUuid() instanceof String)) {
            lemma.setUuid(UUID.randomUUID().toString());
        }

        try {
            transaction = entityManager.getTransaction();
            transaction.begin();
            entityManager.persist(lemma);
            transaction.commit();
        } catch (RuntimeException e) {
            e.printStackTrace();

            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }

            if (e instanceof StaleObjectStateException) {
                panicOnSaveLockingError(lemma, e);
            } else if (e instanceof UnresolvableObjectException) {
                panicOnSaveUnresolvableObjectError(lemma, e);
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
    public void batchPersist(List<Lemma> lemmas) throws RuntimeException {
        EntityManager entityManager = EntityManagerListener.createEntityManager();
        EntityTransaction transaction = null;
        Lemma currentLemma = null;
        Integer batchSize = 50;
        Integer counter = 0;

        try {
            transaction = entityManager.getTransaction();
            transaction.begin();

            for (Lemma lemma : lemmas) {
                currentLemma = lemma;

                if (!(lemma.getUuid() instanceof String)) {
                    lemma.setUuid(UUID.randomUUID().toString());
                }

                entityManager.persist(lemma);
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
                panicOnSaveLockingError(currentLemma, e);
            } else if (e instanceof UnresolvableObjectException) {
                panicOnSaveUnresolvableObjectError(currentLemma, e);
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
    public Lemma findByName(String name) throws RuntimeException {
        EntityManager entityManager = EntityManagerListener.createEntityManager();
        EntityTransaction transaction = null;

        try {
            transaction = entityManager.getTransaction();
            transaction.begin();
            TypedQuery<Lemma> query = entityManager.createQuery("SELECT l FROM Lemma l " +
                    "LEFT JOIN FETCH l.replacement LEFT JOIN FETCH l.pos " +
                    "WHERE l.name = :name ORDER BY l.name", Lemma.class);
            List<Lemma> lemmaList = query.setParameter("name", name).getResultList();
            transaction.commit();

            if (lemmaList.isEmpty()) {
                return null;
            } else {
                return lemmaList.get(0);
            }
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
    public List<Lemma> findByNameStart(String substring) throws RuntimeException {
        return findByNameStart(substring, false);
    }

    /**
     * {@inheritDoc}
     *
     * @throws RuntimeException
     */
    @Override
    public List<Lemma> findByNameStart(String substring, Boolean excludeReplacements) {
        EntityManager entityManager = EntityManagerListener.createEntityManager();
        EntityTransaction transaction = null;
        String queryString;

        if (excludeReplacements) {
            queryString = "SELECT l FROM Lemma l LEFT JOIN FETCH l.pos " +
                    "WHERE l.name LIKE :substring AND l.replacement IS NULL ORDER BY l.name";
        } else {
            queryString = "SELECT l FROM Lemma l LEFT JOIN FETCH l.replacement LEFT JOIN FETCH l.pos " +
                    "WHERE l.name LIKE :substring ORDER BY l.name";
        }

        try {
            transaction = entityManager.getTransaction();
            transaction.begin();
            TypedQuery<Lemma> query = entityManager.createQuery(queryString, Lemma.class);
            List<Lemma> lemmaList = query.setParameter("substring", substring + "%").getResultList();
            transaction.commit();
            return lemmaList;
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
    public List<Lemma> findBySource(Source.LemmaType source) {
        EntityManager entityManager = EntityManagerListener.createEntityManager();
        EntityTransaction transaction = null;

        try {
            transaction = entityManager.getTransaction();
            transaction.begin();
            TypedQuery<Lemma> query = entityManager
                    .createQuery("SELECT l FROM Lemma l LEFT JOIN FETCH l.replacement LEFT JOIN FETCH l.pos " +
                            "WHERE l.source = :source ORDER BY l.name", Lemma.class);
            List<Lemma> lemmaList = query.setParameter("source", source).getResultList();
            transaction.commit();
            return lemmaList;
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
    public List<Lemma> findResolvableLemmata() {
        EntityManager entityManager = EntityManagerListener.createEntityManager();
        EntityTransaction transaction = null;

        try {
            transaction = entityManager.getTransaction();
            transaction.begin();
            TypedQuery<Lemma> query = entityManager
                    .createQuery("FROM Lemma WHERE source = :source AND replacement_string IS NOT NULL", Lemma.class);
            List<Lemma> lemmaList = query.setParameter("source", Source.LemmaType.TL).getResultList();
            transaction.commit();
            return lemmaList;
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
    public Boolean batchResolve(List<Lemma> lemmas) {
        EntityManager entityManager = EntityManagerListener.createEntityManager();
        EntityTransaction transaction = null;
        Boolean returnValue = true;
        Integer batchSize = 50;
        Integer counter = 0;

        try {
            transaction = entityManager.getTransaction();
            transaction.begin();

            for (Lemma lemma : lemmas) {
                TypedQuery<Lemma> query = entityManager
                        .createQuery("FROM Lemma WHERE name = :name", Lemma.class);
                List<Lemma> lemmaList = query.setParameter("name", lemma.getReplacementString()).getResultList();

                if (lemmaList.isEmpty()) {
                    returnValue = false;
                } else {
                    Lemma replacement = lemmaList.get(0);
                    lemma.setReplacement(replacement);
                    entityManager.merge(lemma);
                    counter++;

                    if (counter % batchSize == 0) {
                        entityManager.flush();
                        entityManager.clear();
                    }
                }
            }

            transaction.commit();
            return returnValue;
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
    public String getLemmaName(Lemma lemma) {
        EntityManager entityManager = EntityManagerListener.createEntityManager();
        EntityTransaction transaction = null;

        try {
            transaction = entityManager.getTransaction();
            transaction.begin();
            lemma = entityManager.merge(lemma);
            entityManager.refresh(lemma);
            TypedQuery<String> query = entityManager
                    .createQuery("SELECT name FROM Lemma WHERE id = :id", String.class);
            String lemmaName = query.setParameter("id", lemma.getId()).getSingleResult();
            transaction.commit();
            return lemmaName;
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
    public List<Lemma> getAll() {
        EntityManager entityManager = EntityManagerListener.createEntityManager();
        EntityTransaction transaction = null;

        try {
            transaction = entityManager.getTransaction();
            transaction.begin();
            TypedQuery<Lemma> query = entityManager.createQuery("SELECT l FROM Lemma l " +
                            "LEFT JOIN FETCH l.replacement LEFT JOIN FETCH l.pos LEFT JOIN FETCH l.senses",
                    Lemma.class);
            List<Lemma> lemmaList = query.getResultList();
            transaction.commit();
            return lemmaList;
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
