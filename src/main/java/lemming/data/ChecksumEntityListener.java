package lemming.data;

import lemming.context.BaseContext;

import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

/**
 * Sets a checksum for an entity if its class is recognized.
 */
public class ChecksumEntityListener {
    /**
     * Called on pre-persist.
     *
     * @param object an object
     */
    @PrePersist
    public void onPrePersist(Object object) {
        setChecksum(object);
    }

    /**
     * Called on pre-update.
     *
     * @param object an object
     */
    @PreUpdate
    public void onPreUpdate(Object object) {
        setChecksum(object);
    }

    /**
     * Sets a checksum for an entity if its class is recognized.
     *
     * @param object an object
     */
    private void setChecksum(Object object) {
        if (object instanceof BaseContext) {
            BaseContext context = (BaseContext) object;
            context.setChecksum();
        } else {
            throw new IllegalStateException("Cannot set checksum to unknown class");
        }
    }
}
