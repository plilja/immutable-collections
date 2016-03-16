package se.plilja.imcollect;

import org.junit.Test;

import java.lang.reflect.Modifier;
import java.util.*;

import static org.junit.Assert.assertTrue;
public class ImmutabilityTest {
    private final List<Class<?>> allowedMutability = Collections.singletonList(Comparator.class);

    @Test
    public void allClassesShouldBeImmutable() {
        assertImmutable(MapBasedImmutableList.class);
        assertImmutable(Pair.class);
        assertImmutable(ReversedImmutableList.class);
        assertImmutable(WeightBalancedTree.class);
        assertImmutable(WeightBalancedTreeMap.class);
        assertImmutable(WeightBalancedTreeSet.class);
    }

    private void assertImmutable(Class<?> clazz) {
        assertImmutable(clazz, new HashSet<>());
    }
    private void assertImmutable(Class<?> clazz, Set<Class<?>> visited) {
        if (visited.contains(clazz) || allowedMutability.contains(clazz)) {
            return;
        }
        visited.add(clazz);

        assertTrue(String.format("Class should be final (%s)", clazz.getName()), Modifier.isFinal(clazz.getModifiers()));

        Arrays.stream(clazz.getDeclaredFields())
                .filter(field -> !isGenericType(field.getType()))
                .forEach(field -> {
                    assertTrue(String.format("Field should be final (%s#%s)", clazz.getName(), field.getName()),
                            Modifier.isFinal(field.getModifiers()));
                    assertImmutable(field.getType(), visited);
                });
    }

    private boolean isGenericType(Class<?> clazz) {
        return clazz.equals(Object.class);
    }
}
