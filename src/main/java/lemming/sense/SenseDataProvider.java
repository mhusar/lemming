package lemming.sense;

import lemming.data.EntityManagerListener;
import lemming.lemma.Lemma;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.IFilterStateLocator;
import org.apache.wicket.extensions.markup.html.repeater.util.SortParam;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Provides sense data for data table views.
 */
public final class SenseDataProvider extends SortableDataProvider<SenseWrapper, String> implements IFilterStateLocator<SenseWrapper> {
    /**
     * Determines if a deserialized file is compatible with this class.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The data type that is provided.
     */
    protected Class<?> typeClass;

    /**
     * Defindes the default sort order.
     */
    protected SortParam<String> defaultSortParam;

    /**
     * The state defined by a filter form.
     */
    protected SenseWrapper state;

    /**
     * The state defined a string filter.
     */
    protected String filter;

    /**
     * Creates a data provider.
     *
     * @param defaultSortParam default sort param
     * @param typeClass data type that is provided
     */
    @SuppressWarnings("unchecked")
    public SenseDataProvider(Class<?> typeClass, SortParam<String> defaultSortParam) {
        this.defaultSortParam = defaultSortParam;
        this.typeClass = typeClass;

        try {
            state = new SenseWrapper();
        } catch (IllegalArgumentException | SecurityException e) {
            e.printStackTrace();
        }
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
    public Iterator<SenseWrapper> iterator(long first, long count) {
        EntityManager entityManager = EntityManagerListener.createEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<SenseWrapper> criteriaQuery = criteriaBuilder.createQuery(SenseWrapper.class);
        Root<Lemma> lemma = criteriaQuery.from(Lemma.class);
        EntityTransaction transaction = entityManager.getTransaction();

        if (!(getSort() instanceof SortParam)) {
            setSort(defaultSortParam);
        }

        ListJoin<Lemma, Sense> senses = lemma.joinList("senses", JoinType.LEFT);
        Selection<SenseWrapper> selection = criteriaBuilder
                .construct(SenseWrapper.class, lemma, senses.as(Sense.class));
        Expression<Boolean> restriction = getRestriction(criteriaBuilder, lemma, senses);
        List<Order> order = getOrder(criteriaBuilder, lemma, senses);
        TypedQuery<SenseWrapper> typedQuery = null;

        if (restriction == null) {
            typedQuery = entityManager.createQuery(criteriaQuery.select(selection).orderBy(order))
                    .setFirstResult((int) first).setMaxResults((int) count);
        } else {
            typedQuery = entityManager.createQuery(criteriaQuery.select(selection).where(restriction).orderBy(order))
                    .setFirstResult((int) first).setMaxResults((int) count);
        }

        try {
            transaction.begin();
            Iterator<SenseWrapper> iterator = typedQuery.getResultList().iterator();
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
    public IModel<SenseWrapper> model(SenseWrapper object) {
        return new AbstractReadOnlyModel<SenseWrapper>() {
            /**
             * Determines if a deserialized file is compatible with this class.
             */
            private static final long serialVersionUID = 1L;

            /**
             * Returns the model object.
             *
             * @return The model object.
             */
            public SenseWrapper getObject() {
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
        Root<Lemma> lemma = criteriaQuery.from(Lemma.class);
        EntityTransaction transaction = entityManager.getTransaction();

        ListJoin<Lemma, Sense> senses = lemma.joinList("senses", JoinType.LEFT);
        Expression<Boolean> restriction = getRestriction(criteriaBuilder, lemma, senses);
        TypedQuery<Long> typedQuery = null;

        if (restriction == null) {
            typedQuery = entityManager.createQuery(criteriaQuery.select(criteriaBuilder.count(lemma)));
        } else {
            typedQuery = entityManager.createQuery(criteriaQuery.select(criteriaBuilder.count(lemma))
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
    public SenseWrapper getFilterState() {
        return state;
    }

    /**
     * Sets the state defined by a filter form.
     *
     * @param state state object
     */
    @Override
    public void setFilterState(SenseWrapper state) {
        this.state = state;
        this.filter = null;
    }

    /**
     * Returns automatically created restrictions for a filter state.
     *
     * @param criteriaBuilder contructor for criteria queries
     * @param lemma query root referencing lemmata
     * @param senses list join for referencing senses
     * @return An expression of type boolean, or null.
     */
    protected Expression<Boolean> getFilterStateRestriction(CriteriaBuilder criteriaBuilder, Root<Lemma> lemma,
                                                            ListJoin<Lemma, Sense> senses) {
        if (state instanceof Object) {
            List<Predicate> predicateList = new ArrayList<Predicate>();

            if (state != null) {
                String lemmaString = state.getLemmaString();
                String senseString = state.getSenseString();

                if (lemmaString instanceof String) {
                    Expression<String> expression = criteriaBuilder.upper(lemma.get("name"));
                    String filter = lemmaString + "%".toUpperCase();
                    predicateList.add(criteriaBuilder.like(expression, filter));
                }

                if (senseString instanceof String) {
                    Expression<String> expression = criteriaBuilder.upper(senses.get("meaning"));
                    String filter = senseString + "%".toUpperCase();
                    predicateList.add(criteriaBuilder.like(expression, filter));
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
     * @param lemma query root referencing lemmata
     * @param senses list join for referencing senses
     * @return An expression of type boolean, or null.
     */
    protected Expression<Boolean> getFilterStringRestriction(CriteriaBuilder criteriaBuilder, Root<Lemma> lemma,
                                                             ListJoin<Lemma, Sense> senses) {
        if (filter instanceof String) {
            return criteriaBuilder.or(
                    criteriaBuilder.like(criteriaBuilder.upper(lemma.get("name")), filter + "%".toUpperCase()),
                    criteriaBuilder.like(criteriaBuilder.upper(senses.get("meaning")), filter + "%".toUpperCase()));
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
     * Returns filter string or filter state restrictions.
     *
     * @param criteriaBuilder contructor for criteria queries
     * @param lemma query root referencing lemmata
     * @param senses list join for referencing senses
     * @return An expression of type boolean, or null.
     */
    protected Expression<Boolean> getRestriction(CriteriaBuilder criteriaBuilder, Root<Lemma> lemma,
                                                 ListJoin<Lemma, Sense> senses) {
        Expression<Boolean> filterStateRestriction = getFilterStateRestriction(criteriaBuilder, lemma, senses);
        Expression<Boolean> filterStringRestriction = getFilterStringRestriction(criteriaBuilder, lemma, senses);

        if (filterStateRestriction instanceof Expression) {
            return criteriaBuilder.and(
                    criteriaBuilder.isNull(lemma.get("replacement")),
                    filterStateRestriction);
        } else if (filterStringRestriction instanceof Expression) {
            return criteriaBuilder.and(
                    criteriaBuilder.isNull(lemma.get("replacement")),
                    filterStringRestriction);
        }

        return criteriaBuilder.isNull(lemma.get("replacement"));
    }

    /**
     * Returns an order matching sort properties.
     *
     * @param criteriaBuilder contructor for criteria queries
     * @param lemma query root referencing lemmata
     * @param senses list join for referencing senses
     * @return An order object.
     */
    protected List<Order> getOrder(CriteriaBuilder criteriaBuilder, Root<Lemma> lemma, ListJoin<Lemma, Sense> senses) {
        String property = getSort().getProperty();
        Boolean isAscending = getSort().isAscending();
        List<Order> orderList = new ArrayList<Order>();

        if (isAscending) {
            if (property.equals("lemma")) {
                orderList.add(criteriaBuilder.asc(lemma.get("name")));
                orderList.add(criteriaBuilder.asc(senses.get("parentPosition")));
                orderList.add(criteriaBuilder.asc(senses.get("childPosition")));
            } else if (property.equals("sense")) {
                orderList.add(criteriaBuilder.asc(senses.get("meaning")));
                orderList.add(criteriaBuilder.asc(lemma.get("name")));
            }
        } else {
            if (property.equals("lemma")) {
                orderList.add(criteriaBuilder.desc(lemma.get("name")));
                orderList.add(criteriaBuilder.asc(senses.get("parentPosition")));
                orderList.add(criteriaBuilder.asc(senses.get("childPosition")));
            } else if (property.equals("sense")) {
                orderList.add(criteriaBuilder.desc(senses.get("meaning")));
                orderList.add(criteriaBuilder.asc(lemma.get("name")));
            }
        }

        return orderList;
    }
}
