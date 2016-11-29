package se.plilja.imcollect;

import java.util.Comparator;
import java.util.Map;
import java.util.Optional;

public interface ImmutableMap<K, V> {

    default V get(K key) {
        return lookup(key).orElse(null);
    }

    Optional<V> lookup(K key);

    default boolean contains(K key) {
        return lookup(key).isPresent();
    }

    ImmutableMap<K, V> put(K key, V value);

    ImmutableMap<K, V> remove(K key);

    default ImmutableMap<K, V> putAll(Map<K, V> map) {
        ImmutableMap<K, V> res = this;
        for (Map.Entry<K, V> entry : map.entrySet()) {
            res = res.put(entry.getKey(), entry.getValue());
        }
        return res;
    }

    default ImmutableMap<K, V> putAll(ImmutableMap<K, V> map) {
        ImmutableMap<K, V> res = this;
        for (K key : map.keys()) {
            res = res.put(key, map.get(key));
        }
        return res;
    }

    Iterable<K> keys();

    Iterable<V> values();

    int size();

    static <K extends Comparable<K>, V> ImmutableMap<K, V> empty() {
        return new WeightBalancedTreeMap<K, V>((k1, k2) -> k1.compareTo(k2));
    }

    static <K, V> ImmutableMap<K, V> empty(Comparator<K> comparator) {
        return new WeightBalancedTreeMap<K, V>(comparator);
    }
}
