package lemming.context.verfication;

import lemming.context.BaseContext;
import org.hibernate.annotations.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
import java.sql.Timestamp;

/**
 * Class representing a context which is imported from XML.
 */
@BatchSize(size = 30)
@DynamicUpdate
@Entity
@SelectBeforeUpdate
@OptimisticLocking(type = OptimisticLockType.VERSION)
@Table(name = "unverified_context", indexes = {
        @Index(columnList = "checksum"),
        @Index(columnList = "keyword, location"),
        @Index(columnList = "user, timestamp")})
public class UnverifiedContext extends BaseContext {
    /**
     * A timestamp to save the import time of a KWIC index file.
     *
     * All contexts of one file must have the same timestamp.
     */
    @Column(name = "timestamp", nullable = false)
    @Type(type = "timestamp")
    private Timestamp timestamp;

    /**
     * The real name of a user as string.
     */
    @Column(name = "user", nullable = false)
    private String user;

    /**
     * Returns the timestamp of a context.
     *
     * @return A timestamp
     */
    public Timestamp getTimestamp() {
        return timestamp;
    }

    /**
     * Sets the timestamp of a context.
     *
     * @param timestamp a timestamp
     */
    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * Returns the user string of a context.
     *
     * @return A string.
     */
    public String getUser() {
        return user;
    }

    /**
     * Sets the user string of a context.
     *
     * @param user a string
     */
    public void setUser(String user) {
        this.user = user;
    }
}
