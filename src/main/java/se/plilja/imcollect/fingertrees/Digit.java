package se.plilja.imcollect.fingertrees;

import se.plilja.imcollect.Pair;

import java.util.Arrays;
import java.util.Iterator;

class Digit<T, M> implements Iterable<T> {
    static final int MAX_SIZE = 4;
    private final T[] values;
    final Measure<M, T> measure;
    private final M measured;

    @SafeVarargs
    private Digit(Measure<M, T> measure, T... values) {
        this.measure = measure;
        this.values = values;
        M tmp = measure.identity();
        for (T value : values) {
            tmp = measure.combine(tmp, measure.measure(value));
        }
        this.measured = tmp;
    }

    static <T, M> Digit<T, M> empty(Measure<M, T> measure) {
        return new Digit<>(measure);
    }

    static <T, M> Digit<T, M> one(Measure<M, T> measure, T a) {
        return new Digit<>(measure, a);
    }

    static <T, M> Digit<T, M> two(Measure<M, T> measure, T a, T b) {
        return new Digit<>(measure, a, b);
    }

    static <T, M> Digit<T, M> three(Measure<M, T> measure, T a, T b, T c) {
        return new Digit<>(measure, a, b, c);
    }

    static <T, M> Digit<T, M> four(Measure<M, T> measure, T a, T b, T c, T d) {
        return new Digit<>(measure, a, b, c, d);
    }

    M measured() {
        return measured;
    }

    int size() {
        return values.length;
    }

    Digit<T, M> pushRight(T t) {
        if (size() == MAX_SIZE) {
            throw new UnsupportedOperationException();
        } else {
            T[] newValues = Arrays.copyOf(values, values.length + 1);
            newValues[values.length] = t;
            return new Digit<>(measure, newValues);
        }
    }

    Digit<T, M> pushLeft(T t) {
        if (size() == MAX_SIZE) {
            throw new UnsupportedOperationException();
        } else {
            T[] newValues = Arrays.copyOf(values, values.length + 1);
            newValues[0] = t;
            for (int i = 0; i < values.length; i++) {
                newValues[i + 1] = values[i];
            }
            return new Digit<>(measure, newValues);
        }
    }

    int indexOf(T t) {
        for (int i = 0; i < values.length; i++) {
            if (values[i].equals(t)) {
                return i;
            }
        }
        return -1;
    }

    T get(int idx) {
        assertValidIdx(idx);
        return values[idx];
    }

    private void assertValidIdx(int idx) {
        if (idx < 0) {
            throw new IndexOutOfBoundsException("Negative index not allowed");
        } else if (idx >= values.length) {
            if (values.length == 0) {
                throw new IndexOutOfBoundsException("No values are present");
            } else {
                throw new IndexOutOfBoundsException(String.format("Max allowed index is %d got %d", values.length - 1, idx));
            }
        }
    }

    Digit<T, M> set(int idx, T t) {
        assertValidIdx(idx);
        T[] newValues = Arrays.copyOf(values, values.length);
        newValues[idx] = t;
        return new Digit<>(measure, newValues);
    }

    Pair<Boolean, Digit<T, M>> remove(T t) {
        int i = indexOf(t);
        if (i != -1) {
            return Pair.make(true, removeIdx(i));
        } else {
            return Pair.make(false, this);
        }
    }

    Digit<T, M> removeIdx(int i) {
        assertValidIdx(i);
        T[] newValues = Arrays.copyOf(values, values.length - 1);
        for (int j = i; j < values.length - 1; j++) {
            newValues[j] = values[j + 1];
        }
        return new Digit<>(measure, newValues);
    }

    Digit<T, M> popLeft() {
        return removeIdx(0);
    }

    Digit<T, M> popRight() {
        return removeIdx(size() - 1);
    }

    T peekLeft() {
        return get(0);
    }

    T peekRight() {
        return get(size() - 1);
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            int i = 0;

            @Override
            public boolean hasNext() {
                return i < values.length;
            }

            @Override
            public T next() {
                return values[i++];
            }
        };
    }

    Iterator<T> reverseIterator() {
        return new Iterator<T>() {
            int i = size() - 1;

            @Override
            public boolean hasNext() {
                return i >= 0;
            }

            @Override
            public T next() {
                return values[i--];
            }
        };
    }
}
