package se.plilja.imcollect.internal.fingertrees;


import se.plilja.imcollect.internal.Pair;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;

class Deep<T, M> extends FingerTree<T, M> {
    private final Digit<T, M> prefix;
    private final FingerTree<Node<T, M>, M> middle;
    private final Digit<T, M> suffix;
    private final Measure<M, T> measure;
    private final M measured;

    Deep(Digit<T, M> prefix, FingerTree<Node<T, M>, M> middle, Digit<T, M> suffix, Measure<M, T> measure) {
        assert prefix.size() > 0;
        assert suffix.size() > 0;
        this.prefix = prefix;
        this.middle = middle;
        this.suffix = suffix;
        this.measure = measure;
        M tmp = prefix.measured();
        tmp = measure.combine(tmp, middle.measured());
        tmp = measure.combine(tmp, suffix.measured());
        this.measured = tmp;
    }

    @Override
    public FingerTree<T, M> pushRight(T value) {
        if (value == null) {
            throw new IllegalArgumentException();
        }
        if (suffix.size() == Digit.MAX_SIZE) {
            T a = suffix.get(0);
            T b = suffix.get(1);
            T c = suffix.get(2);
            T d = suffix.get(3);
            return new Deep<>(prefix, middle.pushRight(Node.makeThree(measure, a, b, c)), Digit.two(measure, d, value), measure);
        } else {
            return new Deep<>(prefix, middle, suffix.pushRight(value), measure);
        }
    }

    @Override
    FingerTree<T, M> pushLeft(T value) {
        if (value == null) {
            throw new IllegalArgumentException();
        }
        if (prefix.size() == Digit.MAX_SIZE) {
            T a = prefix.get(0);
            T b = prefix.get(1);
            T c = prefix.get(2);
            T d = prefix.get(3);
            return new Deep<>(Digit.two(measure, value, a), middle.pushLeft(Node.makeThree(measure, b, c, d)), suffix, measure);
        } else {
            return new Deep<>(prefix.pushLeft(value), middle, suffix, measure);
        }
    }

    @Override
    M measured() {
        return measured;
    }

    @Override
    Pair<FingerTree<T, M>, FingerTree<T, M>> split(Predicate<M> predicate) {
        if (predicate.test(measured())) {
            Split<FingerTree<T, M>, T> split = splitTree(predicate, measure.identity());
            return Pair.make(split.left, split.right.pushLeft(split.value));
        } else {
            return Pair.make(this, new Empty<T, M>(measure));
        }
    }

    @Override
    Split<FingerTree<T, M>, T> splitTree(Predicate<M> predicate, M i) {
        M vpr = measure.combine(i, prefix.measured());
        if (predicate.test(vpr)) {
            Split<Digit<T, M>, T> prefixSplit = splitDigit(prefix, predicate, i);
            return new Split<>(toTree(prefixSplit.left), prefixSplit.value, deepL(prefixSplit.right, middle, suffix, measure));
        }
        M vm = measure.combine(vpr, middle.measured());
        if (predicate.test(vm)) {
            Split<FingerTree<Node<T, M>, M>, Node<T, M>> middleSplit = middle.splitTree(predicate, vpr);
            M m = measure.combine(vpr, middleSplit.left.measured());
            Split<Digit<T, M>, T> innerSplit = splitDigit(middleSplit.value, predicate, m);
            return new Split<>(deepR(prefix, middleSplit.left, innerSplit.left, measure), innerSplit.value, deepL(innerSplit.right, middleSplit.right, suffix, measure));
        }
        Split<Digit<T, M>, T> suffixSplit = splitDigit(suffix, predicate, vm);
        return new Split<>(deepR(prefix, middle, suffixSplit.left, measure), suffixSplit.value, toTree(suffixSplit.right));
    }

    @Override
    Optional<Pair<T, Supplier<FingerTree<T, M>>>> viewL() {
        return Optional.of(Pair.make(prefix.peekLeft(), () -> deepL(prefix.popLeft(), middle, suffix, measure)));
    }

    @Override
    Optional<Pair<T, Supplier<FingerTree<T, M>>>> viewR() {
        return Optional.of(Pair.make(suffix.peekRight(), () -> deepR(prefix, middle, suffix.popRight(), measure)));
    }

