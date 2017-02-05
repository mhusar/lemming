package lemming.api.context;

import lemming.api.data.EntityManagerListener;
import lemming.api.lemma.Lemma;
import lemming.api.pos.Pos;
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
 * Provides selectable context data for data table views.
 */
public final class SelectableContextDataProvider extends SortableDataProvider<SelectableContextWrapper, String>
        implements IFilterStateLocator<SelectableContextWrapper> {
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
    protected SelectableContextWrapper state;

    /**
     * The state defined a string filter.
     */
    protected String filter;

    /**
     * Creates a new data provider.
     *
     * @param defaultSortParam default sort param
     * @param typeClass data type that is provided
     */
    @SuppressWarnings("unchecked")
    public SelectableContextDataProvider(Class<?> typeClass, SortParam<String> defaultSortParam) {
        this.defaultSortParam = defaultSortParam;
        this.typeClass = typeClass;

        try {
            state = new SelectableContextWrapper();
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
    public Iterator<SelectableContextWrapper> iterator(long first, long count) {
        EntityManager entityManager = EntityManagerListener.createEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<SelectableContextWrapper> criteriaQuery = criteriaBuilder
                .createQuery(SelectableContextWrapper.class);
        Root<Context> context = criteriaQuery.from(Context.class);
        EntityTransaction transaction = entityManager.getTransaction();

        if (!(getSort() instanceof SortParam)) {
            setSort(defaultSortParam);
        }

        Join<Context, Lemma> lemma = context.join("lemma", JoinType.LEFT);
        Join<Context, Pos> pos = context.join("pos", JoinType.LEFT);
        Selection<SelectableContextWrapper> selection = criteriaBuilder
                .construct(SelectableContextWrapper.class, context);
        Expression<Boolean> restriction = getRestriction(criteriaBuilder, context, lemma, pos);
        List<Order> order = getOrder(criteriaBuilder, context, lemma, pos);
        TypedQuery<SelectableContextWrapper> typedQuery = null;

        if (restriction == null) {
            typedQuery = entityManager.createQuery(criteriaQuery.select(selection).orderBy(order))
                    .setFirstResult((int) first).setMaxResults((int) count);
        } else {
            typedQuery = entityManager.createQuery(criteriaQuery.select(selection).where(restriction).orderBy(order))
                    .setFirstResult((int) first).setMaxResults((int) count);
        }

        try {
            transaction.begin();
            Iterator<SelectableContextWrapper> iterator = typedQuery.getResultList().iterator();
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
    public IModel<SelectableContextWrapper> model(SelectableContextWrapper object) {
        return new AbstractReadOnlyModel<SelectableContextWrapper>() {
            /**
             * Determines if a deserialized file is compatible with this class.
             */
            private static final long serialVersionUID = 1L;

            /**
             * Returns the model object.
             *
             * @return The model object.
             */
            public SelectableContextWrapper getObject() {
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
        Root<Context> context = criteriaQuery.from(Context.class);
        EntityTransaction transaction = entityManager.getTransaction();

        Join<Context, Lemma> lemma = context.join("lemma", JoinType.LEFT);
        Join<Context, Pos> pos = context.join("pos", JoinType.LEFT);
        Expression<Boolean> restriction = getRestriction(criteriaBuilder, context, lemma, pos);
        TypedQuery<Long> typedQuery = null;

        if (restriction == null) {
            typedQuery = entityManager.createQuery(criteriaQuery.select(criteriaBuilder.count(context)));
        } else {
            typedQuery = entityManager.createQuery(criteriaQuery.select(criteriaBuilder.count(context))
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
    public SelectableContextWrapper getFilterState() {
        return state;
    }

    /**
     * Sets the state defined by a filter form.
     *
     * @param state state object
     */
    @Override
    public void setFilterState(SelectableContextWrapper state) {
        this.state = state;
        this.filter = null;
    }

    /**
     * Returns automatically created restrictions for a filter state.
     *
     * @param criteriaBuilder contructor for criteria queries
     * @param context query root referencing contexts
     * @param lemma join for referencing a lemma
     * @param pos join for referencing a pos
     * @return An expression of type boolean, or null.
     */
    protected Expression<Boolean> getFilterStateRestriction(CriteriaBuilder criteriaBuilder, Root<Context> context,
                                                            Join<Context, Lemma> lemma, Join<Context, Pos> pos) {
        if (state instanceof Object) {
            List<Predicate> predicateList = new ArrayList<Predicate>();

            if (state != null) {
                String lemmaString = state.getLemmaString();
                String posString = state.getPosString();
                String locationString = state.getLocationString();
                String precedingString = state.getPrecedingString();
                String keywordString = state.getKeywordString();
                String followingString = state.getFollowingString();

                if (lemmaString instanceof String) {
                    Expression<String> expression = criteriaBuilder.upper(lemma.get("name"));
                    String filter = lemmaString + "%".toUpperCase();
                    predicateList.add(criteriaBuilder.like(expression, filter));
                }

                if (posString instanceof String) {
                    Expression<String> expression = criteriaBuilder.upper(pos.get("name"));
                    String filter = posString + "%".toUpperCase();
                    predicateList.add(criteriaBuilder.like(expression, filter));
                }

                if (locationString instanceof String) {
                    Expression<String> expression = criteriaBuilder.upper(context.get("location"));
                    String filter = locationString + "%".toUpperCase();
                    predicateList.add(criteriaBuilder.like(expression, filter));
                }

                if (precedingString instanceof String) {
                    Expression<String> expression = criteriaBuilder.upper(context.get("preceding"));
                    String filter = precedingString + "%".toUpperCase();
                    predicateList.add(criteriaBuilder.like(expression, filter));
                }

                if (keywordString instanceof String) {
                    Expression<String> expression = criteriaBuilder.upper(context.get("keyword"));
                    String filter = keywordString + "%".toUpperCase();
                    predicateList.add(criteriaBuilder.like(expression, filter));
                }

                if (followingString instanceof String) {
                    Expression<String> expression = criteriaBuilder.upper(context.get("following"));
                    String filter = followingString + "%".toUpperCase();
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
     * @param context query root referencing contexts
     * @param lemma join for referencing a lemma
     * @param pos join for referencing a pos
     * @return An expression of type boolean, or null.
     */
    protected Expression<Boolean> getFilterStringRestriction(CriteriaBuilder criteriaBuilder, Root<Context> context,
                                                             Join<Context, Lemma> lemma, Join<Context, Pos> pos) {
        if (filter instanceof String) {
            return criteriaBuilder.or(
                    criteriaBuilder.like(criteriaBuilder.upper(lemma.get("name")), filter + "%".toUpperCase()),
                    criteriaBuilder.like(criteriaBuilder.upper(pos.get("name")), filter + "%".toUpperCase()),
                    criteriaBuilder.like(criteriaBuilder.upper(context.get("location")), filter + "%".toUpperCase()),
                    criteriaBuilder.like(criteriaBuilder.upper(context.get("preceding")), filter + "%".toUpperCase()),
                    criteriaBuilder.like(criteriaBuilder.upper(context.get("keyword")), filter + "%".toUpperCase()),
                    criteriaBuilder.like(criteriaBuilder.upper(context.get("following")), filter + "%".toUpperCase()));
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
     * @param context query root referencing contexts
     * @param lemma join for referencing a lemma
     * @param pos join for referencing a pos
     * @return An expression of type boolean, or null.
     */
    protected Expression<Boolean> getRestriction(CriteriaBuilder criteriaBuilder, Root<Context> context,
                                                 Join<Context, Lemma> lemma, Join<Context, Pos> pos) {
        Expression<Boolean> filterStateRestriction = getFilterStateRestriction(criteriaBuilder, context, lemma, pos);
        Expression<Boolean> filterStringRestriction = getFilterStringRestriction(criteriaBuilder, context, lemma, pos);

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
     * @param context query root referencing contexts
     * @param lemma join for referencing a lemma
     * @param pos join for referencing a pos
     * @return An order object.
     */
    protected List<Order> getOrder(CriteriaBuilder criteriaBuilder, Root<Context> context, Join<Context, Lemma> lemma,
                                   Join<Context, Pos> pos) {
        String property = getSort().getProperty();
        Boolean isAscending = getSort().isAscending();
        List<Order> orderList = new ArrayList<Order>();

        if (isAscending) {
            if (property.equals("lemma")) {
                orderList.add(criteriaBuilder.asc(lemma.get("name")));
            } else if (property.equals("pos")) {
                orderList.add(criteriaBuilder.asc(pos.get("name")));
            } else if (property.equals("location")) {
                orderList.add(criteriaBuilder.asc(context.get("location")));
            } else if (property.equals("preceding")) {
                orderList.add(criteriaBuilder.asc(context.get("preceding")));
                orderList.add(criteriaBuilder.asc(context.get("keyword")));
            } else if (property.equals("keyword")) {
                orderList.add(criteriaBuilder.asc(context.get("keyword")));
                orderList.add(criteriaBuilder.asc(context.get("preceding")));
            } else if (property.equals("following")) {
                orderList.add(criteriaBuilder.asc(context.get("following")));
                orderList.add(criteriaBuilder.asc(context.get("keyword")));
            }
        } else {
            if (property.equals("lemma")) {
                orderList.add(criteriaBuilder.desc(lemma.get("name")));
            } else if (property.equals("pos")) {
                orderList.add(criteriaBuilder.desc(pos.get("name")));
            } else if (property.equals("location")) {
                orderList.add(criteriaBuilder.desc(context.get("location")));
            } else if (property.equals("preceding")) {
                orderList.add(criteriaBuilder.desc(context.get("preceding")));
                orderList.add(criteriaBuilder.asc(context.get("keyword")));
            } else if (property.equals("keyword")) {
                orderList.add(criteriaBuilder.desc(context.get("keyword")));
                orderList.add(criteriaBuilder.asc(context.get("preceding")));
            } else if (property.equals("following")) {
                orderList.add(criteriaBuilder.desc(context.get("following")));
                orderList.add(criteriaBuilder.asc(context.get("keyword")));
            }
        }

        return orderList;
    }
}
