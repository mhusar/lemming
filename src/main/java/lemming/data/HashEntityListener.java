package lemming.data;

import com.google.common.hash.Hashing;
import lemming.context.BaseContext;

import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

/**
 * Sets a hash for an entity if its class is recognized.
 */
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
     * Returns a string concatenating the text elements of the original context.
     *
     * @param context a base context
     * @return A string of arbitrary length.
     */
    private String getString(BaseContext context) {
        return String.join("\u001F\u001F", new String[] {
                context.getPreceding(),
                Optional.ofNullable(context.getInitPunctuation()).orElse(""),
                context.getKeyword(),
                Optional.ofNullable(context.getEndPunctuation()).orElse(""),
                context.getFollowing()
        });
    }

    /**
     * Sets a hash for an entity if its class is recognized.
     *
     * @param object an object
     */
    private void setHash(Object object) {
        if (object instanceof BaseContext) {
            BaseContext context = (BaseContext) object;
            context.setHash(getSha512(getString(context)));
        } else {
            throw new IllegalStateException("Unknown entity: " + object.getClass().getCanonicalName());
        }
    }
}
