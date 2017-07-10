package lemming.data;

import org.hibernate.ScrollableResults;

import java.util.List;

/**
 * The root interface of the DAO hierarchy.
 *
 * @param <E> entity type
 */
public interface IDao<E> {
    /**
     * Checks if an object is transient.
     *
     * @param element
     *            element to check
     * @return True if element is transient; false otherwise.
     */
    Boolean isTransient(E element);

    /**
     * Makes an instance managed and persistent.
     *
     * @param entity entity instance
     */
    void persist(E entity);

    /**
     * Merge the state of the given entity into the current persistence context.
     *
     * @param entity entity instance
     * @return The managed instance that the state was merged to.
     */
    E merge(E entity);

    /**
     * Removes the entity instance.
     *
     * @param entity entity instance
     */
    void remove(E entity);

    /**
     * Removes the entinty instance found by primary key, if any.
     *
     * @param primaryKey primary key
     */
    void removeByPrimaryKey(Object primaryKey);

    /**
     * Refresh the state of the instance from the database, overwriting changes made to the entity, if any.
     *
     * @param entity entity instance
     * @return The managed instance that was refreshed.
     */
    E refresh(E entity);

    /**
     * Find by primary key. Search for an entity of the specified class and primary key. If the entity instance is
     * contained in the persistence context, it is returned from there.
     *
     * @param primaryKey  primary key
     * @return The found entity instance or null if the entity does not exist.
     */
    E find(Object primaryKey);

    /**
     * Delivers a list with all entity instances.
     *
     * @return A list of entity instances.
     */
    List<E> getAll();

    /**
     * Sends the user to an error page if saving of data failed due to locking.
     *
     * @param element
     *            the element that was not saved
     * @param exception
     *            the raised exception
     */
    void panicOnSaveLockingError(Object element, RuntimeException exception);

    /**
     * Sends the user to an error page if removing of data failed due to
     * locking.
     *
     * @param element the element that was not removed
     * @param exception
     *            the raised exception
     */
    void panicOnRemoveLockingError(Object element, RuntimeException exception);

    /**
     * Sends the user to an error page if saving of data failed due to an
     * unresolvable object.
     *
     * @param element
     *            the element that was not saved
     * @param exception
     *            the raised exception
     */
    void panicOnSaveUnresolvableObjectError(Object element, RuntimeException exception);

    /**
     * Sends the user to an error page if removing of data failed due to an
     * unresolvable object.
     *
     * @param element the element that was not removed
     * @param exception
     *            the raised exception
     */
    void panicOnRemoveUnresolvableObjectError(Object element, RuntimeException exception);
}
