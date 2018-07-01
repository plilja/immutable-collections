package se.plilja.imcollect;

import se.plilja.imcollect.internal.WeightBalancedTreeSet;

import java.util.Comparator;

public interface ImmutableSet<T> extends ImmutableCollection<T> {

    @Override
    ImmutableSet<T> add(T t);

    @Override
    default ImmutableSet<T> addAll(Iterable<? extends T> values) {
        return (ImmutableSet<T>) ImmutableCollection.super.addAll(values);
    }

    @Override
    ImmutableSet<T> remove(T value);

    @Override
    default ImmutableSet<T> removeAll(Iterable<? extends T> values) {
        return (ImmutableSet<T>) ImmutableCollection.super.removeAll(values);
    }

    @Override
    default ImmutableSet<T> retainAll(Iterable<? extends T> values) {
        return (ImmutableSet<T>) ImmutableCollection.super.retainAll(values);
    }

    public static <K extends Comparable<K>> ImmutableSet<K> of(K... ks) {
        ImmutableSet<K> r = empty();
        for (K k : ks) {
            r = r.add(k);
        }
        return r;
    }

    public static <K> ImmutableSet<K> of(Comparator<K> comparator, K... ks) {
        ImmutableSet<K> r = empty(comparator);
        for (K k : ks) {
            r = r.add(k);
        }
        return r;
    }

    public static <K extends Comparable<K>> ImmutableSet<K> empty() {
        return new WeightBalancedTreeSet<>((k1, k2) -> k1.compareTo(k2));
    }

    public static <K> ImmutableSet<K> empty(Comparator<K> comparator) {
        return new WeightBalancedTreeSet<>(comparator);
    }
}
