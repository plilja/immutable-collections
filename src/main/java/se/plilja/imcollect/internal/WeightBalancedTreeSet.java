package se.plilja.imcollect.internal;

import se.plilja.imcollect.ImmutableSet;

import java.util.Comparator;
import java.util.Iterator;
import java.util.function.Function;

public final class WeightBalancedTreeSet<T> implements ImmutableSet<T> {
    private final WeightBalancedTree<T> tree;

    private WeightBalancedTreeSet(WeightBalancedTree<T> tree) {
        this.tree = tree;
    }

    public WeightBalancedTreeSet(Comparator<T> comparator) {
        this.tree = new WeightBalancedTree<>(comparator);
    }

    @Override
    public WeightBalancedTreeSet<T> add(T value) {
        if (value == null) {
            throw new IllegalArgumentException("Null values are not supported");
        }
        return new WeightBalancedTreeSet<>(tree.add(value));
    }

    @Override
    public WeightBalancedTreeSet<T> remove(T value) {
        if (value == null) {
            return this; // null can not be present, hence there is nothing to remove
        }
        return new WeightBalancedTreeSet<>(tree.remove(value));
    }

    @Override
    public int size() {
        return tree.size();
    }

    @Override
    public boolean contains(T val) {
        return tree.lookup(val).isPresent();
    }

    @Override
    public Iterator<T> iterator() {
        return tree.iterator(Function.identity());
    }

    @Override
    public WeightBalancedTreeSet<T> addAll(Iterable<? extends T> values) {
        return (WeightBalancedTreeSet<T>) ImmutableSet.super.addAll(values);
    }


}
