package lemming.data;

import lemming.context.Context;
import lemming.context.ContextType;
import lemming.lemma.Lemma;
import lemming.pos.Pos;
import org.apache.wicket.model.ResourceModel;

import javax.persistence.criteria.*;
import java.lang.reflect.Array;
import java.util.HashMap;
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
     * @param joins map of joins
     * @param filter string filter
     * @return An expression of type boolean, or null.
     */
    private static Expression<Boolean> getContextFilterStringRestriction(CriteriaBuilder criteriaBuilder,
                                                                         Root<?> root, Map<String,Join<?,?>> joins,
                                                                         String filter) {
        ContextType.Type type = matchContextType(filter);
        Join<Context,Lemma> lemma = (Join<Context,Lemma>) joins.get("lemma");
        Join<Context,Pos> pos = (Join<Context,Pos>) joins.get("pos");

        if (type != null) {
            return criteriaBuilder.or(
                    criteriaBuilder.like(criteriaBuilder.upper(lemma.get("name")), filter + "%".toUpperCase()),
                    criteriaBuilder.like(criteriaBuilder.upper(pos.get("name")), filter + "%".toUpperCase()),
                    criteriaBuilder.like(criteriaBuilder.upper(root.get("location")), filter + "%".toUpperCase()),
                    criteriaBuilder.like(criteriaBuilder.upper(root.get("preceding")), filter + "%".toUpperCase()),
                    criteriaBuilder.like(criteriaBuilder.upper(root.get("keyword")), filter + "%".toUpperCase()),
                    criteriaBuilder.like(criteriaBuilder.upper(root.get("following")), filter + "%".toUpperCase())
            );
        } else {
            return criteriaBuilder.or(
                    criteriaBuilder.like(criteriaBuilder.upper(lemma.get("name")), filter + "%".toUpperCase()),
                    criteriaBuilder.like(criteriaBuilder.upper(pos.get("name")), filter + "%".toUpperCase()),
                    criteriaBuilder.like(criteriaBuilder.upper(root.get("location")), filter + "%".toUpperCase()),
                    criteriaBuilder.equal(root.get("type"), type),
                    criteriaBuilder.like(criteriaBuilder.upper(root.get("preceding")), filter + "%".toUpperCase()),
                    criteriaBuilder.like(criteriaBuilder.upper(root.get("keyword")), filter + "%".toUpperCase()),
                    criteriaBuilder.like(criteriaBuilder.upper(root.get("following")), filter + "%".toUpperCase())
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
                    criteriaBuilder.like(criteriaBuilder.upper(root.get("name")), filter + "%".toUpperCase()),
                    criteriaBuilder.like(criteriaBuilder.upper(root.get("replacementString")),
                            filter + "%".toUpperCase()),
                    criteriaBuilder.like(criteriaBuilder.upper(root.get("posString")), filter + "%".toUpperCase()),
                    criteriaBuilder.equal(root.get("source"), source),
                    criteriaBuilder.like(criteriaBuilder.upper(root.get("reference")), filter + "%".toUpperCase()));
        } else {
            return criteriaBuilder.or(
                    criteriaBuilder.like(criteriaBuilder.upper(root.get("name")), filter + "%".toUpperCase()),
                    criteriaBuilder.like(criteriaBuilder.upper(root.get("replacementString")),
                            filter + "%".toUpperCase()),
                    criteriaBuilder.like(criteriaBuilder.upper(root.get("posString")), filter + "%".toUpperCase()),
                    criteriaBuilder.like(criteriaBuilder.upper(root.get("reference")), filter + "%".toUpperCase()));
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
                    criteriaBuilder.like(criteriaBuilder.upper(root.get("name")), filter + "%".toUpperCase()),
                    criteriaBuilder.equal(root.get("source"), source));
        } else {
            return criteriaBuilder.like(criteriaBuilder.upper(root.get("name")), filter + "%".toUpperCase());
        }
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
            return getContextFilterStringRestriction(criteriaBuilder, root, joins, filter);
        } else if (typeClass.equals(Lemma.class)) {
            return getLemmaFilterStringRestriction(criteriaBuilder, root, filter);
        } else if (typeClass.equals(Pos.class)) {
            return getPosFilterStringRestriction(criteriaBuilder, root, filter);
        }

        return null;
    }

    /**
     * Returns an automatically created order object for a proprty string.
     *
     * @param criteriaBuilder contructor for criteria queries
     * @param root query root referencing entities
     * @param joins map of joins
     * @param property sort property
     * @param isAscending sort direction
     * @return An order object.
     */
    public static Order getOrder(CriteriaBuilder criteriaBuilder, Root<?> root, Map<String,Join<?,?>> joins,
                                 String property, Boolean isAscending) {
        String[] splitProperty = property.split("\\.");
        Expression<String> expression;

        if (Array.getLength(splitProperty) == 2) {
            Join<?,?> join = joins.get(splitProperty[0]);
            expression = join.get(splitProperty[1]);
        } else {
            expression = root.get(property);
        }

        if (isAscending) {
            return criteriaBuilder.asc(criteriaBuilder.upper(expression));
        } else {
            return criteriaBuilder.desc(criteriaBuilder.upper(expression));
        }
    }

    /**
     * Returns automatically created joins for some classes.
     *
     * @param root query root referencing entities
     * @param typeClass data type
     * @return A map of joins, or null.
     */
    public static Map<String,Join<?,?>> getJoins(Root<?> root, Class<?> typeClass) {
        Map<String,Join<?,?>> joins = new HashMap<String,Join<?,?>>();

        if (typeClass.equals(Context.class)) {
            joins.put("lemma", root.join("lemma", JoinType.LEFT));
            joins.put("pos", root.join("pos", JoinType.LEFT));
            return joins;
        }

        return null;
    }
}
