package se.plilja.imcollect.internal;

import com.pholser.junit.quickcheck.Property;
import com.pholser.junit.quickcheck.generator.InRange;
import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

import static org.junit.Assert.*;

@RunWith(JUnitQuickcheck.class)
public class WeightBalancedTreeMapTest {

    @Test
    public void testLookup() {
        var target = new WeightBalancedTreeMap<Integer, String>(Integer::compare);

        // when
        for (int i = 0; i < 1000; i++) {
            target = target.put(i, String.format("foo %d", i));
        }

        // then
        for (int i = 0; i < 1000; i++) {
            assertEquals(String.format("foo %d", i), target.get(i));
        }
    }

    @Property
    public void testContains(List<@InRange(minInt = -100, maxInt = 100) Integer> values) {
        var target = new WeightBalancedTreeMap<Integer, String>(Integer::compare);

        // when
        for (Integer i : values) {
            target = target.put(i, String.format("foo %d", i));
        }

        // then
        for (int i = -100; i <= 100; i++) {
            assertEquals(values.contains(i), target.contains(i));
        }
    }

    @Test
    public void stressTest() {
        var target = new WeightBalancedTreeMap<Integer, String>(Integer::compare);

        for (int i = 50000; i < 100000; i++) {
            target = target.put(i, String.format("foo %d", i));
        }

        for (int i = 0; i < 100000; i++) {
            target.lookup(i); // lookup and discard result
        }
    }

    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    @Test
    public void stressTestReferenceImplementation() {
        var target = new TreeMap<Integer, String>();

        for (int i = 50000; i < 100000; i++) {
            target.put(i, String.format("foo %d", i));
        }

        for (int i = 0; i < 100000; i++) {
            target.getOrDefault(i, "bar"); // lookup and discard result
        }
    }

    @Property
    public void iterateValuesShouldBehaveAsReferenceImplementation(List<@InRange(minInt = -10, maxInt = 10) Integer> values) {
        var target = new WeightBalancedTreeMap<Integer, Integer>(Integer::compare);
        var reference = new TreeMap<Integer, Integer>();

        for (int i : values) {
            target = target.put(i, i);
            reference.put(i, i);
        }

        var targetIt = target.values().iterator();
        var referenceIt = reference.values().iterator();
        while (targetIt.hasNext() && referenceIt.hasNext()) {
            int a = targetIt.next();
            int b = referenceIt.next();
            assertEquals(b, a);
        }
        assertFalse(targetIt.hasNext());
        assertFalse(referenceIt.hasNext());
    }

    @Property
    public void iterateKeysShouldBehaveAsReferenceImplementation(List<@InRange(minInt = -10, maxInt = 10) Integer> values) {
        var target = new WeightBalancedTreeMap<Integer, Integer>(Integer::compare);
        var reference = new TreeMap<Integer, Integer>();

        for (int i : values) {
            target = target.put(i, i);
            reference.put(i, i);
        }

        var targetIt = target.keys().iterator();
        var referenceIt = reference.keySet().iterator();
        while (targetIt.hasNext() && referenceIt.hasNext()) {
            int a = targetIt.next();
            int b = referenceIt.next();
            assertEquals(b, a);
        }
        assertFalse(targetIt.hasNext());
        assertFalse(referenceIt.hasNext());
    }

    @Property
    public void sizeShouldIncreaseWhenNewValuesAreAdded(List<@InRange(minInt = -10, maxInt = 10) Integer> values) {
        var target = new WeightBalancedTreeMap<Integer, Integer>(Integer::compare);

        for (int i : values) {
            long expectedSize;
            if (target.contains(i)) {
                expectedSize = target.size();
            } else {
                expectedSize = target.size() + 1;
            }

            // when
            target = target.put(i, 2 * i);

            // then
            assertEquals(expectedSize, target.size());
        }
    }

    @Property
    public void removedValuesShouldNotBePresent(List<@InRange(minInt = -10, maxInt = 10) Integer> baseValues, List<@InRange(minInt = -10, maxInt = 10) Integer> toRemove) {
        var target = new WeightBalancedTreeMap<Integer, Integer>(Integer::compare);
        for (int i : baseValues) {
            target = target.put(i, 2 * i);
        }

        // when
        for (int i : toRemove) {
            target = target.remove(i);
        }

        // then
        for (int i : toRemove) {
            assertFalse(target.contains(i));
            assertFalse(target.lookup(i).isPresent());
        }
    }

    @Property
    public void putAllFromMutableMap(List<@InRange(minInt = -10, maxInt = 10) Integer> baseValues, List<@InRange(minInt = -10, maxInt = 10) Integer> toAdd) {
        var target = new WeightBalancedTreeMap<Integer, Integer>(Integer::compare);
        for (int i : baseValues) {
            target = target.put(i, 2 * i);
        }

        var addMap = new HashMap<Integer, Integer>();
        for (int i : toAdd) {
            addMap.put(i, 3 * i);
        }

        // when
        target = target.putAll(addMap);

        // then
        for (int key : target.keys()) {
            if (addMap.containsKey(key)) {
                assertEquals(3 * key, (int) target.get(key));
            } else {
                assertEquals(2 * key, (int) target.get(key));
            }
        }
    }

    @Property
    public void putAllFromImmutableMap(List<@InRange(minInt = -10, maxInt = 10) Integer> baseValues, List<@InRange(minInt = -10, maxInt = 10) Integer> toAdd) {
        var target = new WeightBalancedTreeMap<Integer, Integer>(Integer::compare);
        for (int i : baseValues) {
            target = target.put(i, 2 * i);
        }

        var addMap = new WeightBalancedTreeMap<Integer, Integer>(Integer::compare);
        for (int i : toAdd) {
            addMap = addMap.put(i, 3 * i);
        }

        // when
        target = target.putAll(addMap);

        // then
        for (int key : target.keys()) {
            if (addMap.contains(key)) {
                assertEquals(3 * key, (int) target.get(key));
            } else {
                assertEquals(2 * key, (int) target.get(key));
            }
        }
    }

    @Property
    public void lookupNullShouldAlwaysReturnEmpty(List<Integer> baseValues) {
        var target = new WeightBalancedTreeMap<Integer, Integer>(Integer::compare);
        for (int i : baseValues) {
            target = target.put(i, i);
        }

        assertFalse(target.lookup(null).isPresent());
        assertFalse(target.contains(null));
    }

    @Test(expected = IllegalArgumentException.class)
    public void insertNullKeyShouldFail() {
        var target = new WeightBalancedTreeMap<Integer, Integer>(Integer::compare);
        target.put(null, 4711);
    }

    @Test(expected = IllegalArgumentException.class)
    public void insertNullValueShouldFail() {
        var target = new WeightBalancedTreeMap<Integer, Integer>(Integer::compare);
        target.put(4711, null);
    }

    @Property
    public void removeNullShouldReturnTheExactSameInstance(List<Integer> baseValues) {
        var target = new WeightBalancedTreeMap<Integer, Integer>(Integer::compare);
        for (int i : baseValues) {
            target = target.put(i, i);
        }

        // when
        var result = target.remove(null);

        // then
        assertSame(result, target);
    }
}