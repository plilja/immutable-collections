package se.plilja.imcollect;

import se.plilja.imcollect.internal.fingertrees.FingerTreeList;

public interface ImmutableList<T> extends ImmutableCollection<T> {

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

    static <T> ImmutableList<T> empty() {
        return FingerTreeList.empty();
    }
}
