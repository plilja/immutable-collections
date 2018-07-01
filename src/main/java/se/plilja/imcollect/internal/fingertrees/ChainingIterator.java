package se.plilja.imcollect.internal.fingertrees;


import java.util.Collections;
import java.util.Iterator;
import java.util.function.Supplier;

final class ChainingIterator<T> implements Iterator<T> {
    private final Iterator<Supplier<Iterator<T>>> iterators;
    private Iterator<T> currentIterator;

     ChainingIterator(Iterator<Supplier<Iterator<T>>> iterators) {
        this.iterators = iterators;
        this.currentIterator = Collections.emptyIterator();
    }

    @Override
    public boolean hasNext() {
        advance();
        return currentIterator.hasNext();
    }

    private void advance() {
        while (!currentIterator.hasNext() && iterators.hasNext()) {
            currentIterator = iterators.next().get();
        }
    }

    @Override
    public T next() {
        T res = currentIterator.next();
        advance();
        return res;
    }
}
