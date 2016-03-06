package se.plilja.imcollect;

import com.pholser.junit.quickcheck.Property;
import com.pholser.junit.quickcheck.generator.InRange;

import java.util.*;

import static org.junit.Assert.assertEquals;

public class ReversedImmutableListTest extends CollectionsBaseTest {

    public ReversedImmutableListTest() {
        super(new ReversedImmutableList<>());
    }

    @Property
    public void sizeShouldBeEqualToNumberOfAddedValues(List<Integer> values) {
        ReversedImmutableList<Integer> target = new ReversedImmutableList<>();

        for (int i = 0; i < values.size(); i++) {
            target = target.add(values.get(i));
            assertEquals(i + 1, target.size());
        }
    }

    @Property
    public void toMutableCollectionShouldEqualAnArrayListInReversedOrder(List<@InRange(minInt = -10, maxInt = 10) Integer> values) {
        ReversedImmutableList<Integer> target = new ReversedImmutableList<Integer>().addAll(values);
        ArrayList<Integer> reference = new ArrayList<>(values);
        Collections.reverse(reference);

        // when
        Collection<Integer> result = target.toMutableCollection();

        // then
        assertEquals(result, reference);
    }
}