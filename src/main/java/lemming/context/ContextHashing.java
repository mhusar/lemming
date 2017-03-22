package lemming.context;

import com.google.common.hash.Hashing;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

/**
 * A helper class creating hashes for base contexts.
 */
public class ContextHashing {
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
     * Returns a SHA512 hash to fingerprint a base context.
     *
     * @param context a base context
     * @return A string representation of a SHA512 hash.
     */
    public static String getSha512(BaseContext context) {
        return Hashing.sha512().hashString(getString(context), StandardCharsets.UTF_8).toString();
    }
}
