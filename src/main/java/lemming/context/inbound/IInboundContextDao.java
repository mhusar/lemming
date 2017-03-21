package lemming.context.inbound;

import lemming.data.IDao;

import java.util.List;

/**
 * Defines an inbound context DAO by extending interface IDao.
 */
public interface IInboundContextDao extends IDao<InboundContext> {
    /**
     * Makes multiple context instances managed and persistent.
     *
     * @param contexts context instances
     */
    void batchPersist(List<InboundContext> contexts);

    /**
     * Returns a list of matching contexts for a given keyword.
     *
     * @param keyword keyword of a context
     * @return A list of matching contexts.
     */
    List<InboundContext> findByKeyword(String keyword);

    /**
     * Returns a list of matching contexts for a given keyword substring.
     *
     * @param substring substring of a context keyword
     * @return A list of matching contexts.
     */
    List<InboundContext> findByKeywordStart(String substring);

    /**
     * Returns a list of matching contexts for a given location.
     *
     * @param location location of a context
     * @return A list of matching contexts.
     */
    List<InboundContext> findByLocation(String location);

    /**
     * Returns a list of matching contexts for a given location substring.
     *
     * @param substring substring of a context location
     * @return A list of matching contexts.
     */
    List<InboundContext> findByLocationStart(String substring);

    /**
     * Returns a list of inbound context summaries which hold data about groups of inbound contexts.
     *
     * @return A list of inbound context summaries.
     */
    List<InboundContextSummary> getSummaries();

    /**
     * Removes inbound contexts matching an inbound context summary.
     *
     * @param summary inbound context summary
     */
    void removeBySummary(InboundContextSummary summary);
}
