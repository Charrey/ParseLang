package parselang.parser.parsers;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * Two-key map that has a limited capacity and removes the oldest entry when it is full
 * @param <A> type of the first key
 * @param <B> type of the second key
 * @param <C> type of the value
 */
public class MaxSizeDoubleMap<A, B, C> {


    private final Map<A, Map<B, C>> cache;
    private final int maxSize;

    /**
     * Creates a new MaxSizeDoubleMap
     * @param maxSize maximum size before entries start being thrown away
     */
    public MaxSizeDoubleMap(int maxSize) {
        this.maxSize = maxSize;
        if (maxSize >= 1) {
            cache = createLimitedMap(maxSize);
        } else {
            cache = new HashMap<>();
        }
    }

    /**
     * Returns whether the map contains a key-key pair as key
     * @param a first key
     * @param b second key
     * @return whether it contains this pair of keys
     */
    public boolean contains(A a, B b) {
        return cache.containsKey(a) && cache.get(a).containsKey(b);
    }

    /**
     * Returns the value associated with two keys
     * @param a the first key
     * @param b the second key
     * @return the value associated with the two keys
     * @throws NoSuchElementException if the key pair is not a key in this map.
     */
    public C get(A a, B b) {
        if (!contains(a, b)) {
            throw new NoSuchElementException();
        }
        return cache.get(a).get(b);
    }

    /**
     * Puts a new key-key-value triplet into this map.
     * @param a First key
     * @param b Second key
     * @param c Value
     */
    public void put(A a, B b, C c) {
        if (maxSize >= 1) {
            cache.computeIfAbsent(a, integer -> createLimitedMap((int) (3 * Math.log(maxSize))));
        } else {
            cache.computeIfAbsent(a, a1 -> new HashMap<>());
        }
        cache.get(a).put(b, c);
    }


    private static <K, V> Map<K, V> createLimitedMap(final int maxSize) {
        return new LinkedHashMap<>(maxSize * 10 / 7, 0.7f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
                return size() > maxSize;
            }
        };
    }

}
