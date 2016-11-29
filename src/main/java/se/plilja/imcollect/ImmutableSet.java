package se.plilja.imcollect;

import java.util.Comparator;

public interface ImmutableSet<T> extends ImmutableCollection<T> {

    static <K extends Comparable<K>, V> ImmutableSet<K> empty() {
        return new WeightBalancedTreeSet<K>((k1, k2) -> k1.compareTo(k2));
    }

    static <K, V> ImmutableSet<K> empty(Comparator<K> comparator) {
        return new WeightBalancedTreeSet<K>(comparator);
    }
}
