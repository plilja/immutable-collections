package se.plilja.imcollect;

import se.plilja.imcollect.internal.WeightBalancedTreeMap;

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

    public static <K extends Comparable<K>, V> ImmutableMap<K, V> singletonMap(K key, V value) {
        return ImmutableMap.<K, V>empty().put(key, value);
    }

    public static <K, V> ImmutableMap<K, V> singletonMap(Comparator<K> comparator, K key, V value) {
        return ImmutableMap.<K, V>empty(comparator).put(key, value);
    }

    public static <K extends Comparable<K>, V> ImmutableMap<K, V> empty() {
        return new WeightBalancedTreeMap<K, V>(Comparator.naturalOrder());
    }

    public static <K, V> ImmutableMap<K, V> empty(Comparator<K> comparator) {
        return new WeightBalancedTreeMap<>(comparator);
    }
}
