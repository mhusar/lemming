package lemming.context;

/**
 * Class representing context types.
 */
public class ContextType {
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
     * Neither rubric nor segment or verse.
     */
    private static final String NONE = "NONE";

    /**
     * Types of contexts.
     */
    public enum Type {
        RUBRIC, SEGMENT, VERSE, NONE
    }
}
