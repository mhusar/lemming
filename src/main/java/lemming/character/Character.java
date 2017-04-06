package lemming.character;

import java.io.Serializable;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.persistence.Version;

import lemming.data.DatedEntity;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.OptimisticLockType;
import org.hibernate.annotations.OptimisticLocking;
import org.hibernate.annotations.SelectBeforeUpdate;

/**
 * Represents a special character.
 */
@BatchSize(size = 30)
@DynamicUpdate
@Entity
@OptimisticLocking(type = OptimisticLockType.VERSION)
@SelectBeforeUpdate
@Table(name = "\"character\"", indexes = {
        @Index(columnList = "`character`", unique = true),
        @Index(columnList = "position", unique = true),
        @Index(columnList = "uuid", unique = true) })
public class Character extends DatedEntity implements Serializable {
    /**
     * ID associated with a special character.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * A UUID used to distinguish characters.
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
     * Character string of a character.
     */
    @Column(name = "\"character\"", nullable = false)
    private String character;

    /**
     * Position of a character.
     */
    @Column(name = "position", nullable = false)
    private Integer position;

    /**
     * Creates an instance of a character.
     */
    public Character() {
    }

    /**
     * Returns the ID associated with a character.
     * 
     * @return Primary key of a character.
     */
    public Integer getId() {
        return id;
    }

    /**
     * Sets the ID of a character.
     * 
     * @param id
     *            the ID of a character
     */
    @SuppressWarnings("unused")
    private void setId(Integer id) {
        this.id = id;
    }

    /**
     * Returns the UUID of a character.
     * 
     * @return UUID of a character.
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * Sets the UUID of a character.
     * 
     * @param uuid
     *            the UUID of a character
     */
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    /**
     * Returns the version of a character.
     * 
     * @return Version number of a character.
     */
    @SuppressWarnings("unused")
    private Long getVersion() {
        return version;
    }

    /**
     * Sets the version number of a character.
     * 
     * @param version
     *            version number of a character
     */
    @SuppressWarnings("unused")
    private void setVersion(Long version) {
        this.version = version;
    }

    /**
     * Returns the character string of a character.
     * 
     * @return Identifier of a character.
     */
    public String getCharacter() {
        return character;
    }

    /**
     * Sets the character string of a character.
     * 
     * @param character
     *            the character string of a character
     */
    public void setCharacter(String character) {
        this.character = character;
    }

    /**
     * Returns the position of a character.
     * 
     * @return Position of a character.
     */
    public Integer getPosition() {
        return position;
    }

    /**
     * Sets the position of a character.
     * 
     * @param position
     *            the position of a character
     */
    public void setPosition(Integer position) {
        this.position = position;
    }

    /**
     * Indicates if some other object is equal to this one.
     * 
     * @param object
     *            the reference object with which to compare
     * @return True if this object is the same as the object argument; false
     *         otherwise.
     */
    @Override
    public boolean equals(Object object) {
        if (this == object)
            return true;
        if (object == null || !(object instanceof Character))
            return false;

        Character character = (Character) object;

        if (!(uuid instanceof String)) {
            uuid = UUID.randomUUID().toString();
        }

        return uuid.equals(character.getUuid());
    }

    /**
     * Returns a hash code value for a character.
     * 
     * @return A hash code value for a character.
     */
    @Override
    public int hashCode() {
        if (!(uuid instanceof String)) {
            uuid = UUID.randomUUID().toString();
        }

        return uuid.hashCode();
    }
}
