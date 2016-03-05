package se.plilja.imcollect;

import com.pholser.junit.quickcheck.Property;
import com.pholser.junit.quickcheck.generator.InRange;
import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

@RunWith(JUnitQuickcheck.class)
public class WeightBalancedTreeMapTest {

    @Test
    public void testLookup() {
        WeightBalancedTreeMap<Integer, String> target = new WeightBalancedTreeMap<>(Integer::compare);

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
        WeightBalancedTreeMap<Integer, String> target = new WeightBalancedTreeMap<>(Integer::compare);

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
        WeightBalancedTreeMap<Integer, String> target = new WeightBalancedTreeMap<>(Integer::compare);

        for (int i = 50000; i < 100000; i++) {
            target = target.put(i, String.format("foo %d", i));
        }

        for (int i = 0; i < 100000; i++) {
            target.lookup(i); // lookup and discard result
        }
    }

    @Test
    public void stressTestReferenceImplementation() {
        TreeMap<Integer, String> target = new TreeMap<>();

        for (int i = 50000; i < 100000; i++) {
            target.put(i, String.format("foo %d", i));
        }

        for (int i = 0; i < 100000; i++) {
            target.getOrDefault(i, "bar"); // lookup and discard result
        }
    }

    @Property
    public void iterateValuesShouldBehaveAsReferenceImplementation(List<@InRange(minInt = -10, maxInt = 10) Integer> values) {
        WeightBalancedTreeMap<Integer, Integer> target = new WeightBalancedTreeMap<>(Integer::compare);
        TreeMap<Integer, Integer> reference = new TreeMap<>();

        for (int i : values) {
            target = target.put(i, i);
            reference.put(i, i);
        }

        Iterator<Integer> targetIt = target.values().iterator();
        Iterator<Integer> referenceIt = reference.values().iterator();
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
        WeightBalancedTreeMap<Integer, Integer> target = new WeightBalancedTreeMap<>(Integer::compare);
        TreeMap<Integer, Integer> reference = new TreeMap<>();

        for (int i : values) {
            target = target.put(i, i);
            reference.put(i, i);
        }

        Iterator<Integer> targetIt = target.keys().iterator();
        Iterator<Integer> referenceIt = reference.keySet().iterator();
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
        WeightBalancedTreeMap<Integer, Integer> target = new WeightBalancedTreeMap<>(Integer::compare);

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
        WeightBalancedTreeMap<Integer, Integer> target = new WeightBalancedTreeMap<>(Integer::compare);
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
        WeightBalancedTreeMap<Integer, Integer> target = new WeightBalancedTreeMap<>(Integer::compare);
        for (int i : baseValues) {
            target = target.put(i, 2 * i);
        }

        Map<Integer, Integer> addMap = new HashMap<>();
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
        WeightBalancedTreeMap<Integer, Integer> target = new WeightBalancedTreeMap<>(Integer::compare);
        for (int i : baseValues) {
            target = target.put(i, 2 * i);
        }

        WeightBalancedTreeMap<Integer, Integer> addMap = new WeightBalancedTreeMap<>(Integer::compare);
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


}