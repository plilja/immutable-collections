package se.plilja.imcollect;

// TODO not public
public final class Pair<A, B> {
    public final A first;
    public final B second;

    public Pair(A first, B second) {
        this.first = first;
        this.second = second;
    }

    public static <A, B> Pair<A, B> make(A first, B second) {
        return new Pair<>(first, second);
    }
}