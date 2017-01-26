package lemming.api.context;

import lemming.api.data.IDao;

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
     * Returns a list of matching contexts for a given substring.
     *
     * @param substring substring of a context name
     * @return A list of matching contexts.
     */
    List<Context> findByKeywordStart(String substring);
}
