package se.plilja.imcollect;

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

    Iterable<K> keys();

    Iterable<V> values();

    long size();
}
