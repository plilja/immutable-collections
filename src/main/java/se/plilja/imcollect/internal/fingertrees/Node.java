package se.plilja.imcollect.internal.fingertrees;


import java.util.Iterator;

interface Node<T, M> extends Iterable<T> {

    static <T, M> Node<T, M> makeTwo(Measure<M, T> measure, T a, T b) {
        return new Node2<T, M>(measure, a, b);
    }

    static <T, M> Node<T, M> makeThree(Measure<M, T> measure, T a, T b, T c) {
        return new Node3<T, M>(measure, a, b, c);
    }

    M measured();

    Digit<T, M> toDigit();

    class Node2<T, M> implements Node<T, M> {
        private final Measure<M, T> measure;
        private final T a;
        private final T b;
        private final M measured;

        Node2(Measure<M, T> measure, T a, T b) {
            this.measure = measure;
            this.a = a;
            this.b = b;
            measured = measure.combine(measure.measure(a), measure.measure(b));
        }

        @Override
        public M measured() {
            return measured;
        }

        @Override
        public Digit<T, M> toDigit() {
            return Digit.two(measure, a, b);
        }

        @Override
        public Iterator<T> iterator() {
            return new VarArgsIterator<T>(a, b);
        }
    }

    class Node3<T, M> implements Node<T, M> {
        private final Measure<M, T> measure;
        private final T a;
        private final T b;
        private final T c;
        private final M measured;

        Node3(Measure<M, T> measure, T a, T b, T c) {
            this.measure = measure;
            this.a = a;
            this.b = b;
            this.c = c;
            measured = measure.combine(measure.combine(measure.measure(a), measure.measure(b)), measure.measure(c));
        }

        @Override
        public M measured() {
            return measured;
        }

        @Override
        public Digit<T, M> toDigit() {
            return Digit.three(measure, a, b, c);
        }

        @Override
        public Iterator<T> iterator() {
            return new VarArgsIterator<T>(a, b, c);
        }
    }
}