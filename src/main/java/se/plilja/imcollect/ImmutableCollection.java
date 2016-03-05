package se.plilja.imcollect;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public interface ImmutableCollection<T> extends Iterable<T> {

    ImmutableCollection<T> add(T value);

    default ImmutableCollection<T> addAll(Iterable<? extends T> values) {
        ImmutableCollection<T> res = this;
        for (T value : values) {
            res = res.add(value);
        }
        return res;
    }

    ImmutableCollection<T> remove(T value);

    default boolean isEmpty() {
        return size() == 0;
    }

    long size();

    boolean contains(T val);

    default boolean containsAll(Iterable<? extends T> values) {
        boolean res = true;
        for (T value : values) {
            res = res && contains(value);
        }
        return res;
    }

    default ImmutableCollection<T> removeAll(Iterable<? extends T> values) {
        HashSet<T> s = new HashSet<>();
        for (T value : values) {
            s.add(value);
        }
        ImmutableCollection<T> res = this;
        for (T t : this) {
            if (s.contains(t)) {
                res = res.remove(t);
            }
        }
        return res;
    }

    default ImmutableCollection<T> retainAll(Iterable<? extends T> values) {
        HashSet<T> s = new HashSet<>();
        for (T value : values) {
            s.add(value);
        }
        ImmutableCollection<T> res = this;
        for (T value : this) {
            if (!s.contains(value)) {
                res = res.remove(value);
            }
        }
        return res;
    }

    default Collection<T> toMutableCollection() {
        ArrayList<T> res = new ArrayList<>();
        for (T t : this) {
            res.add(t);
        }
        return res;
    }

    default Stream<T> stream() {
        return StreamSupport.stream(Spliterators.spliterator(iterator(), size(), 0), false);
    }

    default Stream<T> parallelStream() {
        return StreamSupport.stream(Spliterators.spliterator(iterator(), size(), 0), false);
    }
}
