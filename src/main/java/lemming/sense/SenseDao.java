package lemming.sense;

import lemming.data.EntityManagerListener;
import lemming.data.GenericDao;
import lemming.lemma.Lemma;
import org.hibernate.StaleObjectStateException;
import org.hibernate.UnresolvableObjectException;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
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
        return sense.getId() == null;
    }

    /**
     * Refreshes foreign key strings of a sense.
     *
     * @param sense the refreshed sense
     */
    private void refreshForeignKeyStrings(Sense sense) {
        if (sense.getLemma() != null) {
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

        if (sense.getUuid() == null) {
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
    public void remove(Sense sense) throws RuntimeException {
        EntityManager entityManager = EntityManagerListener.createEntityManager();
        EntityTransaction transaction = null;

        try {
            transaction = entityManager.getTransaction();
            transaction.begin();
            Sense mergedSense = entityManager.merge(sense);

            // remove contexts from sense
            Query updateContextsQuery = entityManager
                    .createQuery("UPDATE Context c SET c.sense = null WHERE c.sense = :sense");
            updateContextsQuery.setParameter("sense", mergedSense).executeUpdate();

            if (sense.isParentSense()) {
                // fix parent positions of siblings and their children
                fixParentPositions(entityManager, mergedSense);
            } else {
                // remove sense from parent sense and fix child positions
                removeFromParentSense(entityManager, mergedSense);
            }

            entityManager.remove(mergedSense);
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
     * Remove sense from parent sense and fix child positions.
     *
     * @param entityManager an interface to interact with the persistence context
     * @param sense the sense to remove
     */
    private void removeFromParentSense(EntityManager entityManager, Sense sense) {
        TypedQuery<Sense> parentQuery = entityManager
                .createQuery("SELECT s FROM Sense s WHERE :sense IN ELEMENTS(s.children)", Sense.class);
        Sense parentSense = parentQuery.setParameter("sense", sense).getSingleResult();

        // remove sense from parent sense
        parentSense.getChildren().remove(sense);

        // refresh parent sense
        TypedQuery<Sense> query = entityManager.createQuery("SELECT s FROM Sense s LEFT JOIN FETCH s.lemma " +
                "LEFT JOIN FETCH s.children WHERE s.id = :id", Sense.class);
        Sense refreshedParentSense = query.setParameter("id", parentSense.getId()).getSingleResult();

        // fix child positions of children
        for (int i = 0; i < refreshedParentSense.getChildren().size(); i++) {
            refreshedParentSense.getChildren().get(i).setChildPosition(i);
        }
    }

    /**
     * Fix parent positions of siblings and their children.
     *
     * @param entityManager an interface to interact with the persistence context
     * @param sense the sense to remove
     */
    private void fixParentPositions(EntityManager entityManager, Sense sense) {
        TypedQuery<Sense> siblingsQuery = entityManager.createQuery("SELECT s FROM Sense s LEFT JOIN FETCH s.lemma " +
                "LEFT JOIN FETCH s.children WHERE s.lemma = :lemma AND s != :sense", Sense.class);
        List<Sense> siblingList = siblingsQuery.setParameter("lemma", sense.getLemma()).setParameter("sense", sense)
                .getResultList();

        for (int i = 0; i < siblingList.size(); i++) {
            Sense sibling = siblingList.get(i);
            sibling.setParentPosition(i);

            for (Sense child : sibling.getChildren()) {
                child.setParentPosition(i);
            }
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
        EntityTransaction transaction = null;

        try {
            transaction = entityManager.getTransaction();
            transaction.begin();
            TypedQuery<Sense> query = entityManager.createQuery("SELECT s FROM Sense s "
                    + "WHERE s.childPosition IS NULL", Sense.class);
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

    /**
     * {@inheritDoc}
     *
     * @throws RuntimeException
     */
    @Override
    public Sense getParent(Sense sense) {
        EntityManager entityManager = EntityManagerListener.createEntityManager();
        EntityTransaction transaction = null;

        try {
            transaction = entityManager.getTransaction();
            transaction.begin();
            TypedQuery<Sense> query = entityManager
                    .createQuery("SELECT s FROM Sense s LEFT JOIN FETCH s.lemma LEFT JOIN FETCH s.children " +
                            "WHERE :sense IN ELEMENTS(s.children)", Sense.class);
            List<Sense> parentSenseList = query.setParameter("sense", sense).getResultList();
            transaction.commit();

            if (parentSenseList.isEmpty()) {
                return null;
            } else {
                return parentSenseList.get(0);
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
    public List<Sense> getChildren(Sense sense) {
        EntityManager entityManager = EntityManagerListener.createEntityManager();
        EntityTransaction transaction = null;

        try {
            transaction = entityManager.getTransaction();
            transaction.begin();
            TypedQuery<Sense> query = entityManager.createQuery("SELECT s FROM Sense s WHERE s.lemma = :lemma " +
                            "AND s.parentPosition = :parentPosition AND s.childPosition IS NOT NULL " +
                            "ORDER BY s.childPosition", Sense.class);
            List<Sense> childSenseList = query.setParameter("lemma", sense.getLemma())
                    .setParameter("parentPosition", sense.getParentPosition()).getResultList();
            transaction.commit();
            return childSenseList;
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
    public Boolean hasChildSenses(Sense sense) {
        EntityManager entityManager = EntityManagerListener.createEntityManager();
        EntityTransaction transaction = null;

        try {
            transaction = entityManager.getTransaction();
            transaction.begin();
            TypedQuery<Sense> query = entityManager.createQuery("SELECT s FROM Sense s LEFT JOIN FETCH s.children " +
                    "WHERE s.id = :id", Sense.class);
            Sense refreshedSense = query.setParameter("id", sense.getId()).getSingleResult();
            transaction.commit();
            return !refreshedSense.getChildren().isEmpty();
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
    public void moveBefore(Sense source, Sense target) {
        EntityManager entityManager = EntityManagerListener.createEntityManager();
        EntityTransaction transaction = null;

        try {
            transaction = entityManager.getTransaction();
            transaction.begin();
            TypedQuery<Sense> refreshQuery = entityManager.createQuery("SELECT s FROM Sense s " +
                    "LEFT JOIN FETCH s.lemma LEFT JOIN FETCH s.children WHERE s.id = :id", Sense.class);
            Sense refreshedTarget = refreshQuery.setParameter("id", target.getId()).getSingleResult();
            Sense mergedSource = entityManager.merge(source);
            Lemma lemma = refreshedTarget.getLemma();
            Integer parentPosition = refreshedTarget.getParentPosition();
            Integer childPosition = refreshedTarget.getChildPosition();

            if (childPosition == null) { // is parent sense
                createParentPositionGap(entityManager, lemma, parentPosition);
                entityManager.refresh(mergedSource);
                setParentPosition(entityManager, mergedSource, parentPosition);
            } else { // is child sense
                createChildPositionGap(entityManager, lemma, parentPosition, childPosition);
                entityManager.refresh(mergedSource);
                setChildPosition(entityManager, mergedSource, refreshedTarget, parentPosition, childPosition);
            }

            closePositionGaps(entityManager, lemma);
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
    @Override
    public void moveAfter(Sense source, Sense target) {
        EntityManager entityManager = EntityManagerListener.createEntityManager();
        EntityTransaction transaction = null;

        try {
            transaction = entityManager.getTransaction();
            transaction.begin();
            TypedQuery<Sense> refreshQuery = entityManager.createQuery("SELECT s FROM Sense s " +
                    "LEFT JOIN FETCH s.children WHERE s.id = :id", Sense.class);
            Sense refreshedTarget = refreshQuery.setParameter("id", target.getId()).getSingleResult();
            Sense mergedSource = entityManager.merge(source);
            Lemma lemma = refreshedTarget.getLemma();
            Integer parentPosition = refreshedTarget.getParentPosition();
            Integer childPosition = refreshedTarget.getChildPosition();

            if (childPosition == null) { // is parent sense
                createParentPositionGap(entityManager, lemma, parentPosition + 1);
                entityManager.refresh(mergedSource);
                setParentPosition(entityManager, mergedSource, parentPosition + 1);
            } else { // is child sense
                createChildPositionGap(entityManager, lemma, parentPosition, childPosition + 1);
                entityManager.refresh(mergedSource);
                setChildPosition(entityManager, mergedSource, refreshedTarget, parentPosition, childPosition + 1);
            }

            closePositionGaps(entityManager, lemma);
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

    private void createChildPositionGap(EntityManager entityManager, Lemma lemma, Integer parentPosition,
                                        Integer gapPosition) {
        TypedQuery<Sense> query = entityManager.createQuery("SELECT s FROM Sense s WHERE s.lemma = :lemma " +
                "AND s.parentPosition = :parentPosition AND s.childPosition IS NOT NULL " +
                "ORDER BY s.childPosition", Sense.class);
        List<Sense> senseList = query.setParameter("lemma", lemma).setParameter("parentPosition", parentPosition)
                .getResultList();

        for (Sense sense : senseList) {
            Integer childPosition = sense.getChildPosition();

            if (childPosition >= gapPosition) {
                sense.setChildPosition(childPosition + 1);
                entityManager.merge(sense);
            }
        }

        entityManager.flush();
    }

    private void createParentPositionGap(EntityManager entityManager, Lemma lemma, Integer gapPosition) {
        TypedQuery<Sense> query = entityManager.createQuery("SELECT s FROM Sense s WHERE s.lemma = :lemma " +
                "ORDER BY s.parentPosition, s.childPosition", Sense.class);
        List<Sense> senseList = query.setParameter("lemma", lemma).getResultList();

        for (Sense sense : senseList) {
            Integer parentPosition = sense.getParentPosition();

            if (parentPosition >= gapPosition) {
                sense.setParentPosition(parentPosition + 1);
                entityManager.merge(sense);
            }
        }

        entityManager.flush();
    }

    private void setChildPosition(EntityManager entityManager, Sense source, Sense target, Integer parentPosition,
                                  Integer childPosition) {
        TypedQuery<Sense> refreshQuery = entityManager.createQuery("SELECT DISTINCT s FROM Sense s " +
                "LEFT JOIN FETCH s.children WHERE s.id = :id", Sense.class);
        Sense refreshedSource = refreshQuery.setParameter("id", source.getId()).getSingleResult();
        TypedQuery<Sense> parentQuery = entityManager.createQuery("SELECT DISTINCT s FROM Sense s " +
                "LEFT JOIN FETCH s.children WHERE :sense IN ELEMENTS(s.children)", Sense.class);
        List<Sense> sourceParentSense = parentQuery.setParameter("sense", refreshedSource).getResultList();
        Sense targetParent = parentQuery.setParameter("sense", target).getSingleResult();

        refreshedSource.setParentPosition(parentPosition);
        refreshedSource.setChildPosition(childPosition);

        // add sense to new parent sense
        targetParent.getChildren().add(refreshedSource);
        entityManager.merge(targetParent);

        // remove sense from old parent sense
        for (Sense parentSense : sourceParentSense) {
            parentSense.getChildren().remove(refreshedSource);
            entityManager.merge(parentSense);
        }

        entityManager.merge(refreshedSource);
        entityManager.flush();
    }

    private void setParentPosition(EntityManager entityManager, Sense source, Integer parentPosition) {
        TypedQuery<Sense> refreshQuery = entityManager.createQuery("SELECT DISTINCT s FROM Sense s " +
                "LEFT JOIN FETCH s.children WHERE s.id = :id", Sense.class);
        Sense refreshedSource = refreshQuery.setParameter("id", source.getId()).getSingleResult();
        TypedQuery<Sense> parentQuery = entityManager.createQuery("SELECT DISTINCT s FROM Sense s " +
                "LEFT JOIN FETCH s.children WHERE :sense IN ELEMENTS(s.children)", Sense.class);
        List<Sense> sourceParentList = parentQuery.setParameter("sense", refreshedSource).getResultList();

        refreshedSource.setParentPosition(parentPosition);
        refreshedSource.setChildPosition(null);

        for (Sense childSense : refreshedSource.getChildren()) {
            childSense.setParentPosition(parentPosition);
            entityManager.merge(childSense);
        }

        // remove sense from parent sense
        for (Sense parentSense : sourceParentList) {
            parentSense.getChildren().remove(refreshedSource);
            entityManager.merge(parentSense);
        }

        entityManager.merge(refreshedSource);
        entityManager.flush();
    }

    private void closePositionGaps(EntityManager entityManager, Lemma lemma) {
        TypedQuery<Sense> parentQuery = entityManager.createQuery("SELECT DISTINCT s FROM Sense s " +
                "WHERE s.lemma = :lemma AND s.childPosition IS NULL ORDER BY s.parentPosition", Sense.class);
        TypedQuery<Sense> childQuery = entityManager.createQuery("SELECT DISTINCT s FROM Sense s " +
                "WHERE s.lemma = :lemma AND s.parentPosition = :parentPosition AND s.childPosition IS NOT NULL " +
                "ORDER BY s.childPosition", Sense.class);
        List<Sense> senseList = parentQuery.setParameter("lemma", lemma).getResultList();

        for (int i = 0; i < senseList.size(); i++) {
            Sense sense = senseList.get(i);
            sense.setParentPosition(i);
            List<Sense> children = childQuery.setParameter("lemma", lemma)
                    .setParameter("parentPosition", sense.getParentPosition()).getResultList();

            for (int j = 0; j < children.size(); j++) {
                Sense child = children.get(j);
                child.setParentPosition(i);
                child.setChildPosition(j);

                entityManager.merge(child);
            }

            entityManager.merge(sense);
        }
    }
}
