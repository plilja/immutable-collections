package se.plilja.imcollect.internal.fingertrees;

import se.plilja.imcollect.ImmutableList;

import java.util.Iterator;

public final class FingerTreeList<T> implements ImmutableList<T> {
    private final FingerTree<T, Integer> fingerTree;

    private FingerTreeList(FingerTree<T, Integer> fingerTree) {
        this.fingerTree = fingerTree;
    }

    public static <T> FingerTreeList<T> empty() {
        return new FingerTreeList<>(new Empty<>(new CountingMeasure<>()));
    }

    private static class CountingMeasure<T> implements Measure<Integer, T> {
        @Override
        public Integer identity() {
            return 0;
        }

        @Override
        public Integer combine(Integer a, Integer b) {
            return a + b;
        }

        @Override
        public Integer measure(T value) {
            return 1;
        }
    }


    @Override
    public T get(int idx) {
        if (idx < 0 || idx >= size()) {
            throw new IndexOutOfBoundsException();
        } else {
            Split<FingerTree<T, Integer>, T> split = fingerTree.splitTree(j -> j >= idx + 1, 0);
            return split.value;
        }
    }

    @Override
    public FingerTreeList<T> set(int idx, T value) {
        if (idx < 0 || idx >= size()) {
            throw new IndexOutOfBoundsException();
        } else {
            Split<FingerTree<T, Integer>, T> split = fingerTree.splitTree(j -> j >= idx + 1, 0);
            return new FingerTreeList<>(split.left.pushRight(value).concat(split.right));
        }
    }

    @Override
    public int indexOf(T value) {
        int i = 0;
        for (T t : fingerTree) {
            if (t.equals(value)) {
                return i;
            }
            i++;
        }
        return -1;
    }

    @Override
    public int lastIndexOf(T value) {
        int res = -1;
        int i = 0;
        // TODO iterate in reverse instead
        for (T t : fingerTree) {
            if (t.equals(value)) {
                res = i;
            }
            i++;
        }
        return res;
    }

    @Override
    public FingerTreeList<T> add(T value) {
        return new FingerTreeList<>(fingerTree.pushRight(value));
    }

    @Override
    public FingerTreeList<T> addAll(Iterable<? extends T> values) {
        return (FingerTreeList<T>) ImmutableList.super.addAll(values);
    }

    @Override
    public FingerTreeList<T> remove(T value) {
        int idx = indexOf(value);
        if (idx == -1) {
            return this;
        } else {
            Split<FingerTree<T, Integer>, T> split = fingerTree.splitTree(j -> j >= idx + 1, 0);
            return new FingerTreeList<>(split.left.concat(split.right));
        }
    }

    @Override
    public int size() {
        return fingerTree.measured();
    }

    @Override
    public Iterator<T> iterator() {
        return fingerTree.iterator();
    }

    @Override
    public FingerTreeList<T> removeAll(Iterable<? extends T> values) {
        return (FingerTreeList<T>) ImmutableList.super.removeAll(values);
    }
}
