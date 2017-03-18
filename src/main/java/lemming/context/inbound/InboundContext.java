package lemming.context.inbound;

import lemming.context.BaseContext;
import org.hibernate.annotations.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.sql.Timestamp;

/**
 * Class representing a context which is imported from XML.
 */
@BatchSize(size = 30)
@DynamicUpdate
@Entity
@SelectBeforeUpdate
@OptimisticLocking(type = OptimisticLockType.VERSION)
@javax.persistence.Table(name = "inbound_context", indexes = {
        @javax.persistence.Index(columnList = "uuid", unique = true),
        @javax.persistence.Index(columnList = "keyword, location"),
        @javax.persistence.Index(columnList = "user, timestamp")})
public class InboundContext extends BaseContext {
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
     * @param user a user string
     */
    public void setUserString(String user) {
        this.user = user;
    }
}
