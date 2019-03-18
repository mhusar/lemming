package lemming.context.inbound;

import lemming.context.Context;
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
    @SuppressWarnings("unused")
    void batchPersist(List<InboundContext> contexts);

    /**
     * Finds complements of inbound contexts.
     *
     * @param contexts list of inbound contexts
     * @return A list of contexts or null.
     */
    List<Context> findComplements(List<InboundContext> contexts);
}
