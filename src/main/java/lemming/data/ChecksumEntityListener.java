package lemming.data;

import com.google.common.hash.Hashing;
import lemming.context.BaseContext;

import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

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
     * Returns a SHA512 hash of an input string.
     *
     * @param input input string
     * @return A string representation of a SHA512 hash.
     */
    private static String getSha512(String input) {
        return Hashing.sha512().hashString(input, StandardCharsets.UTF_8).toString();
    }

    /**
     * Returns a string concatenating the text elements of the original context.
     *
     * @param context a base context
     * @return A string of arbitrary length.
     */
    private static String getString(BaseContext context) {
        return String.join("\u001F\u001F", new String[] {
                context.getPreceding(),
                Optional.ofNullable(context.getInitPunctuation()).orElse(""),
                context.getKeyword(),
                Optional.ofNullable(context.getEndPunctuation()).orElse(""),
                context.getFollowing()
        });
    }

    /**
     * Sets a checksum for an entity if its class is recognized.
     *
     * @param object an object
     */
    public static void setChecksum(Object object) {
        if (object instanceof BaseContext) {
            BaseContext context = (BaseContext) object;
            context.setChecksum(getSha512(getString(context)));
        } else {
            throw new IllegalStateException("Can’t set a checksum to an unknown class");
        }
    }
}
