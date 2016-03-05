package se.plilja.imcollect;

import org.junit.Test;

import java.util.Iterator;
import java.util.Random;
import java.util.TreeMap;

import static org.junit.Assert.*;

public class WeightBalancedTreeTest {

    private Random rand = new Random();

    @Test
    public void testLookup() {
        WeightBalancedTree<Integer, String> target = new WeightBalancedTree<>(null, (a, b) -> a - b);

        // when
        for (int i = 0; i < 1000; i++) {
            target = target.put(i, String.format("foo %d", i));
        }

        // then
        for (int i = 0; i < 1000; i++) {
            assertEquals(String.format("foo %d", i), target.get(i));
        }
    }

    @Test
    public void testContains() {
        WeightBalancedTree<Integer, String> target = new WeightBalancedTree<>(null, (a, b) -> a - b);

        // when
        for (int i = 0; i < 1000; i++) {
            target = target.put(i, String.format("foo %d", i));
        }

        // then
        for (int i = 0; i < 1000; i++) {
            assertTrue(target.contains(i));
        }
        for (int i = 1000; i < 2000; i++) {
            assertFalse(target.contains(i));
        }
    }

    @Test
    public void stressTest() {
        WeightBalancedTree<Integer, String> target = new WeightBalancedTree<>(null, (a, b) -> a - b);

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

    @Test
    public void iterateValuesTest() {
        WeightBalancedTree<Integer, Integer> target = new WeightBalancedTree<>(null, (a, b) -> a - b);
        TreeMap<Integer, Integer> reference = new TreeMap<>();

        for (int i = 0; i < 1000; i++) {
            int key = rand.nextInt();
            target = target.put(key, key);
            reference.put(key, key);
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
}