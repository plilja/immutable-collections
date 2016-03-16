package se.plilja.imcollect;

import java.util.Iterator;
import java.util.Objects;

// TODO: 06/03/16 Replace with finger trees
final class MapBasedImmutableList<T> implements ImmutableList<T> {
    private final ImmutableMap<Long, T> map;

    public MapBasedImmutableList() {
        this(new WeightBalancedTreeMap<>(Long::compare));
    }

    private MapBasedImmutableList(ImmutableMap<Long, T> map) {
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
        for (long i = 0; i < size(); i++) {
            T otherValue = map.get(i);
            if (otherValue.equals(value)) {
                ImmutableMap<Long, T> newMap = new WeightBalancedTreeMap<>(Long::compare);
                for (long j = 0; j < size(); j++) {
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
    public long size() {
        return map.size();
    }

    @Override
    public boolean contains(T val) {
        if (val == null) {
            return false;
        }
        for (long i = 0; i < size(); i++) {
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
    public T get(long idx) {
        assertValidIndex(idx);
        return map.get(idx);
    }

    private void assertValidIndex(long idx) {
        if (idx < 0 || idx >= size()) {
            throw new IndexOutOfBoundsException();
        }
    }

    @Override
    public MapBasedImmutableList<T> set(long idx, T value) {
        assertValidIndex(idx);
        return new MapBasedImmutableList<>(map.put(idx, value));
    }

    @Override
    public long indexOf(T value) {
        for (long i = 0; i < size(); i++) {
            if (map.get(i).equals(value)) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public long lastIndexOf(T value) {
        for (long i = size() - 1; i >= 0; i--) {
            if (map.get(i).equals(value)) {
                return i;
            }
        }
        return -1;
    }
}
