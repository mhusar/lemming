package lemming.api.lemma;

public abstract class Origin {
    /**
     * Taken from Tobler-Lommatzsch.
     */
    private static final String TL = "TL";

    /**
     * Made by a user.
     */
    private static final String USER = "USER";

    /**
     * Origins for lemmata.
     */
    public static enum Type {
        TL, USER
    };
}
