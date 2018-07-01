package se.plilja.imcollect.internal.fingertrees;

import se.plilja.imcollect.internal.Pair;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;

abstract class FingerTree<T, M> implements Iterable<T> {

    public abstract FingerTree<T, M> pushRight(T value);

    abstract FingerTree<T, M> pushLeft(T value);

    abstract M measured();

    final T peekLeft() {
        return viewL()
                .map(view -> view.first)
                .orElseThrow(() -> new NoSuchElementException());
    }

    final T peekRight() {
        return viewR()
                .map(view -> view.first)
                .orElseThrow(() -> new NoSuchElementException());
    }

    final FingerTree<T, M> popLeft() {
        return viewL()
                .map(view -> view.second.get())
                .orElseThrow(() -> new NoSuchElementException());
    }

    final FingerTree<T, M> popRight() {
        return viewR()
                .map(view -> view.second.get())
                .orElseThrow(() -> new NoSuchElementException());
    }

    abstract Pair<FingerTree<T, M>, FingerTree<T, M>> split(Predicate<M> predicate);

    abstract Split<FingerTree<T, M>, T> splitTree(Predicate<M> predicate, M i);

    abstract Optional<Pair<T, Supplier<FingerTree<T, M>>>> viewL();

    abstract Optional<Pair<T, Supplier<FingerTree<T, M>>>> viewR();

    final FingerTree<T, M> concat(FingerTree<T, M> other) {
        LinkedList<T> empty = new LinkedList<>();
        return app3(empty, other);
    }

    /**
     * Join this finger tree with a list of T:s (ts) and another finger tree (xs).
     */
    abstract FingerTree<T, M> app3(LinkedList<T> ts, FingerTree<T, M> xs);

    static <T, M> FingerTree<T, M> prepend(LinkedList<T> toPrepend, FingerTree<T, M> tree) {
        FingerTree<T, M> res = tree;
        Iterator<T> it = toPrepend.descendingIterator();
        while (it.hasNext()) {
            T t = it.next();
            res = res.pushLeft(t);
        }
        return res;
    }

    static <T, M> FingerTree<T, M> append(FingerTree<T, M> tree, LinkedList<T> toAppend) {
        FingerTree<T, M> res = tree;
        for (T t : toAppend) {
            res = res.pushRight(t);
        }
        return res;
    }
}
