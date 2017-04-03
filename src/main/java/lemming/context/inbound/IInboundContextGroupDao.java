package lemming.context.inbound;

import lemming.data.IDao;

import java.util.List;

/**
 * Defines an inbound context group DAO by extending interface IDao.
 */
public interface IInboundContextGroupDao extends IDao<InboundContextGroup> {
    /**
     * Returns a list of inbound contexts of a context group.
     *
     * @param contextGroup a group of inbound contexts
     * @return A list of inbound contexts of a context group.
     */
    List<InboundContext> getContexts(InboundContextGroup contextGroup);

    /**
     * Returns the location where contexts are beginning.
     *
     * @param contextGroup a group of inbound contexts
     * @return Begin location string.
     */
    String getBeginLocation(InboundContextGroup contextGroup);

    /**
     * Returns the location where contexts are ending.
     *
     * @param contextGroup a group of inbound contexts
     * @return End location string.
     */
    String getEndLocation(InboundContextGroup contextGroup);
}
