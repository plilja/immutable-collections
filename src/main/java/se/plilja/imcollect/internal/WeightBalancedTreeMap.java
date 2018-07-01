package se.plilja.imcollect.internal;

import se.plilja.imcollect.ImmutableMap;

import java.util.Comparator;
import java.util.Map;
import java.util.Optional;

public final class WeightBalancedTreeMap<K, V> implements ImmutableMap<K, V> {
    private final WeightBalancedTree<Pair<K, V>> tree;

    private WeightBalancedTreeMap(WeightBalancedTree<Pair<K, V>> tree) {
        this.tree = tree;
    }

    public WeightBalancedTreeMap(Comparator<K> comparator) {
        this.tree = new WeightBalancedTree<>((p1, p2) -> comparator.compare(p1.first, p2.first));
    }

    @Override
    public Optional<V> lookup(K key) {
        if (key == null) {
            return Optional.empty();
        } else {
            return tree.lookup(Pair.make(key, null)).map(p -> p.second);
        }
    }

    @Override
    public WeightBalancedTreeMap<K, V> put(K key, V value) {
        if (key == null) {
            throw new IllegalArgumentException("Null keys are not supported");
        }
        if (value == null) {
            throw new IllegalArgumentException("Null values are not supported");
        }
        return new WeightBalancedTreeMap<>(tree.add(Pair.make(key, value)));
    }

    @Override
    public WeightBalancedTreeMap<K, V> putAll(Map<K, V> map) {
        return (WeightBalancedTreeMap<K, V>) ImmutableMap.super.putAll(map);
    }

    @Override
    public WeightBalancedTreeMap<K, V> putAll(ImmutableMap<K, V> map) {
        return (WeightBalancedTreeMap<K, V>) ImmutableMap.super.putAll(map);
    }

    @Override
    public WeightBalancedTreeMap<K, V> remove(K key) {
        if (key == null) {
            return this;
        }
        return new WeightBalancedTreeMap<>(tree.remove(Pair.make(key, null)));
    }

    @Override
    public Iterable<K> keys() {
        return () -> tree.iterator(p -> p.first);
    }

    @Override
    public Iterable<V> values() {
        return () -> tree.iterator(p -> p.second);
    }

    @Override
    public int size() {
        return tree.size();
    }
}
