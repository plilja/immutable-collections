package se.plilja.imcollect.internal;

import com.pholser.junit.quickcheck.Property;
import com.pholser.junit.quickcheck.generator.InRange;
import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;
import org.junit.Assume;
import org.junit.Test;
import org.junit.runner.RunWith;
import se.plilja.imcollect.ImmutableCollection;

import java.util.Arrays;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.junit.Assert.*;

@RunWith(JUnitQuickcheck.class)
public abstract class CollectionsBaseTest {
    private final ImmutableCollection<Integer> empty;

    public CollectionsBaseTest(ImmutableCollection<Integer> empty) {
        this.empty = empty;
    }

    @Property
    public void removeExistingElementShouldDecreaseSize(List<@InRange(minInt = -10, maxInt = 10) Integer> baseCollection, int newValue) {
        ImmutableCollection<Integer> target = empty.addAll(baseCollection);

        // when
        ImmutableCollection<Integer> withNewValue = target.add(newValue);

        // then
        assertTrue(withNewValue.contains(newValue));
    }

    @Property
    public void removeExistingElementShouldDecreaseSize(List<@InRange(minInt = -10, maxInt = 10) Integer> values) {
        Assume.assumeFalse(values.isEmpty());

        ImmutableCollection<Integer> target = empty.addAll(values);

        // when
        ImmutableCollection<Integer> result = target.remove(values.get(0));

        // then
        assertEquals(target.size() - 1, result.size());
    }

    @Property
    public void removeAllShouldRemoveAllElements(List<@InRange(minInt = -10, maxInt = 10) Integer> base, List<@InRange(minInt = -10, maxInt = 10) Integer> toRemove) {
        ImmutableCollection<Integer> target = empty.addAll(base);

        // when
        ImmutableCollection<Integer> result = target.removeAll(toRemove);

        // then
        for (int i : toRemove) {
            assertFalse(result.contains(i));
        }
    }

    @Property
    public void removeNonExistingElementShouldNotChangeSize(List<@InRange(minInt = -10, maxInt = 10) Integer> base, @InRange(minInt = -10, maxInt = 10) int toRemove) {
        Assume.assumeFalse(base.contains(toRemove));

        ImmutableCollection<Integer> target = empty.addAll(base);

        // when
        ImmutableCollection<Integer> result = target.remove(toRemove);

        // then
        assertEquals(target.size(), result.size());
    }

    @Property
    public void anAddedValueShouldBePresentInCollection(List<@InRange(minInt = -10, maxInt = 10) Integer> base, int newValue) {
        ImmutableCollection<Integer> target = empty.addAll(base);

        // when
        ImmutableCollection<Integer> result = target.add(newValue);

        // then
        assertTrue(result.contains(newValue));
    }

    @Property
    public void allAddedValueShouldBePresentInCollection(List<@InRange(minInt = -10, maxInt = 10) Integer> base, List<@InRange(minInt = -10, maxInt = 10) Integer> newValues) {
        ImmutableCollection<Integer> target = empty.addAll(base);

        // when
        ImmutableCollection<Integer> result = target.addAll(newValues);

        // then
        for (int i : newValues) {
            assertTrue(result.contains(i));
        }
        assertTrue(result.containsAll(newValues));
    }

    @Property
    public void emptyShouldAlwaysBeFalseAfterAdd(int newValue) {
        assert empty.isEmpty();

        // when
        ImmutableCollection<Integer> result = empty.add(newValue);

        // then
        assertFalse(result.isEmpty());
    }

    @Property
    public void retainAll(List<@InRange(minInt = -10, maxInt = 10) Integer> base, List<@InRange(minInt = -10, maxInt = 10) Integer> retainValues) {
        ImmutableCollection<Integer> target = empty.addAll(base);

        // when
        ImmutableCollection<Integer> result = target.retainAll(retainValues);

        // then
        for (int i = -10; i <= 10; i++) {
            assertEquals(base.contains(i) && retainValues.contains(i), result.contains(i));
        }
    }

    @Test
    public void testStream() {
        ImmutableCollection<Integer> target = empty.addAll(asList(1, 3, 5, 7, 9));

        // when
        List<Integer> result = target.stream()
                .sorted()
                .map(i -> 2 * i)
                .collect(toList());

        // then
        assertEquals(asList(2, 6, 10, 14, 18), result);
    }

    @Test
    public void testParallelStream() {
        ImmutableCollection<Integer> target = empty.addAll(asList(1, 3, 5, 7, 9));

        // when
        List<Integer> result = target.parallelStream()
                .sorted()
                .map(i -> 2 * i)
                .collect(toList());

        // then
        assertEquals(asList(2, 6, 10, 14, 18), result);
    }

    @Test(expected = IllegalArgumentException.class)
    public void addingNullShouldNotBeAllowed1() {
        empty.add(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void addingNullShouldNotBeAllowed2() {
        empty.addAll(Arrays.asList(1, 2, null, 3, 4));
    }

    @Property
    public void nullShouldNeverBePresent(List<Integer> base) {
        ImmutableCollection<Integer> target = empty.addAll(base);

        // when
        boolean res = target.contains(null);

        // then
        assertEquals(false, res);
    }

    @Property
    public void removingNullShouldReturnTheExactSameInstance(List<Integer> base) {
        ImmutableCollection<Integer> target = empty.addAll(base);

        // when
        ImmutableCollection<Integer> result = target.remove(null);

        // then
        assertSame(target, result);
    }

}