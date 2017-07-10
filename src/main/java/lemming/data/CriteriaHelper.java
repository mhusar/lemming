package lemming.data;

import lemming.context.Context;
import lemming.context.ContextType;
import lemming.lemma.Lemma;
import lemming.pos.Pos;
import lemming.sense.Sense;
import org.apache.wicket.model.ResourceModel;

import javax.persistence.criteria.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A helper class for criteria restrictions.
 */
public final class CriteriaHelper {
    /**
     * Matches a filter string against a context type.
     *
     * @param filter string filter
     * @return A context type, or null.
     */
    private static ContextType.Type matchContextType(String filter) {
        String rubricString = new ResourceModel("Type.RUBRIC").getObject();
        String segmentString = new ResourceModel("Type.SEGMENT").getObject();

        if (rubricString.toUpperCase().startsWith(filter.toUpperCase())) {
            return ContextType.Type.RUBRIC;
        } else if (segmentString.toUpperCase().startsWith(filter.toUpperCase())) {
            return ContextType.Type.SEGMENT;
        }

        return null;
    }

    /**
     * Matches a filter string against a lemma source type.
     *
     * @param filter string filter
     * @return A lemma source type, or null.
     */
    private static Source.LemmaType matchLemmaSourceType(String filter) {
        String tlString = new ResourceModel("LemmaType.TL").getObject();
        String userString = new ResourceModel("LemmaType.USER").getObject();


        if (tlString.toUpperCase().startsWith(filter.toUpperCase())) {
            return Source.LemmaType.TL;
        } else if (userString.toUpperCase().startsWith(filter.toUpperCase())) {
            return Source.LemmaType.USER;
        }

        return null;
    }

    /**
     * Matches a filter string against a pos source type.
     *
     * @param filter string filter
     * @return A pos source type, or null.
     */
    private static Source.PosType matchPosSourceType(String filter) {
        String deafString = new ResourceModel("PosType.DEAF").getObject();
        String userString = new ResourceModel("PosType.USER").getObject();


        if (deafString.toUpperCase().startsWith(filter.toUpperCase())) {
            return Source.PosType.DEAF;
        } else if (userString.toUpperCase().startsWith(filter.toUpperCase())) {
            return Source.PosType.USER;
        }

        return null;
    }

    /**
     * Returns automatically created context restrictions for a string filter.
     *
     * @param criteriaBuilder contructor for criteria queries
     * @param root query root referencing entities
     * @param filter string filter
     * @return An expression of type boolean, or null.
     */
    private static Expression<Boolean> getContextFilterStringRestriction(CriteriaBuilder criteriaBuilder,
                                                                         Root<?> root, String filter) {
        // deactivate filtering by context type
        ContextType.Type type = null;

        if (type != null) {
            return criteriaBuilder.or(
                    criteriaBuilder.like(root.get("location"), filter + "%"),
                    criteriaBuilder.equal(root.get("type"), type),
                    criteriaBuilder.like(root.get("preceding"), filter + "%"),
                    criteriaBuilder.like(root.get("keyword"), filter + "%"),
                    criteriaBuilder.like(root.get("following"), filter + "%"),
                    criteriaBuilder.like(root.get("lemmaString"), filter + "%"),
                    criteriaBuilder.like(root.get("posString"), filter + "%")
            );
        } else {
            return criteriaBuilder.or(
                    criteriaBuilder.like(root.get("location"), filter + "%"),
                    criteriaBuilder.like(root.get("preceding"), filter + "%"),
                    criteriaBuilder.like(root.get("keyword"), filter + "%"),
                    criteriaBuilder.like(root.get("following"), filter + "%"),
                    criteriaBuilder.like(root.get("lemmaString"), filter + "%"),
                    criteriaBuilder.like(root.get("posString"), filter + "%")
            );
        }
    }

    /**
     * Returns automatically created lemma restrictions for a string filter.
     *
     * @param criteriaBuilder contructor for criteria queries
     * @param root query root referencing entities
     * @param filter string filter
     * @return An expression of type boolean, or null.
     */
    private static Expression<Boolean> getLemmaFilterStringRestriction(CriteriaBuilder criteriaBuilder, Root<?> root,
                                                                       String filter) {
        Source.LemmaType source = CriteriaHelper.matchLemmaSourceType(filter);

        if (source != null) {
            return criteriaBuilder.or(
                    criteriaBuilder.like(root.get("name"), filter + "%"),
                    criteriaBuilder.like(root.get("replacementString"), filter + "%"),
                    criteriaBuilder.like(root.get("posString"), filter + "%"),
                    criteriaBuilder.equal(root.get("source"), source),
                    criteriaBuilder.like(root.get("reference"), filter + "%"));
        } else {
            return criteriaBuilder.or(
                    criteriaBuilder.like(root.get("name"), filter + "%"),
                    criteriaBuilder.like(root.get("replacementString"), filter + "%"),
                    criteriaBuilder.like(root.get("posString"), filter + "%"),
                    criteriaBuilder.like(root.get("reference"), filter + "%"));
        }
    }

