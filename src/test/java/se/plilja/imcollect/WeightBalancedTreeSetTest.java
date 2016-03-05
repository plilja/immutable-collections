package se.plilja.imcollect;

public class WeightBalancedTreeSetTest extends CollectionsBaseTest {

    public WeightBalancedTreeSetTest() {
        super(new WeightBalancedTreeSet<>(Integer::compareTo));
    }
}