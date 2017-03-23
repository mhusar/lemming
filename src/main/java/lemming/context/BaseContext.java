package lemming.context;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lemming.data.ChecksumEntityListener;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Base class representing a context with a minimum of fields.
 */
@EntityListeners({ ChecksumEntityListener.class })
@MappedSuperclass
public abstract class BaseContext implements Serializable {
    /**
     * Determines if a deserialized file is compatible with this class.
     */
    private static final long serialVersionUID = 1L;

    /**
     * ID associated with a context.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * A SHA512 hash of concatenated text elements from the original context.
     *
     * @see ChecksumEntityListener
     */
    @Column(name = "checksum", length = 130, nullable = false)
    @JsonIgnore
    private String checksum;

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
     * Creates an instance of a context.
     */
    public BaseContext() {
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
     * Returns the checksum of a base context.
     *
     * @return A hash string.
     */
    public String getChecksum() {
        return checksum;
    }

    /**
     * Sets the checksum of a context.
     */
    public void setChecksum(String checksum) {
        this.checksum = checksum;
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
     * Indicates if some other object is equal to this one.
     *
     * @param other the reference object with which to compare
     * @return True if this object is the same as the object argument; false otherwise.
     */
    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || !(other instanceof BaseContext)) return false;

        BaseContext context = (BaseContext) other;
        return getId() != null && getId().equals(context.getId());
    }

    /**
     * Returns a hash code value for a context.
     *
     * @return A hash code value for a context.
     */
    @Override
    public int hashCode() {
        if (getId() == null) {
            throw new IllegalStateException("Can’t create a hash code for a non-persistent object");
        }

        return getId();
    }
}