    /**
     * Returns automatically created pos restrictions for a string filter.
     *
     * @param criteriaBuilder contructor for criteria queries
     * @param root query root referencing entities
     * @param filter string filter
     * @return An expression of type boolean, or null.
     */
    private static Expression<Boolean> getPosFilterStringRestriction(CriteriaBuilder criteriaBuilder, Root<?> root,
                                                                     String filter) {
        Source.PosType source = CriteriaHelper.matchPosSourceType(filter);

        if (source != null) {
            return criteriaBuilder.or(
                    criteriaBuilder.like(root.get("name"), filter + "%"),
                    criteriaBuilder.equal(root.get("source"), source));
        } else {
            return criteriaBuilder.like(root.get("name"), filter + "%");
        }
    }

    /**
     * Returns automatically created sense restrictions for a string filter.
     *
     * @param criteriaBuilder contructor for criteria queries
     * @param root query root referencing entities
     * @param filter string filter
     * @return An expression of type boolean, or null.
     */
    private static Expression<Boolean> getSenseFilterStringRestriction(CriteriaBuilder criteriaBuilder, Root<?> root,
                                                                       String filter) {
        return criteriaBuilder.or(
                criteriaBuilder.like(root.get("meaning"), filter + "%"),
                criteriaBuilder.like(root.get("lemmaString"), filter + "%"));
    }

    /**
     * Returns automatically created restrictions for a string filter.
     *
     * @param criteriaBuilder contructor for criteria queries
     * @param root query root referencing entities
     * @param joins map of joins
     * @param filter string filter
     * @param typeClass data type
     * @return An expression of type boolean, or null.
     */
    public static Expression<Boolean> getFilterStringRestriction(CriteriaBuilder criteriaBuilder, Root<?> root,
                                                                 Map<String,Join<?,?>> joins, String filter,
                                                                 Class<?> typeClass) {
        if (typeClass.equals(Context.class)) {
            return getContextFilterStringRestriction(criteriaBuilder, root, filter);
        } else if (typeClass.equals(Lemma.class)) {
            return getLemmaFilterStringRestriction(criteriaBuilder, root, filter);
        } else if (typeClass.equals(Pos.class)) {
            return getPosFilterStringRestriction(criteriaBuilder, root, filter);
        } else if (typeClass.equals(Sense.class)) {
            return getSenseFilterStringRestriction(criteriaBuilder, root, filter);
        }

        return null;
    }

    /**
     * Returns an automatically created list of order objects for context ordering.
     *
     * @param criteriaBuilder contructor for criteria queries
     * @param root query root referencing entities
     * @param property sort property
     * @param isAscending sort direction
     * @return A list of order objects.
     */
    private static List<Order> getContextOrder(CriteriaBuilder criteriaBuilder, Root<?> root, String property,
                                             Boolean isAscending) {
        List<Order> orderList = new ArrayList<Order>();

        if (isAscending) {
            switch (property) {
                case "lemmaString":
                    orderList.add(criteriaBuilder.asc(root.get("lemmaString")));
                    orderList.add(criteriaBuilder.asc(root.get("keyword")));
                    break;
                case "posString":
                    orderList.add(criteriaBuilder.asc(root.get("posString")));
                    orderList.add(criteriaBuilder.asc(root.get("lemmaString")));
                    break;
                case "location":
                    orderList.add(criteriaBuilder.asc(root.get("location")));
                    orderList.add(criteriaBuilder.asc(root.get("keyword")));
                    break;
                case "preceding":
                    orderList.add(criteriaBuilder.asc(root.get("preceding")));
                    orderList.add(criteriaBuilder.asc(root.get("keyword")));
                    break;
                case "keyword":
                    orderList.add(criteriaBuilder.asc(root.get("keyword")));
                    orderList.add(criteriaBuilder.asc(root.get("following")));
                    break;
                case "following":
                    orderList.add(criteriaBuilder.asc(root.get("following")));
                    orderList.add(criteriaBuilder.asc(root.get("keyword")));
                    break;
            }
        } else {
            switch (property) {
                case "lemmaString":
                    orderList.add(criteriaBuilder.desc(root.get("lemmaString")));
                    orderList.add(criteriaBuilder.asc(root.get("keyword")));
                    break;
                case "posString":
                    orderList.add(criteriaBuilder.desc(root.get("posString")));
                    orderList.add(criteriaBuilder.asc(root.get("lemmaString")));
                    break;
                case "location":
                    orderList.add(criteriaBuilder.desc(root.get("location")));
                    orderList.add(criteriaBuilder.asc(root.get("keyword")));
                    break;
                case "preceding":
                    orderList.add(criteriaBuilder.desc(root.get("preceding")));
                    orderList.add(criteriaBuilder.asc(root.get("keyword")));
                    break;
                case "keyword":
                    orderList.add(criteriaBuilder.desc(root.get("keyword")));
                    orderList.add(criteriaBuilder.asc(root.get("following")));
                    break;
                case "following":
                    orderList.add(criteriaBuilder.desc(root.get("following")));
                    orderList.add(criteriaBuilder.asc(root.get("keyword")));
                    break;
            }
        }

        return orderList;
    }

