package lemming.api.lemma;

import lemming.api.data.EntityManagerListener;
import lemming.api.data.GenericDao;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;
import java.util.List;

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
    public Lemma findByName(String name) throws RuntimeException {
        EntityManager entityManager = EntityManagerListener.createEntityManager();
        EntityTransaction transaction = null;

        try {
            transaction = entityManager.getTransaction();
            transaction.begin();
            TypedQuery<Lemma> query = entityManager
                    .createQuery("FROM Lemma WHERE name = :name", Lemma.class);
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
    public List<Lemma> findByName(String substring) throws RuntimeException {
        EntityManager entityManager = EntityManagerListener.createEntityManager();
        EntityTransaction transaction = null;

        try {
            transaction = entityManager.getTransaction();
            transaction.begin();
            TypedQuery<Lemma> query = entityManager
                    .createQuery("FROM Lemma WHERE name LIKE :substring", Lemma.class);
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
}
