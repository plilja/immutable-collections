package se.plilja.imcollect.internal.fingertrees;

import se.plilja.imcollect.internal.Pair;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;

class Single<T, M> extends FingerTree<T, M> {
    private final T value;
    private final Measure<M, T> measure;
    private final M measured;

    Single(T value, Measure<M, T> measure) {
        this.value = value;
        this.measure = measure;
        this.measured = measure.measure(value);
    }

    @Override
    public FingerTree<T, M> pushRight(T newValue) {
        if (value == null) {
            throw new IllegalArgumentException();
        } else {
            return singleToDeep(value, newValue);
        }
    }

    @Override
    FingerTree<T, M> pushLeft(T newValue) {
        if (value == null) {
            throw new IllegalArgumentException();
        } else {
            return singleToDeep(newValue, value);
        }
    }

    private FingerTree<T, M> singleToDeep(T firstValue, T secondValue) {
        MeasureNode<M, T> liftedMeasure = new MeasureNode<>(measure);
        return new Deep<>(
                Digit.one(measure, firstValue),
                new Empty<>(liftedMeasure),
                Digit.one(measure, secondValue),
                measure);
    }

    @Override
    M measured() {
        return measured;
    }

    @Override
    Pair<FingerTree<T, M>, FingerTree<T, M>> split(Predicate<M> predicate) {
        if (predicate.test(measured)) {
            return Pair.make(new Empty<>(measure), this);
        } else {
            return Pair.make(this, new Empty<>(measure));
        }
    }

    @Override
    Split<FingerTree<T, M>, T> splitTree(Predicate<M> predicate, M i) {
        return new Split<>(new Empty<>(measure), value, new Empty<>(measure));
    }

    @Override
    Optional<Pair<T, Supplier<FingerTree<T, M>>>> viewL() {
        return Optional.of(Pair.make(value, () -> new Empty<>(measure)));
    }

    @Override
    Optional<Pair<T, Supplier<FingerTree<T, M>>>> viewR() {
        return Optional.of(Pair.make(value, () -> new Empty<>(measure)));
    }

    @Override
    FingerTree<T, M> app3(LinkedList<T> ts, FingerTree<T, M> xs) {
        return prepend(ts, xs).pushLeft(value);
    }

    @Override
    public Iterator<T> iterator() {
        return new VarArgsIterator<>(value);
    }
}
