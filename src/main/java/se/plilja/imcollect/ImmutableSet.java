package se.plilja.imcollect;

import se.plilja.imcollect.internal.WeightBalancedTreeSet;

import java.util.Comparator;

public interface ImmutableSet<T> extends ImmutableCollection<T> {

    public static <K extends Comparable<K>, V> ImmutableSet<K> empty() {
        return new WeightBalancedTreeSet<>((k1, k2) -> k1.compareTo(k2));
    }

    public static <K, V> ImmutableSet<K> empty(Comparator<K> comparator) {
        return new WeightBalancedTreeSet<>(comparator);
    }
}
