package lemming.context.inbound;

import lemming.data.IDao;

import javax.ws.rs.core.MultivaluedMap;

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

    /**
     * Checks if a context package has any matches.
     *
     * @param contextPackage a package of inbound contexts
     * @return A boolean value.
     */
    Boolean hasMatchedContexts(InboundContextPackage contextPackage);

    /**
     * Finds inbound contexts without a match ordered by location and number.
     *
     * @param contextPackage a package of inbound contexts
     * @return A list of inbound contexts.
     */
    List<InboundContext> findUnmatchedContexts(InboundContextPackage contextPackage);

    /**
     * Finds locations with inbound contexts without a match.
     *
     * @param contextPackage a package of inbound contexts
     * @return A list context location strings.
     */
    List<String> findUnmatchedContextLocations(InboundContextPackage contextPackage);

    /**
     * Finds unmatched contexts by location ordered by number.
     *
     * @param contextPackage a package of inbound contexts
     * @param location       a context location
     * @return List of unmatched inbound contexts.
     */
    List<InboundContext> findUnmatchedContextsByLocation(InboundContextPackage contextPackage,
                                                                             String location);

    /**
     * Map of unmatched cohesive contexts by location ordered by number.
     *
     * @param contextPackage a package of inbound contexts
     * @param location       a context location
     * @return Map of unmatched cohesive inbound contexts.
     */
    MultivaluedMap<Integer, InboundContext> groupUnmatchedContexts(InboundContextPackage contextPackage,
                                                                   String location);

    /**
     * Matches inbound contexts of an inbound context package against contexts. The contexts are matched by a hash
     * applied by class HashEntityListener.
     *
     * @param contextPackage a package of inbound contexts
     */
    void matchContextsByHash(InboundContextPackage contextPackage);

    /**
     * Imports inbound contexts of an inbound context package, removes old contexts and adds new contexts.
     *
     * @param contextPackage a package of inbound contexts
     */
    void importContexts(InboundContextPackage contextPackage);
}
