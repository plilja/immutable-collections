package se.plilja.imcollect;

import se.plilja.imcollect.internal.fingertrees.FingerTreeList;

public interface ImmutableList<T> extends ImmutableCollection<T> {

    @Override
    ImmutableList<T> add(T t);

    @Override
    default ImmutableList<T> addAll(Iterable<? extends T> values) {
        return (ImmutableList<T>) ImmutableCollection.super.addAll(values);
    }

    @Override
    ImmutableList<T> remove(T value);

    @Override
    default ImmutableList<T> removeAll(Iterable<? extends T> values) {
        return (ImmutableList<T>) ImmutableCollection.super.removeAll(values);
    }

    @Override
    default ImmutableList<T> retainAll(Iterable<? extends T> values) {
        return (ImmutableList<T>) ImmutableCollection.super.retainAll(values);
    }

    T get(int idx);

    ImmutableList<T> set(int idx, T value);

    /**
     * Finds the first index of a given value.
     *
     * @param value Value to look for
     * @return The leftmost index of the value or -1 if the value is not present in the list.
     */
    int indexOf(T value);

    /**
     * @param value Value to look for
     * @return The rightmost index of the value or -1 if the value is not present in the list.
     */
    int lastIndexOf(T value);

    default boolean contains(T value) {
        return indexOf(value) != -1;
    }

    @SafeVarargs
    public static <T> ImmutableList<T> of(T... ts) {
        ImmutableList<T> r = empty();
        for (T t : ts) {
            r = r.add(t);
        }
        return r;
    }

    public static <T> ImmutableList<T> empty() {
        return FingerTreeList.empty();
    }
}
