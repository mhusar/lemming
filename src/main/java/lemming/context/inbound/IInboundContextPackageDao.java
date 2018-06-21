package lemming.context.inbound;

import lemming.data.IDao;

import java.util.List;

/**
 * Defines an inbound context package DAO by extending interface IDao.
 */
@SuppressWarnings("unused")
public interface IInboundContextPackageDao extends IDao<InboundContextPackage> {
    /**
     * Returns a list of inbound contexts of a context package.
     *
     * @param contextPackage a package of inbound contexts
     * @return A list of inbound contexts of a context package.
     */
    List<InboundContext> getContexts(InboundContextPackage contextPackage);

    /**
     * Returns the location where contexts are beginning.
     *
     * @param contextPackage a package of inbound contexts
     * @return Begin location string.
     */
    String getBeginLocation(InboundContextPackage contextPackage);

    /**
     * Returns the location where contexts are ending.
     *
     * @param contextPackage a package of inbound contexts
     * @return End location string.
     */
    String getEndLocation(InboundContextPackage contextPackage);
}
