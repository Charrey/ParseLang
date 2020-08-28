package parselang.util.collections;

import java.util.Map;
import java.util.Set;

public interface BiMap<K, V> extends Map<K, V> {

    V put(K var1, V var2);

    void putAll(Map<? extends K, ? extends V> var1);

    Set<V> values();

}