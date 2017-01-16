package lemming.api.data;

import lemming.api.lemma.Lemma;
import lemming.api.pos.Pos;
import org.apache.wicket.model.ResourceModel;

import javax.persistence.criteria.*;

/**
 * A helper class for criteria restrictions.
 */
public final class CriteriaHelper {

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
     * @param filter string filter
     * @param typeClass data type
     * @return An expression of type boolean, or null.
     */
    public static Expression<Boolean> getFilterStringRestriction(CriteriaBuilder criteriaBuilder, Root<?> root,
                                                                 String filter, Class<?> typeClass) {
        if (typeClass.equals(Lemma.class)) {
            return getLemmaFilterStringRestriction(criteriaBuilder, root, filter);
        } else if (typeClass.equals(Pos.class)) {
            return getPosFilterStringRestriction(criteriaBuilder, root, filter);
        }

        return null;
    }
}
