package lemming.context.verfication;

import lemming.data.IDao;

import java.util.List;

/**
 * Defines an unverified context DAO by extending interface IDao.
 */
public interface IUnverifiedContextDao extends IDao<UnverifiedContext> {
    /**
     * Makes multiple context instances managed and persistent.
     *
     * @param contexts context instances
     */
    void batchPersist(List<UnverifiedContext> contexts);

    /**
     * Returns a list of matching contexts for a given keyword.
     *
     * @param keyword keyword of a context
     * @return A list of matching contexts.
     */
    List<UnverifiedContext> findByKeyword(String keyword);

    /**
     * Returns a list of matching contexts for a given keyword substring.
     *
     * @param substring substring of a context keyword
     * @return A list of matching contexts.
     */
    List<UnverifiedContext> findByKeywordStart(String substring);

    /**
     * Returns a list of matching contexts for a given location.
     *
     * @param location location of a context
     * @return A list of matching contexts.
     */
    List<UnverifiedContext> findByLocation(String location);

    /**
     * Returns a list of matching contexts for a given location substring.
     *
     * @param substring substring of a context location
     * @return A list of matching contexts.
     */
    List<UnverifiedContext> findByLocationStart(String substring);

    /**
     * Returns a list of unverified context overviews.
     *
     * @return A list of unverified context overviews.
     */
    List<UnverifiedContextOverview> getOverviews();

    /**
     * Removes unverified contexts matching an unverified context overview.
     *
     * @param overview unverified context overview
     */
    void removeByOverview(UnverifiedContextOverview overview);
}
