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
     * A context package an inbound context belongs to.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "package_id", nullable = false)
    private InboundContextPackage _package;

    /**
     * A matching context for an inbound context.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "match_id")
    private Context match;

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
    @SuppressWarnings("unused")
    public Context toContext() {
        return new Context(getLocation(), getNumber(), getType(), getKeyword(), getPreceding(), getFollowing(),
                getInitPunctuation(), getEndPunctuation());
    }

    /**
     * Returns the package an inbound context belongs to.
     *
     * @return A context package.
     */
    @SuppressWarnings("unused")
    public InboundContextPackage getPackage() {
        return _package;
    }

    /**
     * Sets the package an inbound context belongs to
     *
     * @param _package a context package
     */
    public void setPackage(InboundContextPackage _package) {
        this._package = _package;
    }

    /**
     * Returns the matching context for an inbound context.
     *
     * @return A matching context or null.
     */
    public Context getMatch() {
        return match;
    }

    /**
     * Set the matching context for an inbound context.
     *
     * @param match matching context
     */
    public void setMatch(Context match) {
        this.match = match;
    }
}
