package lemming.character;

import lemming.data.EntityManagerListener;
import lemming.data.GenericDao;
import org.hibernate.StaleObjectStateException;
import org.hibernate.UnresolvableObjectException;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.UUID;

/**
 * Represents a Data Access Object providing data operations for special
 * characters.
 */
public class CharacterDao extends GenericDao<Character> implements ICharacterDao {
    /**
     * Creates an instance of a CharacterDao.
     */
    public CharacterDao() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Boolean isTransient(Character character) {
        return character.getId() == null;
    }

    /**
     * {@inheritDoc}
     *
     * @throws RuntimeException
     */
    @Override
    public void persist(Character character) throws RuntimeException {
        EntityManager entityManager = EntityManagerListener.createEntityManager();
        EntityTransaction transaction = null;

        if (character.getUuid() == null) {
            character.setUuid(UUID.randomUUID().toString());
        }

        try {
            transaction = entityManager.getTransaction();
            transaction.begin();
            insertCharacter(entityManager, character);
            transaction.commit();
        } catch (RuntimeException e) {
            e.printStackTrace();

            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }

            if (e instanceof StaleObjectStateException) {
                panicOnSaveLockingError(character, e);
            } else if (e instanceof UnresolvableObjectException) {
                panicOnSaveUnresolvableObjectError(character, e);
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
    public Character merge(Character character) throws RuntimeException {
        EntityManager entityManager = EntityManagerListener.createEntityManager();
        EntityTransaction transaction = null;

        try {
            transaction = entityManager.getTransaction();
            transaction.begin();

            TypedQuery<Character> query = entityManager
                    .createQuery("FROM Character WHERE id = :id", Character.class);
            query.setParameter("id", character.getId());
            Character persistentCharacter = query.getSingleResult();
            Character mergedCharacter = moveCharacter(entityManager, character, persistentCharacter);
            transaction.commit();
            return mergedCharacter;
        } catch (RuntimeException e) {
            e.printStackTrace();

            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }

            if (e instanceof StaleObjectStateException) {
                panicOnSaveLockingError(character, e);
            } else if (e instanceof UnresolvableObjectException) {
                panicOnSaveUnresolvableObjectError(character, e);
            } else {
                throw e;
            }

            return null;
        } finally {
            entityManager.close();
        }
    }

    /**
     * Inserts a character at the specified position. Shifts subsequent elements
     * to the right.
     *
     * @param entityManager entity manager interacting with the persistence context
     * @param character     a character object
     * @throws RuntimeException
     */
    private void insertCharacter(EntityManager entityManager, Character character) throws RuntimeException {
        TypedQuery<Character> query = entityManager
                .createQuery("FROM Character WHERE position >= :position ORDER BY position DESC", Character.class);
        query.setParameter("position", character.getPosition());
        List<Character> elements = query.getResultList();

        for (Character element : elements) {
            element.setPosition(element.getPosition() + 1);
            entityManager.merge(element);
            entityManager.flush();
            entityManager.clear();
        }

        entityManager.persist(character);
    }

    /**
     * Moves a persistent character to a new position.
     *
     * @param entityManager       entity manager interacting with the persistence context
     * @param character           the new character
     * @param persistentCharacter the persistent character
     * @throws RuntimeException
     */
    private Character moveCharacter(EntityManager entityManager, Character character, Character persistentCharacter)
            throws RuntimeException {
        Integer position = character.getPosition();
        Integer persistentPosition = persistentCharacter.getPosition();
        Integer minPosition = Math.min(position, persistentPosition);
        Integer maxPosition = Math.max(position, persistentPosition);

        if (minPosition.equals(maxPosition)) {
            return entityManager.merge(character);
        }

        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Character> criteriaQuery = builder.createQuery(Character.class);
        Root<Character> root = criteriaQuery.from(Character.class);
        criteriaQuery.select(root);
        Order elementOrder;

        if (position.equals(minPosition)) {
            elementOrder = builder.desc(root.<Integer>get("position"));
        } else {
            elementOrder = builder.asc(root.<Integer>get("position"));
        }

        criteriaQuery.where(builder.and(builder.ge(root.<Integer>get("position"), minPosition)),
                builder.le(root.<Integer>get("position"), maxPosition)).orderBy(elementOrder);
        List<Character> elements = entityManager.createQuery(criteriaQuery).getResultList();

        elements.remove(persistentCharacter);
        character.setPosition(0);
        character = entityManager.merge(character);
        entityManager.flush();
        entityManager.clear();

        for (Character element : elements) {
            if (position.equals(minPosition)) {
                element.setPosition(maxPosition--);
            } else {
                element.setPosition(minPosition++);
            }

            entityManager.merge(element);
            entityManager.flush();
            entityManager.clear();
        }

        character.setPosition(position);
        return entityManager.merge(character);
    }

    /**
     * {@inheritDoc}
     *
     * @throws RuntimeException
     */
    @Override
    public void remove(Character character) throws RuntimeException {
        EntityManager entityManager = EntityManagerListener.createEntityManager();
        EntityTransaction transaction = null;

        try {
            transaction = entityManager.getTransaction();
            transaction.begin();

            TypedQuery<Character> query = entityManager
                    .createQuery("FROM Character WHERE id = :id", Character.class);
            query.setParameter("id", character.getId());

            Character persistentCharacter = query.getSingleResult();
            CriteriaBuilder builder = entityManager.getCriteriaBuilder();
            CriteriaQuery<Character> criteriaQuery = builder.createQuery(Character.class);
            Root<Character> root = criteriaQuery.from(Character.class);
            criteriaQuery.select(root);

            criteriaQuery.where(builder.gt(root.<Integer>get("position"), persistentCharacter.getPosition()))
                    .orderBy(builder.asc(root.<Integer>get("position")));
            List<Character> elements = entityManager.createQuery(criteriaQuery).getResultList();

            entityManager.remove(persistentCharacter);
            entityManager.flush();
            entityManager.clear();

            for (Character element : elements) {
                element.setPosition(element.getPosition() - 1);
                entityManager.merge(element);
                entityManager.flush();
                entityManager.clear();
            }

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
    public Character findByCharacter(String characterString) throws RuntimeException {
        EntityManager entityManager = EntityManagerListener.createEntityManager();
        EntityTransaction transaction = null;

        try {
            transaction = entityManager.getTransaction();
            transaction.begin();
            TypedQuery<Character> query = entityManager
                    .createQuery("FROM Character WHERE character = :character", Character.class);
            List<Character> characterList = query.setParameter("character", characterString).getResultList();
            transaction.commit();

            if (characterList.isEmpty()) {
                return null;
            } else {
                return characterList.get(0);
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
    public List<Character> getAll() throws RuntimeException {
        EntityManager entityManager = EntityManagerListener.createEntityManager();
        EntityTransaction transaction = null;

        try {
            transaction = entityManager.getTransaction();
            transaction.begin();
            TypedQuery<Character> query = entityManager
                    .createQuery("FROM Character ORDER BY position ASC", Character.class);
            List<Character> characterList = query.getResultList();
            transaction.commit();
            return characterList;
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
