package parselang.util.collections;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;

public class HashBiMap<K, V> implements BiMap<K, V> {

    private Map<K, V> kToV = new HashMap<>();
    private Map<V, K> vToK = new HashMap<>();

    public HashBiMap(Map<K, V> kToV, Map<V, K> vToK) {
        this.kToV = kToV;
        this.vToK = vToK;
    }

    public HashBiMap() {

    }


    @Override
    public int size() {
        return kToV.size();
    }

    @Override
    public boolean isEmpty() {
        return kToV.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return kToV.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return vToK.containsKey(value);
    }

    @Override
    public V get(Object key) {
        return kToV.get(key);
    }

    @Override
    public V put(K var1, V var2) {
        V toReturn = kToV.getOrDefault(var1, null);
        kToV.put(var1, var2);
        vToK.put(var2, var1);
        return toReturn;
    }

    @Override
    public V remove(Object key) {
        V toReturn = kToV.getOrDefault(key, null);
        if (toReturn != null) {
            kToV.remove(key);
            vToK.remove(toReturn);
        }
        return toReturn;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> var1) {
        var1.forEach((BiConsumer<K, V>) this::put);
    }

    @Override
    public void clear() {
        kToV.clear();
        vToK.clear();
    }

    @Override
    public Set<K> keySet() {
        return kToV.keySet();
    }

    @Override
    public Set<V> values() {
        return vToK.keySet();
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return kToV.entrySet();
    }

    @Override
    public BiMap<V, K> inverse() {
        return new HashBiMap<>(vToK, kToV);
    }
}
