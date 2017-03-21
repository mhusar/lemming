package lemming.context.inbound;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * A helper class which holds data about groups of inbound contexts.
 */
public class InboundContextSummary implements Serializable {
    /**
     * Number of contexts.
     */
    private Long numberOfContexts;

    /**
     * Timestamp of inbound contexts.
     */
    private Timestamp timestamp;

    /**
     * User string of inbound contexts.
     */
    private String userString;

    /**
     * Location where contexts are beginning.
     */
    private String beginLocation;

    /**
     * Location where contexts are ending.
     */
    private String endLocation;

    /**
     * Private default constructor.
     */
    private InboundContextSummary() {
        // does nothing
    }

    /**
     * Creates an InboundContextSummary object.
     *
     * @param numberOfContexts number of inbound contexts
     * @param timestamp timestamp of contexts
     * @param userString user string of contexts
     * @param beginLocation begin location
     * @param endLocation end location
     */
    public InboundContextSummary(Long numberOfContexts, Timestamp timestamp, String userString,
                                 String beginLocation, String endLocation) {
        this.numberOfContexts = numberOfContexts;
        this.timestamp = timestamp;
        this.userString = userString;
        this.beginLocation = beginLocation;
        this.endLocation = endLocation;
    }

    /**
     * Returns the number of inbound contexts.
     *
     * @return An integer.
     */
    public Long getNumberOfContexts() {
        return numberOfContexts;
    }

    /**
     * Sets the number of inbound contexts.
     *
     * @param numberOfContexts number of inbound contexts
     */
    public void setNumberOfContexts(Long numberOfContexts) {
        this.numberOfContexts = numberOfContexts;
    }

    /**
     * Returns timestamp of contexts.
     *
     * @return A timestamp
     */
    public Timestamp getTimestamp() {
        return timestamp;
    }

    /**
     * Sets the timestamp of contexts.
     *
     * @param timestamp a timestamp
     */
    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * Returns the user string string of contexts.
     *
     * @return A string.
     */
    public String getUserString() {
        return userString;
    }

    /**
     * Sets the user string string of contexts.
     *
     * @param user a string
     */
    public void setUserString(String user) {
        this.userString = user;
    }

    /**
     * Returns the location where contexts are beginning.
     *
     * @return Begin location string.
     */
    public String getBeginLocation() {
        return beginLocation;
    }

    /**
     * Sets the location where contexts are beginning.
     *
     * @param beginLocation begin location
     */
    public void setBeginLocation(String beginLocation) {
        this.beginLocation = beginLocation;
    }

    /**
     * Returns the location where contexts are ending.
     *
     * @return End location string.
     */
    public String getEndLocation() {
        return endLocation;
    }

    /**
     * Sets the location where contexts are ending.
     *
     * @param endLocation end location
     */
    public void setEndLocation(String endLocation) {
        this.endLocation = endLocation;
    }
}
