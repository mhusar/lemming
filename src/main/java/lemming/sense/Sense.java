package lemming.sense;

import lemming.lemma.Lemma;
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
 * Class representing a sense.
 */
@BatchSize(size = 30)
@DynamicUpdate
@Entity
@SelectBeforeUpdate
@OptimisticLocking(type = OptimisticLockType.VERSION)
@Table(name = "sense", indexes = {
        @Index(columnList = "uuid, meaning", unique = true)})
public class Sense implements Serializable {
    /**
     * Determines if a deserialized file is compatible with this class.
     */
    private static final long serialVersionUID = 1L;

    /**
     * ID associated with a sense.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * A UUID used to distinguish senses.
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
     * Lemma of a sense.
     */
    @ManyToOne
    @JoinColumn(name = "lemma_id", nullable = false)
    private Lemma lemma;

    /**
     * Meaning of a sense.
     */
    @Column(name = "meaning", nullable = false)
    private String meaning;

    /**
     * Parent node position of a sense (0-based).
     */
    @Column(name = "parent_position", nullable = false)
    private Integer parentPosition;

    /**
     * Child node position of a sense (0-based).
     */
    @Column(name = "child_position")
    private Integer childPosition;

    /**
     * Child senses of a sense.
     */
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(name = "sense_children", indexes = { @Index(columnList = "parent_id, child_id", unique = true) }, joinColumns = { @JoinColumn(name = "parent_id") }, inverseJoinColumns = { @JoinColumn(name = "child_id") })
    @OrderBy("child_position")
    private List<Sense> children;

    /**
     * Creates an instance of a sense.
     */
    public Sense() {
    }

    /**
     * Returns the ID associated with a sense.
     *
     * @return Primary key of a sense.
     */
    public Integer getId() {
        return id;
    }

    /**
     * Sets the ID of a sense.
     *
     * @param id the ID of a sense
     */
    @SuppressWarnings("unused")
    private void setId(Integer id) {
        this.id = id;
    }

    /**
     * Returns the UUID of a sense.
     *
     * @return UUID of a sense.
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * Sets the UUID of a sense.
     *
     * @param uuid the UUID of a sense
     */
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    /**
     * Returns the version of a sense.
     *
     * @return Version number of a sense.
     */
    public Long getVersion() {
        return version;
    }

    /**
     * Sets the version number of a sense.
     *
     * @param version version number of a sense
     */
    @SuppressWarnings("unused")
    private void setVersion(Long version) {
        this.version = version;
    }

    /**
     * Returns the lemma of a sense.
     *
     * @return Lemma of a sense.
     */
    public Lemma getLemma() {
        return lemma;
    }

    /**
     * Sets the lemma of a sense.
     *
     * @param lemma lemma of a sense
     */
    public void setLemma(Lemma lemma) {
        this.lemma = lemma;
    }

    /**
     * Returns the meaning of a sense.
     *
     * @return Meaning of a sense.
     */
    public String getMeaning() {
        return meaning;
    }

    /**
     * Sets the meaning of a sense.
     *
     * @param meaning the meaning of a sense
     */
    public void setMeaning(String meaning) {
        this.meaning = meaning;
    }

    /**
     * Returns the parent position of a sense.
     *
     * @return A number.
     */
    public Integer getParentPosition() {
        return parentPosition;
    }

    /**
     * Sets the parent position of a sense.
     *
     * @param parentPosition parent position
     */
    public void setParentPosition(Integer parentPosition) {
        this.parentPosition = parentPosition;
    }

    /**
     * Returns the child position of a node.
     *
     * @return A number or null if a sense is no parent node.
     */
    public Integer getChildPosition() {
        return childPosition;
    }

    /**
     * Sets the child position of a sense.
     *
     * @param childPosition child position
     */
    public void setChildPosition(Integer childPosition) {
        this.childPosition = childPosition;
    }

    /**
     * Returns child senses of a sense.
     *
     * @return A list of child senses.
     */
    public List<Sense> getChildren() {
        return children;
    }

    /**
     * Sets child senses for a sense.
     *
     * @param children list of child senses
     */
    public void setChildren(List<Sense> children) {
        this.children = children;
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
        if (object == null || !(object instanceof Sense))
            return false;

        Sense sense = (Sense) object;

        if (!(uuid instanceof String)) {
            uuid = UUID.randomUUID().toString();
        }

        return uuid.equals(sense.getUuid());
    }

    /**
     * Returns a hash code value for a sense.
     *
     * @return A hash code value for a sense.
     */
    @Override
    public int hashCode() {
        if (!(uuid instanceof String)) {
            uuid = UUID.randomUUID().toString();
        }

        return uuid.hashCode();
    }
}
