package lemming.data;

import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.IFilterStateLocator;
import org.apache.wicket.extensions.markup.html.repeater.util.SortParam;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Order;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Provides data for data table views.
 *
 * @param <T>
 *            data type that is provided
 */
public final class GenericDataProvider<T> extends SortableDataProvider<T, String> implements IFilterStateLocator<T> {
    /**
     * Determines if a deserialized file is compatible with this class.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The data type that is provided.
     */
    protected Class<T> typeClass;

    /**
     * Defindes the default sort order.
     */
    protected SortParam<String> defaultSortParam;

    /**
     * The state defined by a filter form.
     */
    protected T state;

    /**
     * The state defined a string filter.
     */
    protected String filter;

    /**
     * Creates a data provider.
     *
     * @param typeClass
     *            class type that is provided
     * @param defaultSortParam
     *            default sort param
     */
    public GenericDataProvider(Class<T> typeClass, SortParam<String> defaultSortParam) {
        this.typeClass = typeClass;
        this.defaultSortParam = defaultSortParam;
    }

    /**
     * Returns an iterator for a subset of total data.
     *
     * @param first
     *            first row of data
     * @param count
     *            minimum number of rows retrieved
     * @return Iterator capable of iterating over row data.
     */
    @Override
    @SuppressWarnings("unchecked")
    public Iterator<T> iterator(long first, long count) {
        EntityManager entityManager = EntityManagerListener.createEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> criteriaQuery = criteriaBuilder.createQuery(typeClass);
        Root<T> root = criteriaQuery.from(typeClass);
        EntityTransaction transaction = entityManager.getTransaction();

        if (!(getSort() instanceof SortParam)) {
            setSort(defaultSortParam);
        }

        Selection<T> selection = getSelection(root);
        Map<String,Join<?,?>> joins = CriteriaHelper.getJoins(root, typeClass);
        Expression<Boolean> restriction = getRestriction(criteriaBuilder, root, joins);
        Order order = getOrder(criteriaBuilder, root, joins);
        TypedQuery<T> typedQuery = null;

        if (restriction == null) {
            typedQuery = entityManager.createQuery(criteriaQuery.select(selection).orderBy(order))
                    .setFirstResult((int) first).setMaxResults((int) count);
        } else {
            typedQuery = entityManager.createQuery(criteriaQuery.select(selection).where(restriction).orderBy(order))
                    .setFirstResult((int) first).setMaxResults((int) count);
        }

        try {
            transaction.begin();
            Iterator<T> iterator = typedQuery.getResultList().iterator();
            transaction.commit();
            return iterator;
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
     * Wraps objects retrieved from an iterator as models.
     *
     * @param object object that needs to be wrapped
     * @return The model representation of an object.
     */
    @Override
    public IModel<T> model(T object) {
        return new AbstractReadOnlyModel<T>() {
            /**
             * Determines if a deserialized file is compatible with this class.
             */
            private static final long serialVersionUID = 1L;

            /**
             * Returns the model object.
             *
             * @return The model object.
             */
            public T getObject() {
                return object;
            }
        };
    }

    /**
     * Returns the total number of items in the collection represented by the DataProvider.
     *
     * @return Total number of items.
     */
    @Override
    public long size() {
        EntityManager entityManager = EntityManagerListener.createEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        Root<T> root = criteriaQuery.from(typeClass);
        EntityTransaction transaction = entityManager.getTransaction();

        if (!(getSort() instanceof SortParam)) {
            setSort(defaultSortParam);
        }

        Map<String,Join<?,?>> joins = CriteriaHelper.getJoins(root, typeClass);
        Expression<Boolean> restriction = getRestriction(criteriaBuilder, root, joins);
        TypedQuery<Long> typedQuery = null;

        if (restriction == null) {
            typedQuery = entityManager.createQuery(criteriaQuery.select(criteriaBuilder.count(root)));
        } else {
            typedQuery = entityManager.createQuery(criteriaQuery.select(criteriaBuilder.count(root))
                    .where(restriction));
        }

        try {
            transaction.begin();
            Long size = typedQuery.getSingleResult();
            transaction.commit();
            return size;
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
     * Returns the state defined by a filter form.
     *
     * @return A state object.
     */
    @Override
    public T getFilterState() {
        return state;
    }

    /**
     * Sets the state defined by a filter form.
     *
     * @param state state object
     */
    @Override
    public void setFilterState(T state) {
        this.state = state;
        this.filter = null;
    }

    /**
     * Returns automatically created restrictions for a filter state.
     *
     * @param criteriaBuilder contructor for criteria queries
     * @param root query root referencing entities
     * @return An expression of type boolean, or null.
     */
    protected Expression<Boolean> getFilterStateRestriction(CriteriaBuilder criteriaBuilder, Root<T> root) {
        if (state instanceof Object) {
            List<Predicate> predicateList = new ArrayList<Predicate>();

            for (Field field : state.getClass().getDeclaredFields()) {
                field.setAccessible(true);

                if (field.getName().equals("serialVersionUID") || field.getName().equals("id")
                        || field.getName().equals("uuid")) {
                    continue;
                }

                Object value = null;

                try {
                    value = field.get(state);
                } catch (IllegalArgumentException | IllegalAccessException e) {
                    e.printStackTrace();
                }

                if (value != null) {
                    if (value instanceof String) {
                        Expression<String> expression = criteriaBuilder.upper(root.get(field.getName()));
                        String filter = (String) value + "%".toUpperCase();
                        predicateList.add(criteriaBuilder.like(expression, filter));
                    } else {
                        Expression<String> expression = root.get(field.getName());
                        predicateList.add(criteriaBuilder.equal(expression, value));
                    }
                }
            }

            if (!predicateList.isEmpty()) {
                return criteriaBuilder.or(predicateList.toArray(new Predicate[predicateList.size()]));
            }
        }

        return null;
    }

    /**
     * Returns automatically created restrictions for a filter string.
     *
     * @param criteriaBuilder contructor for criteria queries
     * @param root query root referencing entities
     * @return An expression of type boolean, or null.
     */
    protected Expression<Boolean> getFilterStringRestriction(CriteriaBuilder criteriaBuilder, Root<T> root,
                                                             Map<String,Join<?,?>> joins) {
        if (filter instanceof String) {
            return CriteriaHelper.getFilterStringRestriction(criteriaBuilder, root, joins, filter, typeClass);
        }

        return null;
    }

    /**
     * Updates the string filter of the DataProvider.
     *
     * @param filter string filter
     */
    public void updateFilter(String filter) {
        this.filter = filter;
        this.state = null;
    }

    /**
     * Return the selection for criteria queries.
     *
     * @param root query root referencing entities
     * @return A selection.
     */
    protected Selection<T> getSelection(Root<T> root) {
        return root;
    }

    /**
     * Returns filter string or filter state restrictions.
     *
     * @param criteriaBuilder contructor for criteria queries
     * @param root query root referencing entities
     * @return An expression of type boolean, or null.
     */
    protected Expression<Boolean> getRestriction(CriteriaBuilder criteriaBuilder, Root<T> root,
                                                 Map<String,Join<?,?>> joins) {
        Expression<Boolean> filterStateRestriction = getFilterStateRestriction(criteriaBuilder, root);
        Expression<Boolean> filterStringRestriction = getFilterStringRestriction(criteriaBuilder, root, joins);

        if (filterStateRestriction instanceof Expression) {
            return filterStateRestriction;
        } else if (filterStringRestriction instanceof Expression) {
            return filterStringRestriction;
        }

        return null;
    }

    /**
     * Returns an order matching sort properties.
     *
     * @param criteriaBuilder contructor for criteria queries
     * @param root query root referencing entities
     * @param joins map of joins
     * @return An order object.
     */
    protected Order getOrder(CriteriaBuilder criteriaBuilder, Root<T> root, Map<String,Join<?,?>> joins) {
        String property = getSort().getProperty();
        Order order = CriteriaHelper.getOrder(criteriaBuilder, root, joins, property, getSort().isAscending());

        return order;
    }
}
