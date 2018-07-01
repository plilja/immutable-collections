package se.plilja.imcollect.internal;

import se.plilja.imcollect.ImmutableCollection;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;

public final class ReversedImmutableList<T> implements ImmutableCollection<T> {
    private final ReversedImmutableList<T> next;
    private final T value;

    private ReversedImmutableList(T value, ReversedImmutableList<T> next) {
        this.next = next;
        this.value = value;
    }

    public ReversedImmutableList() {
        next = null;
        value = null;
    }

    public Iterator<T> iterator() {
        return new ReversedImmutableListIterator(this);
    }

    @Override
    public ReversedImmutableList<T> add(T value) {
        if (value == null) {
            throw new IllegalArgumentException("Null values are not supported");
        }

        return new ReversedImmutableList<>(value, this);
    }

    @Override
    public boolean isEmpty() {
        return value == null;
    }

    @Override
    public ReversedImmutableList<T> remove(T value) {
        if (value == null) {
            return this; // Null values are not supported and hence there can be nothing to remove
        }

        Deque<T> q = new ArrayDeque<>();
        ReversedImmutableList<T> match = this;
        while (match.value != null && !match.value.equals(value)) {
            q.addFirst(match.value);
            match = match.next;
            assert match != null;
        }
        if (match.value == null) {
            return this; // value is not present, reuse this object
        } else {
            ReversedImmutableList<T> res = match.next;
            while (!q.isEmpty()) {
                T t = q.removeFirst();
                res = new ReversedImmutableList<>(t, res);
            }
            return res;

        }
    }

    @Override
    public int size() {
        int r = 0;
        for (T ignored : this) {
            r++;
        }
        return r;
    }

    @Override
    public ReversedImmutableList<T> addAll(Iterable<? extends T> values) {
        return (ReversedImmutableList<T>) ImmutableCollection.super.addAll(values);
    }

    @Override
    public boolean contains(T val) {
        for (T t : this) {
            if (t.equals(val))
                return true;
        }
        return false;
    }

    private class ReversedImmutableListIterator implements Iterator<T> {

        private ReversedImmutableList<T> n;

        public ReversedImmutableListIterator(ReversedImmutableList<T> curr) {
            n = curr;
        }

        @Override
        public boolean hasNext() {
            return n.value != null;
        }

        @Override
        public T next() {
            T res = n.value;
            n = n.next;
            return res;
        }
    }

}