    @Override
    FingerTree<T, M> app3(LinkedList<T> ts, FingerTree<T, M> xs) {
        if (xs instanceof Empty) {
            return append(this, ts);
        } else if (xs instanceof Single) {
            return append(this, ts).pushRight(xs.peekRight());
        } else {
            Deep<T, M> deepXs = (Deep<T, M>) xs;
            LinkedList<T> concat = concat(suffix, ts, deepXs.prefix);
            return new Deep<T, M>(prefix, middle.app3(nodes(concat, measure), deepXs.middle), deepXs.suffix, measure);

        }
    }

    private static <T, M> LinkedList<T> concat(Digit<T, M> a, LinkedList<T> b, Digit<T, M> c) {
        Iterator<T> it = a.reverseIterator();
        while (it.hasNext()) {
            b.addFirst(it.next());
        }
        for (T t : c) {
            b.addLast(t);
        }
        return b;
    }

    private static <T, M> LinkedList<Node<T, M>> nodes(LinkedList<T> ts, Measure<M, T> measure) {
        LinkedList<Node<T, M>> res = new LinkedList<>();
        if (ts.size() == 2) {
            res.add(Node.makeTwo(measure, ts.removeFirst(), ts.removeFirst()));
        } else if (ts.size() == 3) {
            res.add(Node.makeThree(measure, ts.removeFirst(), ts.removeFirst(), ts.removeFirst()));
        } else if (ts.size() == 4) {
            res.add(Node.makeTwo(measure, ts.removeFirst(), ts.removeFirst()));
            res.add(Node.makeTwo(measure, ts.removeFirst(), ts.removeFirst()));
        } else {
            res.add(Node.makeThree(measure, ts.removeFirst(), ts.removeFirst(), ts.removeFirst()));
            res.addAll(nodes(ts, measure));
        }
        return res;
    }

    private static <T, M> FingerTree<T, M> deepL(Digit<T, M> prefix, FingerTree<Node<T, M>, M> middle, Digit<T, M> suffix, Measure<M, T> measure) {
        if (prefix.size() == 0) {
            return middle.viewL()
                    .map(pair -> (FingerTree<T, M>) new Deep<T, M>(pair.first.toDigit(), pair.second.get(), suffix, measure))
                    .orElseGet(() -> toTree(suffix));
        } else {
            return new Deep<T, M>(prefix, middle, suffix, measure);
        }
    }

    private static <T, M> FingerTree<T, M> deepR(Digit<T, M> prefix, FingerTree<Node<T, M>, M> middle, Digit<T, M> suffix, Measure<M, T> measure) {
        if (suffix.size() == 0) {
            return middle.viewR()
                    .map(pair -> (FingerTree<T, M>) new Deep<T, M>(prefix, pair.second.get(), pair.first.toDigit(), measure))
                    .orElseGet(() -> toTree(prefix));
        } else {
            return new Deep<T, M>(prefix, middle, suffix, measure);
        }
    }

    private static <T, M> FingerTree<T, M> toTree(Digit<T, M> digit) {
        FingerTree<T, M> res = new Empty<T, M>(digit.measure);
        for (T t : digit) {
            res = res.pushRight(t);
        }
        return res;
    }

    private Split<Digit<T, M>, T> splitDigit(Iterable<T> digit, Predicate<M> predicate, M i) {
        Digit<T, M> left = Digit.empty(measure);
        Digit<T, M> right = Digit.empty(measure);
        T match = null;
        boolean matched = false;
        for (T t : digit) {
            i = measure.combine(i, measure.measure(t));
            if (!matched && predicate.test(i)) {
                matched = true;
                match = t;
                continue;
            }
            if (matched) {
                right = right.pushRight(t);
            } else {
                left = left.pushRight(t);
            }
        }
        if (match == null) {
            throw new IllegalStateException();
        }
        return new Split<>(left, match, right);
    }

    @Override
    public Iterator<T> iterator() {
        ArrayList<Supplier<Iterator<T>>> its = new ArrayList<>();
        its.add(() -> prefix.iterator());
        for (Node<T, M> node : middle) {
            its.add(() -> node.iterator());
        }
        its.add(() -> suffix.iterator());
        return new ChainingIterator<T>(its.iterator());
    }
}