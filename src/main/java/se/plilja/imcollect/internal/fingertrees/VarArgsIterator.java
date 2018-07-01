package se.plilja.imcollect.internal.fingertrees;

import java.util.Iterator;

class VarArgsIterator<T> implements Iterator<T> {
    private final T[] ts;
    private int i = 0;

    @SafeVarargs
    VarArgsIterator(T... ts) {
        this.ts = ts;
    }

    @Override
    public boolean hasNext() {
        return i < ts.length;
    }

    @Override
    public T next() {
        return ts[i++];
    }
}