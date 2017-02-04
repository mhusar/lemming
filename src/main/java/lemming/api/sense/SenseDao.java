package lemming.api.sense;

import lemming.api.data.EntityManagerListener;
import lemming.api.data.GenericDao;
import lemming.api.lemma.Lemma;
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
    @Override
    public Boolean isTransient(Sense sense) {
        return !(sense.getId() instanceof Integer);
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
            entityManager.refresh(sense);
            sense.getChildren().size();

            transaction.commit();
            return sense;
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
    public Sense findByMeaning(String meaning) throws RuntimeException {
        EntityManager entityManager = EntityManagerListener.createEntityManager();
        EntityTransaction transaction = null;

        try {
            transaction = entityManager.getTransaction();
            transaction.begin();
            TypedQuery<Sense> query = entityManager
                    .createQuery("FROM Sense WHERE meaning = :meaning", Sense.class);
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
    @Override
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
