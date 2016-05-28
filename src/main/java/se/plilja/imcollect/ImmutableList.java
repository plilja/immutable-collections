package se.plilja.imcollect;

public interface ImmutableList<T> extends ImmutableCollection<T> {

    T get(int idx);

    ImmutableList<T> set(int idx, T value);

    int indexOf(T value);

    int lastIndexOf(T value);
}
