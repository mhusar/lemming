package lemming.context.inbound;

import lemming.context.BaseContext;

/**
 * A triple of two contexts and the distance between them.
 */
public class Triple {
    /**
     * Context 1.
     */
    private BaseContext context1;

    /**
     * Context 2.
     */
    private BaseContext context2;

    /**
     * Index of context 1.
     */
    private Integer context1Index;

    /**
     * Index of context 2.
     */
    private Integer context2Index;

    /**
     * Distance between context 1 and 2.
     */
    private Integer distance;

    /**
     * Creates a triple.
     *
     * @param context1      context 1
     * @param context1Index index of context 1
     * @param distance      distance between context 1 and 2
     * @param context2      context 2
     * @param context2Index index of context 2
     * @see MatchHelper
     */
    public Triple(BaseContext context1, Integer context1Index, Integer distance,
                  BaseContext context2, Integer context2Index) {
        this.context1 = context1;
        this.context1Index = context1Index;
        this.distance = distance;
        this.context2 = context2;
        this.context2Index = context2Index;
    }

    /**
     * Returns context 1.
     *
     * @return A context.
     */
    public BaseContext getContext1() {
        return context1;
    }

    /**
     * Returns context 2.
     *
     * @return A context.
     */
    public BaseContext getContext2() {
        return context2;
    }

    /**
     * Returns index of context 1.
     *
     * @return A context index.
     */
    public Integer getContext1Index() {
        return context1Index;
    }

    /**
     * Returns index of context 2.
     *
     * @return A context index.
     */
    public Integer getContext2Index() {
        return context2Index;
    }

    /**
     * Returns the distance between context 1 and 2.
     *
     * @return A distance as integer.
     */
    public Integer getDistance() {
        return distance;
    }
}
