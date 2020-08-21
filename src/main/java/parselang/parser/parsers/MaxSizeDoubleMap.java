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

    public Optional<C> getOptional(A a, B b) {
        if (contains(a, b)) {
            return Optional.of(get(a, b));
        } else {
            return Optional.empty();
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

    public void update(A a, B b, Function<C, C> function) {
        cache.get(a).put(b, function.apply(cache.get(a).get(b)));
    }


    private static <K, V> Map<K, V> createLimitedMap(final int maxSize) {
        return new LinkedHashMap<K, V>(maxSize*10/7, 0.7f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
                return size() > maxSize;
            }
        };
    }

    public void computeIfAbsent(A a, B b, Function<Pair<A, B>, C> c) {
        if (maxSize >= 1) {
            cache.computeIfAbsent(a, integer -> createLimitedMap((int) (3 * Math.log(maxSize))));
        } else {
            cache.computeIfAbsent(a, integer -> new HashMap<>());
        }
        cache.get(a).computeIfAbsent(b, b1 -> c.apply(new Pair<>(a, b1)));

    }
}
