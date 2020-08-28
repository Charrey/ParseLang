package parselang.parser.parsers;

import parselang.parser.ParseResult;
import parselang.parser.data.NonTerminal;
import parselang.util.Pair;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public class MaxSizeDoubleMap<A, B, C> {


    private final Map<A, Map<B, C>> cache;
    private final int maxSize;


    public MaxSizeDoubleMap(int maxSize) {
        this.maxSize = maxSize;
        if (maxSize >= 1) {
            cache = createLimitedMap(maxSize);
        } else {
            cache = new HashMap<>();
        }
    }

    public boolean contains(A a, B b) {
        return cache.containsKey(a) && cache.get(a).containsKey(b);
    }

    public C get(A a, B b) {
        return cache.get(a).get(b);
    }

    public void add(A a, B b, C c) {
        if (maxSize >= 1) {
            cache.computeIfAbsent(a, integer -> createLimitedMap((int) (3 * Math.log(maxSize))));
        } else {
            cache.computeIfAbsent(a, a1 -> new HashMap<>());
        }
        cache.get(a).put(b, c);
    }


    private static <K, V> Map<K, V> createLimitedMap(final int maxSize) {
        return new LinkedHashMap<K, V>(maxSize*10/7, 0.7f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
                return size() > maxSize;
            }
        };
    }

}
