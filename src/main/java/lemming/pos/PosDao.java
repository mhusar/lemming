package lemming.pos;

import lemming.context.Context;
import lemming.data.EntityManagerListener;
import lemming.data.GenericDao;
import lemming.data.Source;
import lemming.lemma.Lemma;
import org.hibernate.StaleObjectStateException;
import org.hibernate.UnresolvableObjectException;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.UUID;

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
     */
    public Boolean isTransient(Pos pos) {
        return !(pos.getId() instanceof Integer);
    }

    /**
     * Refreshes foreign key strings of a part of speech.
     *
     * @param pos the refreshed part of speech
     */
    private void refreshForeignKeyStrings(EntityManager entityManager, Pos pos) {
        TypedQuery<Context> contextQuery = entityManager.createQuery("FROM Context WHERE pos = :pos", Context.class);
        List<Context> contextList = contextQuery.setParameter("pos", pos).getResultList();
        TypedQuery<Lemma> lemmaQuery = entityManager.createQuery("FROM Lemma WHERE pos = :pos", Lemma.class);
        List<Lemma> lemmaList = lemmaQuery.setParameter("pos", pos).getResultList();

        for (Context context : contextList) {
            context.setPosString(pos.getName());
        }

        for (Lemma lemma : lemmaList) {
            lemma.setPosString(pos.getName());
        }
    }

    /**
     * {@inheritDoc}
     *
     * @throws RuntimeException
     */
    public void persist(Pos pos) throws RuntimeException {
        EntityManager entityManager = EntityManagerListener.createEntityManager();
        EntityTransaction transaction = null;

        if (!(pos.getUuid() instanceof String)) {
            pos.setUuid(UUID.randomUUID().toString());
        }

        try {
            transaction = entityManager.getTransaction();
            transaction.begin();
            entityManager.persist(pos);
            transaction.commit();
        } catch (RuntimeException e) {
            e.printStackTrace();

            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }

            if (e instanceof StaleObjectStateException) {
                panicOnSaveLockingError(pos, e);
            } else if (e instanceof UnresolvableObjectException) {
                panicOnSaveUnresolvableObjectError(pos, e);
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
    public Pos merge(Pos pos) throws RuntimeException {
        EntityManager entityManager = EntityManagerListener.createEntityManager();
        EntityTransaction transaction = null;

        try {
            transaction = entityManager.getTransaction();
            transaction.begin();
            refreshForeignKeyStrings(entityManager, pos);
            Pos mergedPos = entityManager.merge(pos);
            transaction.commit();
            return mergedPos;
        } catch (RuntimeException e) {
            e.printStackTrace();

            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }

            if (e instanceof StaleObjectStateException) {
                panicOnSaveLockingError(pos, e);
            } else if (e instanceof UnresolvableObjectException) {
                panicOnSaveUnresolvableObjectError(pos, e);
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
    public Pos findByName(String name) throws RuntimeException {
        EntityManager entityManager = EntityManagerListener.createEntityManager();
        EntityTransaction transaction = null;

        try {
            transaction = entityManager.getTransaction();
            transaction.begin();
            TypedQuery<Pos> query = entityManager
                    .createQuery("FROM Pos WHERE name = :name ORDER BY name", Pos.class);
            List<Pos> posList = query.setParameter("name", name).getResultList();
            transaction.commit();

            if (posList.isEmpty()) {
                return null;
            } else {
                for (Pos pos : posList) {
                    if (pos.getName().equals(name)) {
                        return pos;
                    }
                }

                return null;
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
    public List<Pos> findByNameStart(String substring) throws RuntimeException {
        EntityManager entityManager = EntityManagerListener.createEntityManager();
        EntityTransaction transaction = null;

        try {
            transaction = entityManager.getTransaction();
            transaction.begin();
            TypedQuery<Pos> query = entityManager
                    .createQuery("FROM Pos WHERE name LIKE :substring ORDER BY name", Pos.class);
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

    /**
     * {@inheritDoc}
     *
     * @throws RuntimeException
     */
    public List<Pos> findBySource(Source.PosType source) {
        EntityManager entityManager = EntityManagerListener.createEntityManager();
        EntityTransaction transaction = null;

        try {
            transaction = entityManager.getTransaction();
            transaction.begin();
            TypedQuery<Pos> query = entityManager
                    .createQuery("FROM Pos WHERE source = :source ORDER BY name", Pos.class);
            List<Pos> posList = query.setParameter("source", source).getResultList();
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

    /**
     * {@inheritDoc}
     *
     * @throws RuntimeException
     */
    public String getPosName(Pos pos) {
        EntityManager entityManager = EntityManagerListener.createEntityManager();
        EntityTransaction transaction = null;

        try {
            transaction = entityManager.getTransaction();
            transaction.begin();
            pos = entityManager.merge(pos);
            entityManager.refresh(pos);
            TypedQuery<String> query = entityManager
                    .createQuery("SELECT name FROM Pos WHERE id = :id", String.class);
            String lemmaName = query.setParameter("id", pos.getId()).getSingleResult();
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
}
