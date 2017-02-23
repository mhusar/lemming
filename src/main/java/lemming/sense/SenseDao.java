package lemming.sense;

import lemming.data.EntityManagerListener;
import lemming.data.GenericDao;
import lemming.lemma.Lemma;
import org.hibernate.StaleObjectStateException;
import org.hibernate.UnresolvableObjectException;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.UUID;

/**
 * Represents a Data Access Object providing data operations for senses.
 */
public class SenseDao extends GenericDao<Sense> implements ISenseDao {
    /**
     * Creates an instance of a SenseDao.
     */
    public SenseDao() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    public Boolean isTransient(Sense sense) {
        return !(sense.getId() instanceof Integer);
    }

    /**
     * Refreshes foreign key strings of a sense.
     *
     * @param sense the refreshed sense
     */
    private void refreshForeignKeyStrings(Sense sense) {
        if (sense.getLemma() instanceof Lemma) {
            sense.setLemmaString(sense.getLemma().getName());
        }
    }

    /**
     * {@inheritDoc}
     *
     * @throws RuntimeException
     */
    public Sense refresh(Sense sense) throws RuntimeException {
        EntityManager entityManager = EntityManagerListener.createEntityManager();
        EntityTransaction transaction = null;

        try {
            transaction = entityManager.getTransaction();
            transaction.begin();
            sense = entityManager.merge(sense);
            TypedQuery<Sense> query = entityManager.createQuery("SELECT s FROM Sense s LEFT JOIN FETCH s.lemma " +
                    "LEFT JOIN FETCH s.children WHERE s.id = :id", Sense.class);
            Sense refreshedSense = query.setParameter("id", sense.getId()).getSingleResult();
            transaction.commit();
            return refreshedSense;
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
    public void persist(Sense sense) throws RuntimeException {
        EntityManager entityManager = EntityManagerListener.createEntityManager();
        EntityTransaction transaction = null;

        if (!(sense.getUuid() instanceof String)) {
            sense.setUuid(UUID.randomUUID().toString());
        }

        try {
            transaction = entityManager.getTransaction();
            transaction.begin();
            refreshForeignKeyStrings(sense);
            entityManager.persist(sense);
            transaction.commit();
        } catch (RuntimeException e) {
            e.printStackTrace();

            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }

            if (e instanceof StaleObjectStateException) {
                panicOnSaveLockingError(sense, e);
            } else if (e instanceof UnresolvableObjectException) {
                panicOnSaveUnresolvableObjectError(sense, e);
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
    public Sense merge(Sense sense) throws RuntimeException {
        EntityManager entityManager = EntityManagerListener.createEntityManager();
        EntityTransaction transaction = null;

        try {
            transaction = entityManager.getTransaction();
            transaction.begin();
            Sense mergedSense = entityManager.merge(sense);
            refreshForeignKeyStrings(mergedSense);
            mergedSense = entityManager.merge(mergedSense);
            transaction.commit();
            return mergedSense;
        } catch (RuntimeException e) {
            e.printStackTrace();

            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }

            if (e instanceof StaleObjectStateException) {
                panicOnSaveLockingError(sense, e);
            } else if (e instanceof UnresolvableObjectException) {
                panicOnSaveUnresolvableObjectError(sense, e);
            } else {
                throw e;
            }
        } finally {
            entityManager.close();
            return null;
        }
    }

    /**
     * {@inheritDoc}
     *
     * @throws RuntimeException
     */
    public List<Sense> findByLemma(Lemma lemma) throws RuntimeException {
        EntityManager entityManager = EntityManagerListener.createEntityManager();
        EntityTransaction transaction = null;

        try {
            transaction = entityManager.getTransaction();
            transaction.begin();
            TypedQuery<Sense> query = entityManager.createQuery("SELECT s FROM Sense s LEFT JOIN FETCH s.lemma " +
                    "LEFT JOIN FETCH s.children WHERE s.lemma = :lemma", Sense.class);
            List<Sense> senseList = query.setParameter("lemma", lemma).getResultList();
            transaction.commit();
            return senseList;
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
    public Sense findByMeaning(String meaning) throws RuntimeException {
        EntityManager entityManager = EntityManagerListener.createEntityManager();
        EntityTransaction transaction = null;

        try {
            transaction = entityManager.getTransaction();
            transaction.begin();
            TypedQuery<Sense> query = entityManager
                    .createQuery("SELECT s FROM Sense s LEFT JOIN FETCH s.lemma LEFT JOIN FETCH s.children " +
                            "WHERE s.meaning = :meaning ORDER BY s.meaning", Sense.class);
            List<Sense> senseList = query.setParameter("meaning", meaning).getResultList();
            transaction.commit();

            if (senseList.isEmpty()) {
                return null;
            } else {
                return senseList.get(0);
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
    public List<Sense> findRootNodes(Lemma lemma) {
        EntityManager entityManager = EntityManagerListener.createEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Sense> criteriaQuery = criteriaBuilder.createQuery(Sense.class);
        Root<Sense> root = criteriaQuery.from(Sense.class);
        EntityTransaction transaction = null;

        try {
            transaction = entityManager.getTransaction();
            transaction.begin();

            criteriaQuery.select(root);
            criteriaQuery.where(criteriaBuilder.and(criteriaBuilder.equal(root.get("lemma"), lemma)),
                    criteriaBuilder.isNull(root.get("childPosition")));
            criteriaQuery.orderBy(criteriaBuilder.asc(root.get("parentPosition")));

            TypedQuery<Sense> query = entityManager.createQuery(criteriaQuery);
            List<Sense> senseList = query.getResultList();
            transaction.commit();
            return senseList;
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
