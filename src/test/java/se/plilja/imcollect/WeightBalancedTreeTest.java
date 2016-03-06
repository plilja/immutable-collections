package se.plilja.imcollect;

import com.pholser.junit.quickcheck.Property;
import com.pholser.junit.quickcheck.generator.InRange;
import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;
import org.junit.runner.RunWith;

import java.util.List;

import static org.junit.Assert.assertTrue;

@RunWith(JUnitQuickcheck.class)
public class WeightBalancedTreeTest {

    @Property(trials = 500)
    public void treeShouldAlwaysBeConsistent(List<Operation> operations, List<@InRange(minInt = -50, maxInt = 50) Integer> values) {
        WeightBalancedTree<Integer> target = new WeightBalancedTree<>(Integer::compare);

        for (int i = 0; i < Integer.min(operations.size(), values.size()); i++) {
            switch (operations.get(i)) {
                case ADD:
                    target = target.add(values.get(i));
                    break;
                case REMOVE:
                    target = target.remove(values.get(i));
                    break;
                default:
                    throw new IllegalArgumentException();
            }

            Pair<Boolean, String> consistent = target.isConsistent();
            if (!consistent.first) {
                System.err.println(consistent.second);
            }
            assertTrue(consistent.first);
        }
    }

    private enum Operation {
        ADD, REMOVE
    }
}
