package se.plilja.imcollect;

import com.pholser.junit.quickcheck.Property;
import com.pholser.junit.quickcheck.generator.InRange;
import org.junit.Assume;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class CollectionsBaseTest {
    protected ImmutableCollection<Integer> empty;

    @Property
    public void removeExistingElementShouldDecreaseSize(List<@InRange(minInt = -100, maxInt = 100) Integer> values) {
        Assume.assumeFalse(values.isEmpty());

        ImmutableCollection<Integer> target = empty.addAll(values);

        // when
        ImmutableCollection<Integer> result = target.remove(values.get(0));

        // then
        assertEquals(target.size() - 1, result.size());
    }
}
