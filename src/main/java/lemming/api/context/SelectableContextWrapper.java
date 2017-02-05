package lemming.api.context;

import lemming.api.lemma.Lemma;
import lemming.api.pos.Pos;

import java.io.Serializable;

/**
 * A wrapper class for contexts which stores a select state.
 */
public class SelectableContextWrapper implements Serializable {
    /**
     * A context.
     */
    private Context context;

    /**
     * Selected state.
     */
    private Boolean isSelected;

    /**
     * Lemma of a keywordString in context as string.
     */
    private String lemmaString;

    /**
     * Part of speech of a keywordString in context as string.
     */
    private String posString;

    /**
     * Location of a context as string.
     */
    private String locationString;

    /**
     * Preceding text of a context as string.
     */
    private String precedingString;

    /**
     * Keyword of a context as string.
     */
    private String keywordString;

    /**
     * Following text of a context as string.
     */
    private String followingString;

    /**
     * Creates a selectable context wrapper.
     */
    public SelectableContextWrapper() {
    }

    /**
     * Creates a selectable context wrapper.
     *
     * @param context a context
     */
    public SelectableContextWrapper(Context context) {
        this.context = context;
    }

    /**
     * Returns a context.
     *
     * @return A context.
     */
    public Context getContext() {
        return context;
    }

    /**
     * Sets a context.
     *
     * @param context a context
     */
    public void setContext(Context context) {
        this.context = context;
    }

    /**
     * Returns a select state.
     *
     * @return A boolean.
     */
    public Boolean getSelected() {
        return isSelected;
    }

    /**
     * Sets a select state.
     *
     * @param selected select state
     */
    public void setSelected(Boolean selected) {
        isSelected = selected;
    }

    /**
     * Returns the lemma of a context.
     *
     * @return A lemma or null.
     */
    public Lemma getLemma() {
        if (context instanceof Context) {
            if (context.getLemma() instanceof Lemma) {
                return context.getLemma();
            }
        }

        return null;
    }

    /**
     * Sets the lemma of a context.
     *
     * @param lemma lemma of a context
     */
    public void setLemma(Lemma lemma) {
        if (context instanceof Context) {
            context.setLemma(lemma);
        }
    }

    /**
     * Return a lemma string.
     *
     * @return A lemma string, or null.
     */
    public String getLemmaString() {
        return lemmaString;
    }

    /**
     * Sets a lemma string.
     *
     * @param lemmaString a lemma string
     */
    public void setLemmaString(String lemmaString) {
        this.lemmaString = lemmaString;
    }

    /**
     * Returns the part of speech of a context.
     *
     * @return A part of speech, or null.
     */
    public Pos getPos() {
        if (context instanceof Context) {
            if (context.getPos() instanceof Pos) {
                return context.getPos();
            }
        }

        return null;
    }

    /*
     * Sets the part of speech of a context.
     *
     * @param pos part of speech of a context
     */
    public void setPos(Pos pos) {
        if (context instanceof Context) {
            context.setPos(pos);
        }
    }

    /**
     * Returns a pos string.
     *
     * @return A pos string, or null.
     */
    public String getPosString() {
        return posString;
    }

    /**
     * Sets a pos string.
     *
     * @param posString a pos string
     */
    public void setPosString(String posString) {
        this.posString = posString;
    }

    /**
     * Returns a location string.
     *
     * @return A location string, or null.
     */
    public String getLocationString() {
        return locationString;
    }

    /**
     * Sets a location string
     *
     * @param locationString a location string
     */
    public void setLocationString(String locationString) {
        this.locationString = locationString;
    }

    /**
     * Returns a preceding context string.
     *
     * @return A preceding context string, or null.
     */
    public String getPrecedingString() {
        return precedingString;
    }

    /**
     * Sets a preceding context string.
     *
     * @param precedingString a preceding context string
     */
    public void setPrecedingString(String precedingString) {
        this.precedingString = precedingString;
    }

    /**
     * Returns a keyword string.
     *
     * @return A keyword string, or null.
     */
    public String getKeywordString() {
        return keywordString;
    }

    /**
     * Sets a keyword string.
     *
     * @param keywordString a keyword string
     */
    public void setKeywordString(String keywordString) {
        this.keywordString = keywordString;
    }

    /**
     * Returns a following context string.
     *
     * @return A following context string, or null.
     */
    public String getFollowingString() {
        return followingString;
    }

    /**
     * Sets a following context string.
     *
     * @param followingString a following context string
     */
    public void setFollowing(String followingString) {
        this.followingString = followingString;
    }
}
