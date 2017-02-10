package lemming.pos;

import lemming.data.Source;
import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.io.Serializable;
import java.util.UUID;

/**
 * Class representing a part of speech.
 */
@BatchSize(size = 30)
@DynamicUpdate
@Entity
@SelectBeforeUpdate
@OptimisticLocking(type = OptimisticLockType.VERSION)
@Table(name = "pos", indexes = {
        @Index(columnList = "uuid, name", unique = true),
        @Index(columnList = "source")})
@XmlRootElement
public class Pos implements Serializable {
    /**
     * Determines if a deserialized file is compatible with this class.
     */
    private static final long serialVersionUID = 1L;

    /**
     * ID associated with a part of speech.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * A UUID used to distinguish parts of speech.
     */
    @Column(name = "uuid")
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
    @Column(name = "name", nullable = false)
    private String name;

    /**
     * Source of a part of speech.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "source", nullable = false)
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
    @XmlTransient
    public String getUuid() {
        return uuid;
    }

    /**
     * Sets the UUID of a part of speech.
     *
     * @param uuid the UUID of a part of speech
     */
    public void setUuid(String uuid) {
        this.uuid = uuid;
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
    @XmlElement(name="name")
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
    @XmlElement(name="source")
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
     * @param object the reference object with which to compare
     * @return True if this object is the same as the object argument; false
     * otherwise.
     */
    @Override
    public boolean equals(Object object) {
        if (this == object)
            return true;
        if (object == null || !(object instanceof Pos))
            return false;

        Pos pos = (Pos) object;

        if (!(uuid instanceof String)) {
            uuid = UUID.randomUUID().toString();
        }

        return uuid.equals(pos.getUuid());
    }

    /**
     * Returns a hash code value for a part of speech.
     *
     * @return A hash code value for a part of speech.
     */
    @Override
    public int hashCode() {
        if (!(uuid instanceof String)) {
            uuid = UUID.randomUUID().toString();
        }

        return uuid.hashCode();
    }
}
