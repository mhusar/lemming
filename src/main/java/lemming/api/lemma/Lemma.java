package lemming.api.lemma;

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

import lemming.api.data.Source;

/**
 * Class representing a lemma.
 */
@BatchSize(size = 20)
@DynamicUpdate
@Entity
@SelectBeforeUpdate
@OptimisticLocking(type = OptimisticLockType.VERSION)
@Table(name = "lemma", indexes = {
        @Index(columnList = "uuid, name", unique = true),
        @Index(columnList = "source")})
@XmlRootElement
public class Lemma implements Serializable {
    /**
     * Determines if a deserialized file is compatible with this class.
     */
    private static final long serialVersionUID = 1L;

    /**
     * ID associated with a lemma.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * A UUID used to distinguish lemmata.
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
     * Name of a lemma.
     */
    @Column(name = "name", nullable = false)
    private String name;

    /**
     * Part-of-speech of a lemma.
     */
    @Column(name = "pos")
    private String pos;

    /**
     * Source of a lemma.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "source", nullable = false)
    private Source.LemmaType source;

    /**
     * Creates an instance of a lemma.
     */
    public Lemma() {
    }

    /**
     * Returns the ID associated with a lemma.
     *
     * @return Primary key of a lemma.
     */
    public Integer getId() {
        return id;
    }

    /**
     * Sets the ID of a lemma.
     *
     * @param id the ID of a lemma
     */
    @SuppressWarnings("unused")
    private void setId(Integer id) {
        this.id = id;
    }

    /**
     * Returns the UUID of a lemma.
     *
     * @return UUID of a lemma.
     */
    @XmlTransient
    public String getUuid() {
        return uuid;
    }

    /**
     * Sets the UUID of a lemma.
     *
     * @param uuid the UUID of a lemma
     */
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    /**
     * Returns the version of a lemma.
     *
     * @return Version number of a lemma.
     */
    public Long getVersion() {
        return version;
    }

    /**
     * Sets the version number of a lemma.
     *
     * @param version version number of a lemma
     */
    @SuppressWarnings("unused")
    private void setVersion(Long version) {
        this.version = version;
    }

    /**
     * Returns the name of a lemma.
     *
     * @return Name of a lemma.
     */
    @XmlElement(name="name")
    public String getName() {
        return name;
    }

    /**
     * Sets the name of a lemma.
     *
     * @param name
     *            the name of a lemma
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the part of speech of a lemma.
     *
     * @return Part-of-speech of a lemma.
     */
    @XmlElement(name="pos")
    public String getPos() {
        return pos;
    }

    /**
     * Sets the part of speech of a lemma.
     *
     * @param pos
     *            the part of speech of a lemma
     */
    public void setPos(String pos) {
        this.pos = pos;
    }

    /**
     * Returns the source of a lemma.
     *
     * @return Source of a lemma.
     */
    @XmlElement(name="source")
    public Source.LemmaType getSource() {
        return source;
    }

    /**
     * Sets the source of a lemma.
     *
     * @param source source of a lemma
     */
    public void setSource(Source.LemmaType source) {
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
        if (object == null || !(object instanceof Lemma))
            return false;

        Lemma lemma = (Lemma) object;

        if (!(uuid instanceof String)) {
            uuid = UUID.randomUUID().toString();
        }

        return uuid.equals(lemma.getUuid());
    }

    /**
     * Returns a hash code value for a lemma.
     *
     * @return A hash code value for a lemma.
     */
    @Override
    public int hashCode() {
        if (!(uuid instanceof String)) {
            uuid = UUID.randomUUID().toString();
        }

        return uuid.hashCode();
    }
}
