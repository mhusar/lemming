package lemming.pos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lemming.data.DatedEntity;
import lemming.data.Source;
import lemming.data.UuidEntityListener;
import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.UUID;

/**
 * Class representing a part of speech.
 */
@BatchSize(size = 30)
@DynamicUpdate
@Entity
@EntityListeners({ UuidEntityListener.class })
@SelectBeforeUpdate
@OptimisticLocking(type = OptimisticLockType.VERSION)
@Table(name = "pos", indexes = {
        @Index(columnList = "uuid", unique = true),
        @Index(columnList = "name, source")})
public class Pos extends DatedEntity implements Serializable {
    /**
     * ID associated with a part of speech.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * A UUID used to distinguish parts of speech.
     */
    @Column(name = "uuid", nullable = false)
    @JsonIgnore
    private String uuid;

    /**
     * Version number field used for optimistic locking.
     */
    @Column(name = "version")
    @Version
    private Long version;

    /**
     * Name of a part of speech.
     */
    @Column(name = "name", nullable = false, length = 120)
    private String name;

    /**
     * Source of a part of speech.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "source", nullable = false, length = 30)
    private Source.PosType source;

    /**
     * Creates an instance of a part of speech.
     */
    public Pos() {
    }

    /**
     * Returns the ID associated with a part of speech.
     *
     * @return Primary key of a part of speech.
     */
    public Integer getId() {
        return id;
    }

    /**
     * Sets the ID of a part of speech.
     *
     * @param id the ID of a part of speech
     */
    @SuppressWarnings("unused")
    private void setId(Integer id) {
        this.id = id;
    }

    /**
     * Returns the UUID of a part of speech.
     *
     * @return UUID of a part of speech.
     */
    public String getUuid() {
        if (uuid == null) {
            uuid = UUID.randomUUID().toString();
        }

        return uuid;
    }

    /**
     * Sets the UUID of a part of speech.
     */
    public void setUuid() {
        getUuid();
    }

    /**
     * Returns the version of a part of speech.
     *
     * @return Version number of a part of speech.
     */
    public Long getVersion() {
        return version;
    }

    /**
     * Sets the version number of a part of speech.
     *
     * @param version version number of a part of speech
     */
    @SuppressWarnings("unused")
    private void setVersion(Long version) {
        this.version = version;
    }

    /**
     * Returns the name of a part of speech.
     *
     * @return Name of a part of speech.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of a part of speech.
     *
     * @param name
     *            the name of a part of speech
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the source of a part of speech.
     *
     * @return Source of a part of speech.
     */
    public Source.PosType getSource() {
        return source;
    }

    /**
     * Sets the source of a part of speech.
     *
     * @param source source of a part of speech
     */
    public void setSource(Source.PosType source) {
        this.source = source;
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
        if (other == null || !(other instanceof Pos)) return false;

        Pos pos = (Pos) other;
        return getUuid().equals(pos.getUuid());
    }

    /**
     * Returns a hash code value for a part of speech.
     *
     * @return A hash code value for a part of speech.
     */
    @Override
    public int hashCode() {
        return getUuid().hashCode();
    }
}
