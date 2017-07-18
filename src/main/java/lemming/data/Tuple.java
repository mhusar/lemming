package lemming.data;

import java.util.AbstractMap;

/**
 * An Entry maintaining a key and a value. The value may be changed using the setValue method.
 *
 * @param <K> key data type
 * @param <V> value data type
 * @see java.util.AbstractMap.SimpleEntry
 */
public class Tuple<K, V> extends AbstractMap.SimpleEntry<K, V> {

    /**
     * Creates a tuple.
     *
     * @param key   key object
     * @param value value object
     */
    public Tuple(K key, V value) {
        super(key, value);
    }
}
