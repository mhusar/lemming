package lemming.lemma;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lemming.data.Source;
import lemming.pos.Pos;
import lemming.sense.Sense;
import org.hibernate.annotations.*;

import javax.persistence.CascadeType;
import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.List;
import java.util.UUID;

/**
 * Class representing a lemma.
 */
@BatchSize(size = 30)
@DynamicUpdate
@Entity
@SelectBeforeUpdate
@OptimisticLocking(type = OptimisticLockType.VERSION)
@Table(name = "lemma", indexes = {
        @Index(columnList = "uuid, name", unique = true),
        @Index(columnList = "replacement_id, replacement_string, pos_string, source, reference")})
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
    @JsonIgnore
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
     * Replacement of a lemma.
     */
    @ManyToOne
    @JoinColumn(name = "replacement_id")
    @JsonIgnore
    private Lemma replacement;

    /**
     * Replacement of a lemma as string.
     */
    @Column(name = "replacement_string")
    @JsonIgnore
    private String replacementString;

    /**
     * Part of speech of a lemma.
     */
    @ManyToOne
    @JoinColumn(name = "pos_id")
    private Pos pos;

    /**
     * Part of speech of a lemma as string.
     */
    @Column(name = "pos_string")
    private String posString;

    /**
     * Source of a lemma.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "source", nullable = false)
    private Source.LemmaType source;

    /**
     * Reference of a lemma.
     */
    @Column(name = "reference")
    private String reference;

    /**
     * Senses of a lemma.
     */
    @OneToMany(mappedBy="lemma", cascade=CascadeType.ALL, fetch=FetchType.LAZY)
    @OrderBy("parent_position")
    @JsonIgnore
    private List<Sense> senses;

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
    public String getName() {
        return name;
    }

    /**
     * Sets the lemma of a lemma.
     *
     * @param name
     *            the name of a lemma
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the replacement of a lemma.
     *
     * @return A lemma, or null.
     */
    public Lemma getReplacement() {
        return replacement;
    }

    /**
     * Sets the replacement of a lemma.
     *
     * @param replacement a replacement lemma
     */
    public void setReplacement(Lemma replacement) {
        this.replacement = replacement;

        if (replacement instanceof Lemma) {
            replacementString = replacement.getName();
        } else {
            replacementString = null;
        }
    }

    /**
     *
     * @return
     */
    public String getReplacementString() {
        return replacementString;
    }

    /**
     *
     * @param replacementString
     */
    public void setReplacementString(String replacementString) {
        this.replacementString = replacementString;
    }

    /**
     * Returns the part of speech of a lemma.
     *
     * @return Part of speech of a lemma.
     */
    public Pos getPos() {
        return pos;
    }

    /**
     * Sets the part of speech of a lemma.
     *
     * @param pos
     *            the part of speech of a lemma
     */
    public void setPos(Pos pos) {
        this.pos = pos;

        if (pos instanceof Pos) {
            posString = pos.getName();
        } else {
            posString = null;
        }
    }

    /**
     * Returns the part of speech of a lemma as string.
     *
     * @return Part of speech of a lemma.
     */
    public String getPosString() {
        return posString;
    }

    /**
     * Sets the part of speech of a lemma as string.
     *
     * @param posString
     *            the part of speech of a lemma
     */
    public void setPosString(String posString) {
        this.posString = posString;
    }

    /**
     * Returns the source of a lemma.
     *
     * @return Source of a lemma.
     */
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
     * Returns the text reference of a lemma;
     *
     * @return A reference, or null.
     */
    public String getReference() {
        return reference;
    }

    /**
     * Sets the text reference of a lemma.
     *
     * @param reference reference of a lemma
     */
    public void setReference(String reference) {
        this.reference = reference;
    }

    /**
     * Returns senses of a lemma.
     *
     * @return A list of senses.
     */
    public List<Sense> getSenses() {
        return senses;
    }

    /**
     * Sets senses of a lemma.
     *
     * @param senses list of senses
     */
    public void setSenses(List<Sense> senses) {
        this.senses = senses;
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
