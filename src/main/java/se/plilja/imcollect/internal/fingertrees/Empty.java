package se.plilja.imcollect.internal.fingertrees;

import se.plilja.imcollect.internal.Pair;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;

class Empty<T, M> extends FingerTree<T, M> {
    private final Measure<M, T> measure;

    Empty(Measure<M, T> measure) {
        this.measure = measure;
    }

    @Override
    public FingerTree<T, M> pushRight(T value) {
        if (value == null) {
            throw new IllegalArgumentException();
        } else {
            return new Single<>(value, measure);
        }
    }

    @Override
    FingerTree<T, M> pushLeft(T value) {
        return pushRight(value);
    }

    @Override
    M measured() {
        return measure.identity();
    }

    @Override
    Pair<FingerTree<T, M>, FingerTree<T, M>> split(Predicate<M> predicate) {
        return Pair.make(this, this);
    }

    @Override
    Split<FingerTree<T, M>, T> splitTree(Predicate<M> predicate, M i) {
        throw new UnsupportedOperationException();
    }

    @Override
    Optional<Pair<T, Supplier<FingerTree<T, M>>>> viewL() {
        return Optional.empty();
    }

    @Override
    Optional<Pair<T, Supplier<FingerTree<T, M>>>> viewR() {
        return Optional.empty();
    }

    @Override
    FingerTree<T, M> app3(LinkedList<T> ts, FingerTree<T, M> xs) {
        return prepend(ts, xs);
    }

    @Override
    public Iterator<T> iterator() {
        return new VarArgsIterator<T>();
    }
}