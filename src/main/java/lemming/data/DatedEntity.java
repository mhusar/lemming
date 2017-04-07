package lemming.data;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.sql.Timestamp;

/**
 * A base entity class with created and modified fields.
 */
@MappedSuperclass
@SuppressWarnings("unused")
public abstract class DatedEntity {
    /**
     * Creation timestamp of the containing entity.
     */
    @Column(name = "created")
    @Type(type = "timestamp")
    @CreationTimestamp
    private Timestamp created;

    /**
     * Update timestamp of the containing entity.
     */
    @Column(name = "modified")
    @Type(type = "timestamp")
    @UpdateTimestamp
    private Timestamp modified;

    /**
     * Returns the creation timestamp of the containing entity.
     *
     * @return A timestamp.
     */
    public Timestamp getCreated() {
        return created;
    }

    /**
     * Returns the update timestamp of the containing entity.
     *
     * @return A timestamp.
     */
    public Timestamp getModified() {
        return modified;
    }
}
