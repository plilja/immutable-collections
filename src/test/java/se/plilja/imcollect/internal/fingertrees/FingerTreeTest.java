package se.plilja.imcollect.internal.fingertrees;

import com.pholser.junit.quickcheck.Property;
import com.pholser.junit.quickcheck.generator.InRange;
import se.plilja.imcollect.internal.CollectionsBaseTest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.IntStream;

import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.junit.Assert.*;

public class FingerTreeTest extends CollectionsBaseTest {

    public FingerTreeTest() {
        super(FingerTreeList.empty());
    }

    @Property
    public void toMutableCollectionShouldReturnObjectEqualToArrayList(ArrayList<Integer> values) {
        FingerTreeList<Integer> target = FingerTreeList.empty();
        target = target.addAll(values);

        // when
        Collection<Integer> result = target.toMutableCollection();

        // then
        assertEquals(values, result);
    }

    @Property
    public void setFollowedByGetShouldYieldSetValue(@InRange(minInt = 0, maxInt = 10) int idx, int value) {
        FingerTreeList<Integer> target = FingerTreeList.empty();
        for (int i = 0; i < 11; i++) {
            target = target.add(i);
        }

        // when
        target = target.set(idx, value);
        int result = target.get(idx);

        // then
        assertEquals(value, result);
    }

    @Property
    public void accessingObjectWithIllegalIndexShouldYieldIndexOutOfBounds(List<Integer> values) {
        FingerTreeList<Integer> target = FingerTreeList.<Integer>empty()
                .addAll(values);

        // when, then
        for (int j = -100; j < 0; j++) {
            verifyCausesOutOfBounds(j, i -> target.get(i));
            verifyCausesOutOfBounds(j, i -> target.set(i, 4711));
        }
        for (int j = values.size(); j < 100; j++) {
            verifyCausesOutOfBounds(j, i -> target.get(i));
            verifyCausesOutOfBounds(j, i -> target.set(i, 4711));
        }
    }

    private void verifyCausesOutOfBounds(int idx, Consumer<Integer> actionThatShouldCauseOutOfBoundsException) {
        try {
            actionThatShouldCauseOutOfBoundsException.accept(idx);
            assertTrue("Should have triggered IndexOutOfBoundsException before reaching here", false);
        } catch (IndexOutOfBoundsException ex) {
            // expected
        }
    }

    @Property
    public void getIndexOfShouldReturnValue(List<Integer> values) {
        FingerTreeList<Integer> target = FingerTreeList.<Integer>empty()
                .addAll(values);

        for (int value : values) {
            assertEquals(value, (int) target.get(target.indexOf(value)));
            assertEquals(value, (int) target.get(target.lastIndexOf(value)));
        }
    }

    @Property
    public void getLastIndexOfVsIndexOf(List<Integer> values) {
        FingerTreeList<Integer> target = FingerTreeList.<Integer>empty()
                .addAll(values);

        for (int value : values) {
            assert target.indexOf(value) != -1;
            assert target.lastIndexOf(value) != -1;
            assertEquals(0, IntStream.range(0, target.indexOf(value))
                    .filter(i -> target.get(i).equals(value))
                    .count());
            assertEquals(0, IntStream.range(target.lastIndexOf(value) + 1, target.size())
                    .filter(i -> target.get(i).equals(value))
                    .count());
            assertThat(target.indexOf(value), lessThanOrEqualTo(target.lastIndexOf(value)));
        }
    }

    @Property
    public void indexOfShouldReturnMinusOneForNonExistingValue(List<@InRange(minInt = -1000, maxInt = -1) Integer> values, @InRange(minInt = 0, maxInt = 1000) Integer query) {
        FingerTreeList<Integer> target = FingerTreeList.<Integer>empty()
                .addAll(values);

        assertEquals(-1, target.indexOf(query));
        assertEquals(-1, target.lastIndexOf(query));
    }

}