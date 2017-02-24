package lemming.context;

import lemming.data.IDao;
import lemming.lemma.Lemma;
import lemming.pos.Pos;
import lemming.sense.Sense;

import java.util.List;

/**
 * Defines a context DAO by extending interface IDao.
 */
public interface IContextDao extends IDao<Context> {
    /**
     * Makes multiple context instances managed and persistent.
     *
     * @param contexts context instances
     */
    void batchPersist(List<Context> contexts);

    /**
     * Returns a matching context for a given keyword.
     *
     * @param keyword keyword of a context
     * @return A matching context, or null.
     */
    Context findByKeyword(String keyword);

    /**
     * Returns a list of matching contexts for a given keyword substring.
     *
     * @param substring substring of a context keyword
     * @return A list of matching contexts.
     */
    List<Context> findByKeywordStart(String substring);

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
}
