package se.plilja.imcollect;

import com.pholser.junit.quickcheck.Property;
import com.pholser.junit.quickcheck.generator.InRange;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.IntStream;

import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class MapBasedImmutableListTest extends CollectionsBaseTest {

    public MapBasedImmutableListTest() {
        super(new MapBasedImmutableList<>());
    }

    @Property
    public void toMutableCollectionShouldReturnObjectEqualToArrayList(ArrayList<Integer> values) {
        MapBasedImmutableList<Integer> target = new MapBasedImmutableList<>();
        target = target.addAll(values);

        // when
        Collection<Integer> result = target.toMutableCollection();

        // then
        assertEquals(values, result);
    }

    @Property
    public void setFollowedByGetShouldYieldSetValue(@InRange(minInt = 0, maxInt = 10) int idx, int value) {
        MapBasedImmutableList<Integer> target = new MapBasedImmutableList<>();
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
        MapBasedImmutableList<Integer> target = new MapBasedImmutableList<Integer>()
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

    private void verifyCausesOutOfBounds(long idx, Consumer<Long> actionThatShouldCauseOutOfBoundsException) {
        try {
            actionThatShouldCauseOutOfBoundsException.accept(idx);
            assertTrue("Should have triggered IndexOutOfBoundsException before reaching here", false);
        } catch (IndexOutOfBoundsException ex) {
            // expected
        }
    }

    @Property
    public void getIndexOfShouldReturnValue(List<Integer> values) {
        MapBasedImmutableList<Integer> target = new MapBasedImmutableList<Integer>()
                .addAll(values);

        for (int value : values) {
            assertEquals(value, (int) target.get(target.indexOf(value)));
            assertEquals(value, (int) target.get(target.lastIndexOf(value)));
        }
    }

    @Property
    public void getLastIndexOfVsIndexOf(List<Integer> values) {
        MapBasedImmutableList<Integer> target = new MapBasedImmutableList<Integer>()
                .addAll(values);

        for (int value : values) {
            assertEquals(0, IntStream.range(0, (int) target.indexOf(value))
                    .filter(i -> target.get(i).equals(value))
                    .count());
            assertEquals(0, IntStream.range((int) target.lastIndexOf(value) + 1, (int) target.size())
                    .filter(i -> target.get(i).equals(value))
                    .count());
            assertThat(target.indexOf(value), lessThanOrEqualTo(target.lastIndexOf(value)));
        }
    }

    @Property
    public void indexOfShouldReturnMinusOneForNonExistingValue(List<@InRange(minInt = -1000, maxInt = -1) Integer> values, @InRange(minInt = 0, maxInt = 1000) Integer query) {
        MapBasedImmutableList<Integer> target = new MapBasedImmutableList<Integer>()
                .addAll(values);

        assertEquals(-1, target.indexOf(query));
        assertEquals(-1, target.lastIndexOf(query));
    }
}