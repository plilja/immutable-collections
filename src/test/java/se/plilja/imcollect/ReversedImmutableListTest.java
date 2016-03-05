package se.plilja.imcollect;

import com.pholser.junit.quickcheck.Property;
import com.pholser.junit.quickcheck.generator.InRange;
import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.*;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;

@RunWith(JUnitQuickcheck.class)
public class ReversedImmutableListTest {

    @Test
    public void isEmpty() {
        ImmutableCollection<Integer> objects = new NaiveImpl<>();
        System.out.println(objects.size());
        ImmutableCollection<Integer> objects2 = objects.add(1);
        System.out.println(objects2);
        System.out.println(objects2.contains(1));
        System.out.println(objects2.containsAll(Arrays.asList(1, 2, 3)));
        System.out.println(objects2.containsAll(Arrays.asList(1)));
        System.out.println(objects2.containsAll(Arrays.asList(2, 3)));
        System.out.println(objects2.isEmpty());
        System.out.println(objects.isEmpty());

    }

    @Test
    public void remove() {
        ImmutableCollection<Integer> target = new ReversedImmutableList<>();
        target = target.add(5);
        target = target.add(5);
        target = target.add(85);
        target = target.removeAll(Arrays.asList(5));
        System.out.println(target.toMutableCollection());

    }

    @Property(trials = 1000)
    public void compareWithReferenceImplementation(List<CollectionOperation> operations,
                                                   LinkedList<@InRange(minInt = -100, maxInt = 100) Integer> values) {
        ImmutableCollection<Integer> target = new ReversedImmutableList<>();
        ImmutableCollection<Integer> referenceImpl = new NaiveImpl<>();
        int m = Math.min(operations.size(), values.size());
        boolean act, exp;
        for (int i = 0; i < m && !values.isEmpty(); i++) {
            CollectionOperation operation = operations.get(i);
            switch (operation) {
                case Add:
                    int v1 = values.pop();
                    target = target.add(v1);
                    referenceImpl = referenceImpl.add(v1);
                    assertEquals(referenceImpl.toMutableCollection(), target.toMutableCollection());
                    break;
                case AddAll:
                    LinkedList<Integer> v8 = popSome(values);
                    target = target.addAll(v8);
                    referenceImpl = referenceImpl.addAll(v8);
                    assertEquals(referenceImpl.toMutableCollection(), target.toMutableCollection());
                    break;
                case Remove:
                    int v2 = values.pop();
                    target = target.remove(v2);
                    referenceImpl = referenceImpl.remove(v2);
                    assertEquals(referenceImpl.toMutableCollection(), target.toMutableCollection());
                    break;
                case RemoveAll:
                    LinkedList<Integer> v7 = popSome(values);
                    target = target.removeAll(v7);
                    referenceImpl = referenceImpl.removeAll(v7);
                    assertEquals(referenceImpl.toMutableCollection(), target.toMutableCollection());
                    break;
                case Contains:
                    int v6 = values.pop();
                    act = target.contains(v6);
                    exp = referenceImpl.contains(v6);
                    assertEquals(exp, act);
                    break;
                case ContainsAll:
                    LinkedList<Integer> v4 = popSome(values);
                    act = target.containsAll(v4);
                    exp = referenceImpl.containsAll(v4);
                    assertEquals(exp, act);
                    break;
                case RetainAll:
                    LinkedList<Integer> v5 = popSome(values);
                    target = target.retainAll(v5);
                    referenceImpl = referenceImpl.retainAll(v5);
                    assertEquals(referenceImpl.toMutableCollection(), target.toMutableCollection());
                    break;
                case IsEmpty:
                    act = target.isEmpty();
                    exp = referenceImpl.isEmpty();
                    assertEquals(exp, act);
                    break;
                case Size:
                    assertEquals(referenceImpl.size(), target.size());
                    break;
                default:
                    throw new UnsupportedOperationException();
            }
        }
    }

    private LinkedList<Integer> popSome(LinkedList<Integer> list) {
        int v = list.pop();
        LinkedList<Integer> res = new LinkedList<>();
        while (v > 0 && !list.isEmpty()) {
            res.add(list.pop());
        }
        return res;
    }

    private static class NaiveImpl<T> implements ImmutableCollection<T> {
        private final LinkedList<T> list;

        private NaiveImpl(LinkedList<T> list) {
            this.list = list;
        }

        private NaiveImpl() {
            this.list = new LinkedList<>();
        }

        @Override
        public NaiveImpl<T> add(T value) {
            LinkedList<T> newList = new LinkedList<>(list);
            newList.addFirst(value);
            return new NaiveImpl<>(newList);
        }

        @Override
        public NaiveImpl<T> remove(T value) {
            LinkedList<T> newList = new LinkedList<>(list);
            newList.remove(value);
            return new NaiveImpl<>(newList);
        }

        @Override
        public int size() {
            return list.size();
        }

        @Override
        public boolean contains(T val) {
            return list.contains(val);
        }

        @Override
        public Iterator<T> iterator() {
            return list.iterator();
        }

        @Override
        public NaiveImpl<T> addAll(Iterable<? extends T> values) {
            LinkedList<T> newList = toList(values);
            Collections.reverse(newList);
            newList.addAll(list);
            return new NaiveImpl<>(newList);
        }


        private LinkedList<T> toList(Iterable<? extends T> values) {
            LinkedList<T> newList = new LinkedList<>();
            for (T value : values) {
                newList.add(value);
            }
            return newList;
        }

        @Override
        public boolean isEmpty() {
            return list.isEmpty();
        }

        @Override
        public boolean containsAll(Iterable<? extends T> values) {
            return list.containsAll(toList(values));
        }

        @Override
        public NaiveImpl<T> removeAll(Iterable<? extends T> values) {
            LinkedList<T> newList = new LinkedList<>(list);
            newList.removeAll(toList(values));
            return new NaiveImpl<>(newList);
        }

        @Override
        public NaiveImpl<T> retainAll(Iterable<? extends T> values) {
            LinkedList<T> newList = new LinkedList<>(list);
            newList.retainAll(toList(values));
            return new NaiveImpl<>(newList);
        }

        @Override
        public Stream<T> stream() {
            return list.stream();
        }

        @Override
        public Stream<T> parallelStream() {
            return list.parallelStream();
        }

        @Override
        public Collection<T> toMutableCollection() {
            return new LinkedList<>(list);
        }
    }


}