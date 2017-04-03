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
import java.util.UUID;

/**
 * Class representing a context.
 */
@BatchSize(size = 30)
@DynamicUpdate
@Entity
@SelectBeforeUpdate
@OptimisticLocking(type = OptimisticLockType.VERSION)
@Table(name = "context", indexes = {
        @Index(columnList = "uuid", unique = true),
        @Index(columnList = "keyword, preceding, following, location, pos_string, lemma_string")})
public class Context implements Serializable {
    /**
     * ID associated with a context.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * A UUID used to distinguish contexts.
     */
    @Column(name = "uuid")
    @JsonIgnore
    private String uuid;

    /**
     * Version number field used for optimistic locking.
     */
    @Column(name = "version")
    @Version
    private Long version;

    /**
     * Location of a context.
     */
    @Column(name = "location", length = 30, nullable = false)
    private String location;

    /**
     * Type of a context.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "type", length = 30, nullable = false)
    private ContextType.Type type;

    /**
     * Keyword of a context.
     */
    @Column(name = "keyword", length=120, nullable = false)
    private String keyword;

    /**
     * Preceding text of a context.
     */
    @Column(name = "preceding", nullable = false)
    private String preceding;

    /**
     * Following text of a context.
     */
    @Column(name = "following", nullable = false)
    private String following;

    /**
     * A punctuation preceding the keyword.
     */
    @Column(name = "punctuation_init", length = 30)
    private String initPunctuation;

    /**
     * A punctuation following the keyword.
     */
    @Column(name = "punctuation_end", length = 30)
    private String endPunctuation;

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
    }

    /**
     * Returns the ID associated with a context.
     *
     * @return Primary key of a context.
     */
    public Integer getId() {
        return id;
    }

    /**
     * Sets the ID of a context.
     *
     * @param id the ID of a context
     */
    @SuppressWarnings("unused")
    private void setId(Integer id) {
        this.id = id;
    }

    /**
     * Returns the UUID of a context.
     *
     * @return UUID of a context.
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * Sets the UUID of a context.
     *
     * @param uuid the UUID of a context
     */
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    /**
     * Returns the version of a context.
     *
     * @return Version number of a context.
     */
    public Long getVersion() {
        return version;
    }

    /**
     * Sets the version number of a context.
     *
     * @param version version number of a context
     */
    @SuppressWarnings("unused")
    private void setVersion(Long version) {
        this.version = version;
    }

    /**
     * Returns the location of a context.
     *
     * @return Location of a context.
     */
    public String getLocation() {
        return location;
    }

    /**
     * Sets the location of a context.
     *
     * @param location location of a context.
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * Returns the type of a context.
     *
     * @return Type of a context.
     */
    public ContextType.Type getType() {
        return type;
    }

    /**
     * Sets the type of a context.
     *
     * @param type type of a context
     */
    public void setType(ContextType.Type type) {
        this.type = type;
    }

    /**
     * Returns the keyword of a context.
     *
     * @return Keyword of a context.
     */
    public String getKeyword() {
        return keyword;
    }

    /**
     * Sets the keyword of a context.
     *
     * @param keyword
     *            the keyword of a context
     */
    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    /**
     * Returns the preceding text of a context.
     *
     * @return Preceding text.
     */
    public String getPreceding() {
        return preceding;
    }

    /**
     * Sets the preceding text of a context.
     *
     * @param preceding preceding text
     */
    public void setPreceding(String preceding) {
        this.preceding = preceding;
    }

    /**
     * Return the following text of a context.
     *
     * @return Following text.
     */
    public String getFollowing() {
        return following;
    }

    /**
     * Sets the following text of a context.
     *
     * @param following following text
     */
    public void setFollowing(String following) {
        this.following = following;
    }

    /**
     * Returns the punctuation preceding a keyword.
     *
     * @return Punctuation text.
     */
    public String getInitPunctuation() {
        return initPunctuation;
    }

    /**
     * Sets the punctuation preceding a keyword.
     *
     * @param initPunctuation punctuation text
     */
    public void setInitPunctuation(String initPunctuation) {
        this.initPunctuation = initPunctuation;
    }

    /**
     * Returns the punctuation following a keyword.
     *
     * @return Punctuation text.
     */
    public String getEndPunctuation() {
        return endPunctuation;
    }

    /**
     * Sets the punctuation following a keyword.
     *
     * @param endPunctuation punctuation text
     */
    public void setEndPunctuation(String endPunctuation) {
        this.endPunctuation = endPunctuation;
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

    /**
     * Indicates if some other object is equal to this one.
     *
     * @param object the reference object with which to compare
     * @return True if this object is the same as the object argument; false
     * otherwise.
     */
    @Override
    public boolean equals(Object object) {
        if (this == object)
            return true;
        if (object == null || !(object instanceof Context))
            return false;

        Context context = (Context) object;

        if (!(uuid instanceof String)) {
            uuid = UUID.randomUUID().toString();
        }

        return uuid.equals(context.getUuid());
    }

    /**
     * Returns a hash code value for a context.
     *
     * @return A hash code value for a context.
     */
    @Override
    public int hashCode() {
        if (!(uuid instanceof String)) {
            uuid = UUID.randomUUID().toString();
        }

        return uuid.hashCode();
    }
}
