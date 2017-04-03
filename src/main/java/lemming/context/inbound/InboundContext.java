package lemming.context.inbound;

import lemming.context.BaseContext;
import lemming.context.Context;
import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;

/**
 * Class representing an inbound context which is imported from XML.
 */
@BatchSize(size = 30)
@DynamicUpdate
@Entity
@SelectBeforeUpdate
@OptimisticLocking(type = OptimisticLockType.VERSION)
@Table(name = "inbound_context", indexes = {
        @Index(columnList = "hash"),
        @Index(columnList = "keyword, location")
})
public class InboundContext extends BaseContext {
    /**
     * A context group an inbound context belongs to.
     */
    @ManyToOne
    @JoinColumn(name="group_id", nullable = false)
    private InboundContextGroup group;

    /**
     * Creates an instance of an inbound context.
     */
    public InboundContext() {
        super();
    }

    /**
     * Converts an inbound context to a context.
     *
     * @return An inbound context.
     */
    public Context toContext() {
        return new Context(getLocation(), getType(), getKeyword(), getPreceding(), getFollowing(), getInitPunctuation(),
                getEndPunctuation());
    }

    /**
     * Returns the group an inbound context belongs to.
     *
     * @return A context group.
     */
    public InboundContextGroup getGroup() {
        return group;
    }

    /**
     * Sets the group an inbound context belongs to
     *
     * @param group a context group
     */
    public void setGroup(InboundContextGroup group) {
        this.group = group;
    }
}
