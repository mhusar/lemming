package lemming.context;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lemming.lemma.Lemma;
import lemming.pos.Pos;
import lemming.sense.Sense;
import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * Class representing a context.
 */
@BatchSize(size = 30)
@DynamicUpdate
@Entity
@SelectBeforeUpdate
@OptimisticLocking(type = OptimisticLockType.VERSION)
@Table(name = "context", indexes = {
        @Index(columnList = "hash"),
        @Index(columnList = "keyword, preceding, following, location, pos_string, lemma_string")
})
public class Context extends BaseContext implements Serializable {
    /**
     * Part of speech of a keyword in context.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pos_id")
    private Pos pos;

    /**
     * Part of speech of a context as string.
     *
     * For better performance of the context index table.
     */
    @Column(name = "pos_string", length = 120)
    @JsonIgnore
    private String posString;

    /**
     * Lemma of a keyword in context.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lemma_id")
    private Lemma lemma;

    /**
     * Lemma of a context as string.
     *
     * For better performance of the context index table.
     */
    @Column(name = "lemma_string", length = 120)
    @JsonIgnore
    private String lemmaString;

    /**
     * Sense of a keyword in context.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sense_id")
    @JsonIgnore
    private Sense sense;

    /**
     * Selected state of a context.
     */
    @Transient
    @JsonIgnore
    private Boolean selected;

    /**
     * Creates an instance of a context.
     */
    public Context() {
        super();
    }

    /**
     * Creates an instance of a context.
     *
     * @param location location of a context
     * @param type type of a context
     * @param keyword keyword of a context
     * @param preceding preceding text of a context
     * @param following following text of a context
     * @param initPunctuation punctuation preceding the keyword
     * @param endPunctuation punctuation following the keyword
     */
    public Context(String location, ContextType.Type type, String keyword, String preceding, String following,
                   String initPunctuation, String endPunctuation) {
        super(location, type, keyword, preceding, following, initPunctuation, endPunctuation);
    }

    /**
     * Returns the part of speech of a context.
     *
     * @return Part of speech of a context.
     */
    public Pos getPos() {
        return pos;
    }

    /**
     * Sets the part of speech of a context.
     *
     * @param pos part of speech of a context
     */
    public void setPos(Pos pos) {
        this.pos = pos;
    }

    /**
     * Returns the part of speech of a context as string.
     *
     * @return Part of speech of a context as string.
     */
    public String getPosString() {
        return posString;
    }

    /**
     * Sets the part of speech string of a context.
     *
     * @param posString part of speech string of a context
     */
    public void setPosString(String posString) {
        this.posString = posString;
    }

    /**
     * Returns the lemma of a context.
     *
     * @return Lemma of a context.
     */
    public Lemma getLemma() {
        return lemma;
    }

    /**
     * Sets the lemma of a context.
     *
     * @param lemma lemma of a context
     */
    public void setLemma(Lemma lemma) {
        this.lemma = lemma;
    }

    /**
     * Returns the lemma of a context as string.
     *
     * @return Lemma of a context as string.
     */
    public String getLemmaString() {
        return lemmaString;
    }

    /**
     * Sets the lemma string of a context.
     *
     * @param lemmaString lemma string of a context
     */
    public void setLemmaString(String lemmaString) {
        this.lemmaString = lemmaString;
    }

    /**
     * Returns the sense of a context.
     *
     * @return Sense of a context.
     */
    public Sense getSense() {
        return sense;
    }

    /**
     * Sets the sense of a context.
     *
     * @param sense sense of a context
     */
    public void setSense(Sense sense) {
        this.sense = sense;
    }

    /**
     * Returns the selected state of a context.
     *
     * @return Selected state of a context.
     */
    public Boolean getSelected() {
        if (selected instanceof Boolean) {
            return selected;
        } else {
            return false;
        }
    }

    /**
     * Sets the selected state of a context.
     *
     * @param selected selected state of a context
     */
    public void setSelected(Boolean selected) {
        this.selected = selected;
    }
}
