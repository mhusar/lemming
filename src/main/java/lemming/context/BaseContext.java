package lemming.context;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lemming.data.DatedEntity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.UUID;

/**
 * Base class representing a context with a minimum of fields.
 */
@MappedSuperclass
public abstract class BaseContext extends DatedEntity implements Serializable {
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
     * Number of a context in a location.
     */
    @Column(name = "number", nullable = false)
    private Integer number;

    /**
     * Type of a context.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "type", length = 30, nullable = false)
    private ContextType.Type type;

    /**
     * Keyword of a context.
     */
    @Column(name = "keyword", length = 120, nullable = false)
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
    BaseContext() {
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
     * Returns the number of a context.
     *
     * @return Number of a context.
     */
    public Integer getNumber() {
        return number;
    }

    /**
     * Sets the number of a context.
     *
     * @param number number of a context
     */
    public void setNumber(Integer number) {
        this.number = number;
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
     * @param keyword the keyword of a context
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
     * @param object the reference object with which to compare
     * @return True if this object is the same as the object argument; false otherwise.
     */
    @Override
    public boolean equals(Object object) {
        if (this == object)
            return true;
        if (object == null || !(object instanceof BaseContext))
            return false;

        BaseContext context = (BaseContext) object;

        if (uuid == null) {
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
        if (uuid == null) {
            uuid = UUID.randomUUID().toString();
        }

        return uuid.hashCode();
    }
}