    /**
     * Returns an automatically created list of order objects for sense ordering.
     *
     * @param criteriaBuilder contructor for criteria queries
     * @param root query root referencing entities
     * @param property sort property
     * @param isAscending sort direction
     * @return A list of order objects.
     */
    private static List<Order> getSenseOrder(CriteriaBuilder criteriaBuilder, Root<?> root, String property,
                                             Boolean isAscending) {
        List<Order> orderList = new ArrayList<Order>();

        if (isAscending) {
            switch (property) {
                case "meaning":
                    orderList.add(criteriaBuilder.asc(root.get("meaning")));
                    orderList.add(criteriaBuilder.asc(root.get("lemmaString")));
                    break;
                case "lemmaString":
                    orderList.add(criteriaBuilder.asc(root.get("lemmaString")));
                    orderList.add(criteriaBuilder.asc(root.get("parentPosition")));
                    orderList.add(criteriaBuilder.asc(root.get("childPosition")));
                    break;
            }
        } else {
            switch (property) {
                case "meaning":
                    orderList.add(criteriaBuilder.desc(root.get("meaning")));
                    orderList.add(criteriaBuilder.asc(root.get("lemmaString")));
                    break;
                case "lemmaString":
                    orderList.add(criteriaBuilder.desc(root.get("lemmaString")));
                    orderList.add(criteriaBuilder.asc(root.get("parentPosition")));
                    orderList.add(criteriaBuilder.asc(root.get("childPosition")));
                    break;
            }
        }

        return orderList;
    }

    /**
     * Returns an automatically created list of order objects for a property string.
     *
     * @param criteriaBuilder contructor for criteria queries
     * @param root query root referencing entities
     * @param joins map of joins
     * @param property sort property
     * @param isAscending sort direction
     * @param typeClass data type
     * @return A list of order objects.
     */
    public static List<Order> getOrder(CriteriaBuilder criteriaBuilder, Root<?> root, Map<String,Join<?,?>> joins,
                                 String property, Boolean isAscending, Class<?> typeClass) {
        List<Order> orderList = new ArrayList<Order>();
        String[] splitProperty = property.split("\\.");
        Expression<String> expression;

        if (typeClass.equals(Context.class)) {
            return getContextOrder(criteriaBuilder, root, property, isAscending);
        } else if (typeClass.equals(Sense.class)) {
            return getSenseOrder(criteriaBuilder, root, property, isAscending);
        }

        if (Array.getLength(splitProperty) == 2) {
            Join<?,?> join = joins.get(splitProperty[0]);

            if (join != null) {
                expression = join.get(splitProperty[1]);
            } else {
                throw new IllegalStateException("Join for sort property " + property + " is missing.");
            }
        } else {
            expression = root.get(property);
        }

        if (isAscending) {
            orderList.add(criteriaBuilder.asc(expression));
        } else {
            orderList.add(criteriaBuilder.desc(expression));
        }

        return orderList;
    }

    /**
     * Returns automatically created joins for some classes.
     *
     * @param root query root referencing entities
     * @param typeClass data type
     * @return A map of joins, or null.
     */
    public static Map<String,Join<?,?>> getJoins(Root<?> root, Class<?> typeClass) {
        return null;
    }
}
