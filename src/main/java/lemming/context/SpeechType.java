package lemming.context;

/**
 * Class representing speech types.
 */
public class SpeechType {
    /**
     * Direct speech.
     */
    private static final String DIRECT = "DIRECT";

    /**
     * Indirect speech.
     */
    private static final String INDIRECT = "INDIRECT";

    /**
     * Neither direct nor indirect speech.
     */
    private static final String NONE = "NONE";

    /**
     * Types of speech.
     */
    public enum Type {
        DIRECT, INDIRECT, NONE
    }
}
