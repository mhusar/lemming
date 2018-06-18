package lemming.context;

/**
 * Class representing context group types.
 */
public class ContextGroupType {
    /**
     * Group item.
     */
    private static final String GROUP = "GROUP";

    /**
     * Member item.
     */
    private static final String MEMBER = "MEMBER";

    /**
     * Neither group nor member item.
     */
    private static final String NONE = "NONE";

    /**
     * Group types of contexts.
     */
    public enum Type {
        GROUP, MEMBER, NONE
    }
}
