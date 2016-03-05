package se.plilja.imcollect;

import java.util.Comparator;
import java.util.Iterator;
import java.util.function.Function;

final class WeightBalancedTreeSet<T> implements ImmutableSet<T> {
    private final WeightBalancedTree<T> tree;

    private WeightBalancedTreeSet(WeightBalancedTree<T> tree) {
        this.tree = tree;
    }

    public WeightBalancedTreeSet(Comparator<T> comparator) {
        this.tree = new WeightBalancedTree<>(comparator);
    }

    @Override
    public WeightBalancedTreeSet<T> add(T value) {
        return new WeightBalancedTreeSet<T>(tree.add(value));
    }

    @Override
    public WeightBalancedTreeSet<T> remove(T value) {
        return new WeightBalancedTreeSet<T>(tree.remove(value));
    }

    @Override
    public long size() {
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
}
