package lemming.data;

/**
 * Class representing sources of data.
 */
public abstract class Source {
    /**
     * Sources of lemmata.
     */
    public enum LemmaType {
        TL, USER
    }

    /**
     * Sources of parts of speech.
     */
    public enum PosType {
        DEAF, USER
    }
}
