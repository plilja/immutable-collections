package se.plilja.imcollect;

public interface ImmutableList<T> extends ImmutableCollection<T> {

    T get(long idx);

    ImmutableList<T> set(long idx, T value);

    long indexOf(T value);

    long lastIndexOf(T value);
}
