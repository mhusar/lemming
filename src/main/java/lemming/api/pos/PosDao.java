package lemming.api.pos;

import lemming.api.data.EntityManagerListener;
import lemming.api.data.GenericDao;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;
import java.util.List;

/**
 * Represents a Data Access Object providing data operations for parts of speech.
 */
public class PosDao extends GenericDao<Pos> implements IPosDao {
    /**
     * Creates an instance of a PosDao.
     */
    public PosDao() {
        super();
    }

    /**
     * {@inheritDoc}
     *
     * @throws RuntimeException
     */
    public List<Pos> findByName(String substring) throws RuntimeException {
        EntityManager entityManager = EntityManagerListener.createEntityManager();
        EntityTransaction transaction = null;

        try {
            transaction = entityManager.getTransaction();
            transaction.begin();
            TypedQuery<Pos> query = entityManager
                    .createQuery("FROM Pos WHERE name LIKE :substring", Pos.class);
            List<Pos> posList = query.setParameter("substring", substring + "%").getResultList();
            transaction.commit();
            return posList;
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
