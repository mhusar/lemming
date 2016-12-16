package lemming.api.pos;

public abstract class Origin {
    /**
     * Taken from a predefined list.
     */
    private static final String DEFAULT = "DEFAULT";

    /**
     * Made by a user.
     */
    private static final String USER = "USER";

    /**
     * Origins for parts of speech.
     */
    public static enum Type {
        DEFAULT, USER
    };
}
