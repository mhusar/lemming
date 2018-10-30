package lemming.data;

import com.google.common.hash.Hashing;
import lemming.context.BaseContext;

import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.nio.charset.StandardCharsets;

/**
 * Sets a hash for an entity if its class is recognized.
 */
@SuppressWarnings("unused")
public class HashEntityListener {
    /**
     * Called on pre-persist.
     *
     * @param object an object
     */
    @PrePersist
    public void onPrePersist(Object object) {
        setHash(object);
    }

    /**
     * Called on pre-update.
     *
     * @param object an object
     */
    @PreUpdate
    public void onPreUpdate(Object object) {
        setHash(object);
    }

    /**
     * Returns a SHA512 hash of an input string.
     *
     * @param input input string
     * @return A string representation of a SHA512 hash.
     */
    private String getSha512(String input) {
        return Hashing.sha512().hashString(input, StandardCharsets.UTF_8).toString();
    }

    /**
     * Sets a hash for an entity if its class is recognized.
     *
     * @param object an object
     */
    private void setHash(Object object) {
        if (object instanceof BaseContext) {
            BaseContext context = (BaseContext) object;
            String hash = getSha512(context.toString("\u001F\u001F"));
            context.setHash(hash);
        } else {
            throw new IllegalStateException("Unknown entity: " + object.getClass().getCanonicalName());
        }
    }
}
