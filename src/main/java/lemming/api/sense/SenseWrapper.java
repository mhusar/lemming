package lemming.api.sense;

import lemming.api.lemma.Lemma;

import java.io.Serializable;

/**
 * A wrapper for lemma sense tupels.
 */
public class SenseWrapper implements Serializable {
    /**
     * A lemma.
     */
    private Lemma lemma;

    /**
     * A sense.
     */
    private Sense sense;

    /**
     * Lemma name as string.
     */
    private String lemmaString;

    /**
     * Sense meaning as string.
     */
    private String senseString;

    /**
     * Creates a sense wrapper.
     */
    public SenseWrapper() {
    }

    /**
     * Creates a sense wrapper.
     *
     * @param lemma a lemma
     * @param sense a sense
     */
    public SenseWrapper(Lemma lemma, Sense sense) {
        this.lemma = lemma;
        this.sense = sense;
    }

    /**
     * Returns the lemma of a sense wrapper.
     *
     * @return A lemma, or null.
     */
    public Lemma getLemma() {
        return lemma;
    }

    /**
     * Sets the lemma of a sense wrapper.
     *
     * @param lemma a lemma
     */
    public void setLemma(Lemma lemma) {
        this.lemma = lemma;
    }

    /**
     * Returns the sense of a sense wrapper.
     *
     * @return A sense, or null.
     */
    public Sense getSense() {
        return sense;
    }

    /**
     * Sets the sense of a sense wrapper.
     *
     * @param sense a sense
     */
    public void setSense(Sense sense) {
        this.sense = sense;
    }

    /**
     * Returns the lemma name of a sense wrapper’s lemma.
     * @return A string, or null.
     */
    public String getLemmaString() {
        return lemmaString;
    }

    /**
     * Sets a lemma name as string.
     *
     * @param lemmaString lemma name as string
     */
    public void setLemmaString(String lemmaString) {
        this.lemmaString = lemmaString;
    }

    /**
     * Returns the sense meaning of a sense wrapper’s sense.
     *
     * @return A lemma string, or null.
     */
    public String getSenseString() {
        return senseString;
    }

    /**
     * Sets a sense senseString as string.
     *
     * @param senseString sense meaning as string
     */
    public void setSenseString(String senseString) {
        this.senseString = senseString;
    }
}
