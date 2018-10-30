package lemming.context;

import lemming.data.IDao;
import lemming.lemma.Lemma;
import lemming.pos.Pos;
import lemming.sense.Sense;

import java.util.List;

/**
 * Defines a context DAO by extending interface IDao.
 */
interface IContextDao extends IDao<Context> {
    /**
     * Makes multiple context instances managed and persistent.
     *
     * @param contexts context instances
     */
    void batchPersist(List<Context> contexts);

    /**
     * Makes multiple context instances merged.
     *
     * @param contexts context instances
     */
    void batchMerge(List<Context> contexts);

    /**
     * Returns a list of matching contexts for a given keyword.
     *
     * @param keyword keyword of a context
     * @return A list of matching contexts.
     */
    List<Context> findByKeyword(String keyword);

    /**
     * Returns a list of matching contexts for a given keyword substring.
     *
     * @param substring substring of a context keyword
     * @return A list of matching contexts.
     */
    List<Context> findByKeywordStart(String substring);

    /**
     * Returns a list of matching contexts for a given location.
     *
     * @param location location of a context
     * @return A list of matching contexts.
     */
    List<Context> findByLocation(String location);

    /**
     * Returns a list of matching contexts for a given location substring.
     *
     * @param substring substring of a context location
     * @return A list of matching contexts.
     */
    List<Context> findByLocationStart(String substring);

    /**
     * Returns a list of matching contexts for a given lemma.
     *
     * @param lemma a lemma
     * @return A list of matching contexts.
     */
    List<Context> findByLemma(Lemma lemma);

    /**
     * Returns a list of matching contexts for a given part of speech.
     *
     * @param pos a pos
     * @return A list of matching contexts.
     */
    List<Context> findByPos(Pos pos);

    /**
     * Returns a list of matching contexts for a given sense.
     *
     * @param sense a sense
     * @return A list of matching contexts.
     */
    List<Context> findBySense(Sense sense);

    /**
     * Finds the ancestor of a context with the same location.
     *
     * @param context a context
     * @return A context or null.
     */
    Context findAncestor(Context context);

    /**
     * Finds the successor of a context with the same location.
     *
     * @param context a context
     * @return A context or null.
     */
    Context findSuccessor(Context context);

    /**
     * Finds contexts before a successor.
     *
     * @param successor successor of contexts
     * @return A list of contexts.
     */
    List<Context> findBefore(Context successor);

    /**
     * Finds contexts after an ancestor.
     *
     * @param ancestor ancestor of contexts
     * @return A list of contexts.
     */
    List<Context> findAfter(Context ancestor);

    /**
     * Finds contexts between an ancestor and a successor.
     *
     * @param ancestor ancestor of contexts
     * @param successor successor of contexts
     * @return A list of contexts.
     */
    List<Context> findBetween(Context ancestor, Context successor);

    /**
     * Creates a group of contexts.
     *
     * @param members memers of a context group
     * @return A context group.
     */
    Context createGroup(List<Context> members);

    /**
     * Adds a context to a context group.
     *
     * @param group a context group
     * @param member a context
     * @return A context group.
     */
    Context addToGroup(Context group, Context member);

    /**
     * Removes a context from a context group.
     *
     * @param group a context group
     * @param member a context
     * @return A context group.
     */
    Context removeFromGroup(Context group, Context member);

    /**
     * Adds a comment to a list of contexts.
     *
     * @param contexts list of contexts
     * @param comment a comment
     * @return A list of changed contexts.
     */
    List<Context> addComment(List<Context> contexts, Comment comment);

    /**
     * Removes a comment from a context.
     *
     * @param context a context
     * @param comment a comment
     * @return A changed comment.
     */
    Context removeComment(Context context, Comment comment);
}
