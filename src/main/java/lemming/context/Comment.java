package lemming.context;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lemming.data.DatedEntity;
import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.UUID;

/**
 * Class representing a comment.
 */
@BatchSize(size = 30)
@DynamicUpdate
@Entity
@SelectBeforeUpdate
@OptimisticLocking(type = OptimisticLockType.VERSION)
@Table(name = "comment", indexes = {
        @Index(columnList = "uuid", unique = true),
        @Index(columnList = "content")})
public class Comment extends DatedEntity implements Serializable {
    /**
     * Determines if a deserialized file is compatible with this class.
     */
    private static final long serialVersionUID = 1L;

    /**
     * ID associated with a comment.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * A UUID used to distinguish comments.
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
     * Content of a comment.
     */
    @Column(name = "content")
    private String content;

    /**
     * Creates an instance of a comment.
     */
    public Comment() {
    }

    /**
     * Returns the ID associated with a comment.
     *
     * @return Primary key of a comment.
     */
    public Integer getId() {
        return id;
    }

    /**
     * Sets the ID of a comment.
     *
     * @param id the ID of a comment
     */
    @SuppressWarnings("unused")
    private void setId(Integer id) {
        this.id = id;
    }

    /**
     * Returns the UUID of a comment.
     *
     * @return UUID of a comment.
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * Sets the UUID of a comment.
     *
     * @param uuid the UUID of a comment
     */
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    /**
     * Returns the version of a comment.
     *
     * @return Version number of a comment.
     */
    public Long getVersion() {
        return version;
    }

    /**
     * Sets the version number of a comment.
     *
     * @param version version number of a comment
     */
    @SuppressWarnings("unused")
    private void setVersion(Long version) {
        this.version = version;
    }

    /**
     * Returns the content of a comment.
     *
     * @return Content of a comment.
     */
    public String getContent() {
        return content;
    }

    /**
     * Sets the content of a comment.
     *
     * @param content content of a comment
     */
    public void setContent(String content) {
        this.content = content;
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
        if (object == null || !(object instanceof Comment))
            return false;

        Comment comment = (Comment) object;

        if (uuid == null) {
            uuid = UUID.randomUUID().toString();
        }

        return uuid.equals(comment.getUuid());
    }

    /**
     * Returns a hash code value for a comment.
     *
     * @return A hash code value for a comment.
     */
    @Override
    public int hashCode() {
        if (uuid == null) {
            uuid = UUID.randomUUID().toString();
        }

        return uuid.hashCode();
    }
}
