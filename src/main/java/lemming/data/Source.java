package lemming.data;

/**
 * Class representing sources of data.
 */
public abstract class Source {
    /**
     * Extracted from Dictionnaire Étymologique de l'Ancien Français.
     *
     * @see <a href="http://www.deaf-page.de/">Dictionnaire Étymologique de l'Ancien Français</a>
     */
    private static final String DEAF = "DEAF";

    /**
     * Extracted from Tobler-Lommatzsch.
     *
     * @see <a href="http://www.uni-stuttgart.de/lingrom/stein/tl/">Tobler-Lommatzsch</a>
     */
    private static final String TL = "TL";

    /**
     * Data stored by a user.
     */
    private static final String USER = "USER";

    /**
     * Sources of lemmata.
     */
    public enum LemmaType {
        TL, USER
    };

    /**
     * Sources of parts of speech.
     */
    public enum PosType {
        DEAF, USER
    }
}
