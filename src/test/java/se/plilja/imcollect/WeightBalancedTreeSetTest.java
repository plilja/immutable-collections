package se.plilja.imcollect;

import com.pholser.junit.quickcheck.Property;
import com.pholser.junit.quickcheck.generator.InRange;

import java.util.HashSet;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class WeightBalancedTreeSetTest extends CollectionsBaseTest {

    public WeightBalancedTreeSetTest() {
        super(new WeightBalancedTreeSet<>(Integer::compareTo));
    }

    @Property
    public void lookupNullShouldAlwaysReturnEmpty(List<@InRange(minInt = -100, maxInt = 100) Integer> baseValues) {
        WeightBalancedTreeSet<Integer> target = new WeightBalancedTreeSet<>(Integer::compare);
        target = target.addAll(baseValues);

        assertFalse(target.contains(null));
    }

    @Property
    public void sizeShouldBeEqualToNumberOfUniqueAddedValues(List<Integer> values) {
        WeightBalancedTreeSet<Integer> target = new WeightBalancedTreeSet<>(Integer::compare);
        HashSet<Integer> uniqueValues = new HashSet<>(values);
        target = target.addAll(values);
        assertEquals(uniqueValues.size(), target.size());
    }

}