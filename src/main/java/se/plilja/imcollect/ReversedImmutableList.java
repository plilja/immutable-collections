package se.plilja.imcollect;

import java.util.*;

final class ReversedImmutableList<T> implements ImmutableCollection<T> {
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
        Objects.requireNonNull(value, "Null values are not supported");

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
        for (T foo : this) {
            r++;
        }
        return r;
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
