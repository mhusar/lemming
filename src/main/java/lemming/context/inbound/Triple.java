package lemming.context.inbound;

import lemming.context.Context;

import java.io.Serializable;

/**
 * A triple of context, inbound context, and the distance between them.
 */
public class Triple implements Serializable {
    /**
     * Context.
     */
    private Context context;

    /**
     * Context index.
     */
    private InboundContext inboundContext;

    /**
     * Index of context.
     */
    private Integer contextIndex;

    /**
     * Index of inbound context.
     */
    private Integer inboundContextIndex;

    /**
     * Distance between context and inbound.
     */
    private Integer distance;

    /**
     * Creates a triple.
     *
     * @param context      context
     * @param contextIndex index of context
     * @param distance      distance between context and inbound context
     * @param inboundContext      inbound context
     * @param inboundContextIndex index of inbound context
     * @see MatchHelper
     */
    public Triple(Context context, Integer contextIndex, Integer distance,
                  InboundContext inboundContext, Integer inboundContextIndex) {
        this.context = context;
        this.contextIndex = contextIndex;
        this.distance = distance;
        this.inboundContext = inboundContext;
        this.inboundContextIndex = inboundContextIndex;
    }

    /**
     * Returns a context.
     *
     * @return A context.
     */
    public Context getContext() {
        return context;
    }

    /**
     * Returns an inbound context.
     *
     * @return An inbound  context.
     */
    public InboundContext getInboundContext() {
        return inboundContext;
    }

    /**
     * Returns index of contex.
     *
     * @return A context index.
     */
    public Integer getContextIndex() {
        return contextIndex;
    }

    /**
     * Returns index of inbound context.
     *
     * @return An inbound context index.
     */
    public Integer getInboundContextIndex() {
        return inboundContextIndex;
    }

    /**
     * Returns the distance between context and inbound context.
     *
     * @return A distance as integer.
     */
    public Integer getDistance() {
        return distance;
    }
}
