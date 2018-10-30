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
    @SuppressWarnings("unused")
    void batchPersist(List<InboundContext> contexts);

    /**
     * Finds the ancestor of an inbound context with the same package and location.
     *
     * @param context an inbound context
     * @return An inbound context or null.
     */
    InboundContext findAncestor(InboundContext context);

    /**
     * Finds the successor of an inbound context with the same package and location.
     *
     * @param context an inbound context
     * @return An inbound context or null.
     */
    InboundContext findSuccessor(InboundContext context);
}
