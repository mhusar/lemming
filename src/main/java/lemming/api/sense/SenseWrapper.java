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
     * @return A lemma.
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
     * @return A sense.
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
     * Returns the name of a sense wrapper’s lemma.
     * @return A string or null.
     */
    public String getName() {
        if (lemma instanceof Lemma) {
            return lemma.getName();
        }

        return null;
    }

    /**
     * Returns the meaning of a sense wrapper’s sense.
     *
     * @return A string or null.
     */
    public String getMeaning() {
        if (sense instanceof Sense) {
            return sense.getMeaning();
        }

        return null;
    }
}
