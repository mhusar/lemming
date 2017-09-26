package lemming.context;

/**
 * Class representing context types.
 */
public class ContextType {
    /**
     * Group item. Used for groups of contexts.
     */
    private static final String GROUP = "GROUP";

    /**
     * Rubric item.
     */
    private static final String RUBRIC = "RUBRIC";

    /**
     * Segment item.
     */
    private static final String SEGMENT = "SEGMENT";

    /**
     * Verse item.
     */
    private static final String VERSE = "VERSE";

    /**
     * Types of contexts.
     */
    public enum Type {
        GROUP, RUBRIC, SEGMENT, VERSE
    }
}
