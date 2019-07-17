package lemming.context;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lemming.context.inbound.InboundContext;
import lemming.lemma.Lemma;
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
import java.util.Set;
import java.util.SortedSet;

/**
 * Class representing a context.
 */
@BatchSize(size = 30)
@DynamicUpdate
@Entity
@SelectBeforeUpdate
@OptimisticLocking(type = OptimisticLockType.VERSION)
@Table(name = "context", indexes = {
        @Index(columnList = "uuid", unique = true),
        @Index(columnList = "keyword, preceding, following, location, number, pos_string, lemma_string, interesting")})
public class Context extends BaseContext implements Comparable<Context>, Serializable {
    /**
     * Determines if a deserialized file is compatible with this class.
     */
    private static final long serialVersionUID = 1L;

    /**
     * A set of replacements for a context.
     */
    @OneToMany(mappedBy = "match", fetch = FetchType.LAZY)
    private Set<InboundContext> replacements;

    /**
     * Part of speech of a keyword in context.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pos_id")
    private Pos pos;

    /**
     * Part of speech of a context as string.
     * <p>
     * For better performance of the context index table.
     */
    @Column(name = "pos_string", length = 120)
    @JsonIgnore
    private String posString;

    /**
     * Lemma of a keyword in context.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lemma_id")
    private Lemma lemma;

    /**
     * Lemma of a context as string.
     * <p>
     * For better performance of the context index table.
     */
    @Column(name = "lemma_string", length = 120)
    @JsonIgnore
    private String lemmaString;

    /**
     * Sense of a keyword in context.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sense_id")
    @JsonIgnore
    private Sense sense;

    /**
     * Comment of a context.
     */
    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name = "context_comments", indexes = {@Index(columnList = "context_id, comment_id", unique = true)},
            joinColumns = {@JoinColumn(name = "context_id", nullable = false, updatable = false)},
            inverseJoinColumns = {@JoinColumn(name = "comment_id", nullable = false, updatable = false)})
    @OrderBy("modified")
    private SortedSet<Comment> comments;

    /**
     * Parent of a context group.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinTable(name="context_group_members",
            joinColumns={@JoinColumn(name="member_id", insertable=false, updatable=false)},
            inverseJoinColumns={@JoinColumn(name="group_id", insertable=false, updatable=false)})
    private Context group;

    /**
     * Group members of a context group.
     */
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name = "context_group_members", indexes = {@Index(columnList = "group_id, member_id", unique = true)},
            joinColumns = {@JoinColumn(name = "group_id", nullable = false, updatable = false)},
            inverseJoinColumns = {@JoinColumn(name = "member_id", nullable = false, updatable = false)})
    @OrderBy("number")
    private SortedSet<Context> members;

    /**
     * Group type of a context.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "group_type", length = 30, nullable = false)
    private ContextGroupType.Type groupType = ContextGroupType.Type.NONE;

    /**
     * Interesting state of a context.
     * <p>
     * True, if a context is interesting for the glossary.
     */
    @Column(name = "interesting", nullable = false)
    private Boolean interesting;

    /**
     * Selected state of a context.
     */
    @Transient
    @JsonIgnore
    private Boolean selected;

    /**
     * Creates an instance of a context.
     */
    public Context() {
    }

    /**
     * Creates an instance of a context.
     *
     * @param location        location of a context
     * @param number          number of a context
     * @param type            type of a context
     * @param keyword         keyword of a context
     * @param preceding       preceding text of a context
     * @param following       following text of a context
     * @param initPunctuation punctuation preceding the keyword
     * @param endPunctuation  punctuation following the keyword
     * @param speech          speech type of a context
     */
    public Context(String location, Integer number, ContextType.Type type, String keyword, String preceding, String following,
                   String initPunctuation, String endPunctuation, SpeechType.Type speech) {
        super(location, number, type, keyword, preceding, following, initPunctuation, endPunctuation, speech);
    }

    /**
     * Returns the replacements for a context.
     *
     * @return A set of replacements.
     */
    public Set<InboundContext> getReplacements() {
        return replacements;
    }

    /**
     * Returns the part of speech of a context.
     *
     * @return Part of speech of a context.
     */
    public Pos getPos() {
        return pos;
    }

    /**
     * Sets the part of speech of a context.
     *
     * @param pos part of speech of a context
     */
    public void setPos(Pos pos) {
        this.pos = pos;
    }

    /**
     * Returns the part of speech of a context as string.
     *
     * @return Part of speech of a context as string.
     */
    public String getPosString() {
        return posString;
    }

    /**
     * Sets the part of speech string of a context.
     *
     * @param posString part of speech string of a context
     */
    public void setPosString(String posString) {
        this.posString = posString;
    }

    /**
     * Returns the lemma of a context.
     *
     * @return Lemma of a context.
     */
    public Lemma getLemma() {
        return lemma;
    }

    /**
     * Sets the lemma of a context.
     *
     * @param lemma lemma of a context
     */
    public void setLemma(Lemma lemma) {
        this.lemma = lemma;
    }

    /**
     * Returns the lemma of a context as string.
     *
     * @return Lemma of a context as string.
     */
    public String getLemmaString() {
        return lemmaString;
    }

    /**
     * Sets the lemma string of a context.
     *
     * @param lemmaString lemma string of a context
     */
    public void setLemmaString(String lemmaString) {
        this.lemmaString = lemmaString;
    }

    /**
     * Returns the sense of a context.
     *
     * @return Sense of a context.
     */
    public Sense getSense() {
        return sense;
    }

    /**
     * Sets the sense of a context.
     *
     * @param sense sense of a context
     */
    public void setSense(Sense sense) {
        this.sense = sense;
    }

    /**
     * Returns the comments of a context.
     *
     * @return Comments of a context.
     */
    public Set<Comment> getComments() {
        return comments;
    }

    /**
     * Returns the parent of a context group.
     *
     * @return Parent of a context.
     */
    public Context getGroup() {
        return group;
    }

    /**
     * Returns the members of a context group.
     *
     * @return Members of a context group.
     */
    public Set<Context> getMembers() {
        return members;
    }

    /**
     * Returns the context group type of a context.
     *
     * @return Returns the context group type of a context.
     */
    public ContextGroupType.Type getGroupType() {
        return groupType;
    }

    /**
     * Sets the context group type of a context.
     *
     * @param groupType context group type
     */
    public void setGroupType(ContextGroupType.Type groupType) {
        this.groupType = groupType;
    }

    /**
     * Returns the interesting state of a context.
     *
     * @return Interesting state of a context.
     */
    public Boolean getInteresting() {
        return interesting;
    }

    /**
     * Sets the interesting state of a context.
     *
     * @param interesting interesting state of a context
     */
    public void setInteresting(Boolean interesting) {
        this.interesting = interesting;
    }

    /**
     * Returns the selected state of a context.
     *
     * @return Selected state of a context.
     */
    public Boolean getSelected() {
        if (selected != null) {
            return selected;
        } else {
            return false;
        }
    }

    /**
     * Sets the selected state of a context.
     *
     * @param selected selected state of a context
     */
    public void setSelected(@SuppressWarnings("SameParameterValue") Boolean selected) {
        this.selected = selected;
    }

    /**
     * Compares a context to another context.
     *
     * @param context a context.
     * @return The result of the comparison.
     * @see Comparable
     */
    @Override
    public int compareTo(Context context) {
        return getNumber().compareTo(context.getNumber());
    }
}
