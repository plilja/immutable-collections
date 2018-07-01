package se.plilja.imcollect.internal;

import se.plilja.imcollect.ImmutableList;

import java.util.Iterator;

public final class MapBasedImmutableList<T> implements ImmutableList<T> {
    private final WeightBalancedTreeMap<Integer, T> map;

    public MapBasedImmutableList() {
        this(new WeightBalancedTreeMap<>(Integer::compare));
    }

    private MapBasedImmutableList(WeightBalancedTreeMap<Integer, T> map) {
        this.map = map;
    }

    @Override
    public MapBasedImmutableList<T> add(T value) {
        if (value == null) {
            throw new IllegalArgumentException("Null values are not supported");
        }
        return new MapBasedImmutableList<>(map.put(size(), value));
    }

    @Override
    public MapBasedImmutableList<T> addAll(Iterable<? extends T> values) {
        return (MapBasedImmutableList<T>) ImmutableList.super.addAll(values);
    }

    @Override
    public MapBasedImmutableList<T> remove(T value) {
        if (value == null) {
            return this;
        }
        for (int i = 0; i < size(); i++) {
            T otherValue = map.get(i);
            if (otherValue.equals(value)) {
                WeightBalancedTreeMap<Integer, T> newMap = new WeightBalancedTreeMap<>(Integer::compare);
                for (int j = 0; j < size(); j++) {
                    if (j != i) {
                        newMap = newMap.put(newMap.size(), map.get(j));
                    }
                }
                return new MapBasedImmutableList<>(newMap);

            }
        }
        return this;
    }

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public boolean contains(T val) {
        if (val == null) {
            return false;
        }
        for (int i = 0; i < size(); i++) {
            if (map.get(i).equals(val)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Iterator<T> iterator() {
        return map.values().iterator();
    }

    @Override
    public T get(int idx) {
        assertValidIndex(idx);
        return map.get(idx);
    }

    private void assertValidIndex(long idx) {
        if (idx < 0 || idx >= size()) {
            throw new IndexOutOfBoundsException();
        }
    }

    @Override
    public MapBasedImmutableList<T> set(int idx, T value) {
        assertValidIndex(idx);
        return new MapBasedImmutableList<>(map.put(idx, value));
    }

    @Override
    public int indexOf(T value) {
        for (int i = 0; i < size(); i++) {
            if (map.get(i).equals(value)) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public int lastIndexOf(T value) {
        for (int i = size() - 1; i >= 0; i--) {
            if (map.get(i).equals(value)) {
                return i;
            }
        }
        return -1;
    }
}
