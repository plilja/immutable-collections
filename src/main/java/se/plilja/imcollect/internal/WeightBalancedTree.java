package se.plilja.imcollect.internal;

import java.util.*;
import java.util.function.Function;

public final class WeightBalancedTree<K> {
    private static final double OMEGA = 2.5;
    private static final double ALPHA = 1.5;
    private static final double DELTA = 1;
    private static final int LT = -1;
    private static final int EQ = 0;
    private static final int GT = 1;

    private final WBNode<K> root;
    private final Comparator<K> comp;

    private WeightBalancedTree(WBNode<K> root, Comparator<K> comparator) {
        this.root = root;
        this.comp = comparator;
    }

    public WeightBalancedTree(Comparator<K> comparator) {
        this(null, comparator);
    }

    public Optional<K> lookup(K key) {
        if (key == null) {
            return Optional.empty();
        } else {
            return lookup(root, key);
        }
    }

    public Optional<K> lookup(WBNode<K> node, K key) {
        if (node == null) {
            return Optional.empty();
        } else {
            switch (compare(key, node.key)) {
                case LT:
                    return lookup(node.left, key);
                case EQ:
                    return Optional.of(node.key);
                case GT:
                    return lookup(node.right, key);
                default:
                    throw new IllegalStateException();
            }
        }
    }

    private int compare(K k1, K k2) {
        return Integer.signum(comp.compare(k1, k2));
    }

    public WeightBalancedTree<K> add(K key) {
        WBNode<K> newRoot = add(root, key);
        return new WeightBalancedTree<>(newRoot, comp);
    }

    private WBNode<K> add(WBNode<K> node, K key) {
        if (node == null) {
            return new WBNode<>(key, null, null, 1);
        } else {
            switch (compare(key, node.key)) {
                case LT:
                    WBNode<K> newLeft = add(node.left, key);
                    return balance(node(node.key, newLeft, node.right));
                case EQ:
                    return node(key, node.left, node.right);
                case GT:
                    WBNode<K> newRight = add(node.right, key);
                    return balance(node(node.key, node.left, newRight));
                default:
                    throw new IllegalStateException();
            }
        }
    }

    private WBNode<K> balance(WBNode<K> node) {
        if (nodeSize(node) <= 2) {
            return node;
        } else if (nodeSize(node.right) > OMEGA * nodeSize(node.left) + DELTA) {
            if (nodeSize(node.right.left) < ALPHA * nodeSize(node.right.right)) {
                return singleLeft(node);
            } else {
                return doubleLeft(node);
            }
        } else if (nodeSize(node.left) > OMEGA * nodeSize(node.right) + DELTA) {
            if (nodeSize(node.left.right) < ALPHA * nodeSize(node.left.left)) {
                return singleRight(node);
            } else {
                return doubleRight(node);
            }
        } else {
            return node;
        }
    }

    private int nodeSize(WBNode<K> node) {
        if (node == null) {
            return 0;
        } else {
            return node.size;
        }
    }

    private WBNode<K> doubleRight(WBNode<K> node) {
        WBNode<K> L = node.left;
        WBNode<K> R = node.right;
        WBNode<K> LR = L.right;
        return node(LR.key,
                node(L.key, L.left, LR.left),
                node(node.key, LR.right, R));
    }

    private WBNode<K> singleRight(WBNode<K> node) {
        WBNode<K> L = node.left;
        WBNode<K> R = node.right;
        WBNode<K> newR = node(node.key, L.right, R);
        return node(L.key, L.left, newR);
    }

    private WBNode<K> doubleLeft(WBNode<K> node) {
        WBNode<K> L = node.left;
        WBNode<K> R = node.right;
        WBNode<K> RL = R.left;
        return node(RL.key,
                node(node.key, L, RL.left),
                node(R.key, RL.right, R.right));
    }

    private WBNode<K> singleLeft(WBNode<K> node) {
        WBNode<K> left = node.left;
        WBNode<K> right = node.right;
        WBNode<K> newLeft = node(node.key, left, right.left);
        return node(right.key, newLeft, right.right);
    }

    public WeightBalancedTree<K> remove(K key) {
        WBNode<K> newRoot = remove(root, key);
        return new WeightBalancedTree<>(newRoot, comp);
    }

    private WBNode<K> remove(WBNode<K> node, K key) {
        if (node == null) {
            return null;
        } else {
            switch (compare(key, node.key)) {
                case LT:
                    WBNode<K> newLeft = remove(node.left, key);
                    return balance(node(node.key, newLeft, node.right));
                case EQ:
                    return concat(node.left, node.right);
                case GT:
                    WBNode<K> newRight = remove(node.right, key);
                    return balance(node(node.key, node.left, newRight));
                default:
                    throw new IllegalStateException();
            }
        }
    }

    private WBNode<K> concat(WBNode<K> left, WBNode<K> right) {
        if (right == null) {
            return left;
        } else if (left == null) {
            return right;
        } else if (nodeSize(left) > nodeSize(right)) {
            Pair<K, WBNode<K>> r = popMax(left);
            K m = r.first;
            WBNode<K> newLeft = r.second;
            return node(m, newLeft, right);
        } else {
            Pair<K, WBNode<K>> r = popMin(right);
            K m = r.first;
            WBNode<K> newRight = r.second;
            return node(m, left, newRight);
        }
    }

    private Pair<K, WBNode<K>> popMin(WBNode<K> node) {
        if (node.left == null) {
            return Pair.make(node.key, node.right);
        } else {
            Pair<K, WBNode<K>> r = popMin(node.left);
            return Pair.make(r.first, balance(node(node.key, r.second, node.right)));
        }
    }

    private Pair<K, WBNode<K>> popMax(WBNode<K> node) {
        if (node.right == null) {
            return Pair.make(node.key, node.left);
        } else {
            Pair<K, WBNode<K>> r = popMax(node.right);
            return Pair.make(r.first, balance(node(node.key, node.left, r.second)));
        }
    }

    public <T> Iterator<T> iterator(Function<K, T> mapping) {
        return new WeightBalancedTreeIterator<>(root, mapping);
    }

    public int size() {
        return nodeSize(root);
    }

    /**
     * Test method. Validates that the tree is consistent.
     */
    Pair<Boolean, String> isConsistent() {
        return isConsistent(root);
    }

    private Pair<Boolean, String> isConsistent(WBNode<K> node) {
        if (node == null) {
            return Pair.make(true, "");
        } else {
            if (nodeSize(node) != nodeSize(node.left) + nodeSize(node.right) + 1) {
                return Pair.make(false, "Node size not equal to size of node children");
            }
            if (nodeSize(node.left) > OMEGA * nodeSize(node.right) + DELTA || nodeSize(node.right) > OMEGA * nodeSize(node.left) + DELTA) {
                return Pair.make(false, "Tree is not weight balanced");
            }
            Pair<Boolean, String> leftConsistent = isConsistent(node.left);
            Pair<Boolean, String> rightConsistent = isConsistent(node.right);
            return Pair.make(leftConsistent.first && rightConsistent.first, leftConsistent.second + rightConsistent.second);
        }
    }

    private WBNode<K> node(K key, WBNode<K> left, WBNode<K> right) {
        int n = nodeSize(left) + nodeSize(right) + 1;
        return new WBNode<>(key, left, right, n);
    }

    private final class WBNode<T> {
        final T key;
        final WBNode<T> left;
        final WBNode<T> right;
        final int size;

        private WBNode(T key, WBNode<T> left, WBNode<T> right, int size) {
            this.key = key;
            this.left = left;
            this.right = right;
            this.size = size;
        }
    }

    private final class WeightBalancedTreeIterator<T> implements Iterator<T> {
        private Deque<WBNode<K>> stack;
        private WBNode<K> next;
        private Function<K, T> nodeMapper;

        public WeightBalancedTreeIterator(WBNode<K> root, Function<K, T> nodeMapper) {
            this.nodeMapper = nodeMapper;
            stack = new ArrayDeque<>();
            goLeft(root);
            next = extractNext();
        }

        @Override
        public boolean hasNext() {
            return next != null;
        }

        @Override
        public T next() {
            WBNode<K> res = next;
            next = extractNext();
            return nodeMapper.apply(res.key);
        }

        private WBNode<K> extractNext() {
            if (stack.isEmpty()) {
                return null;
            } else {
                WBNode<K> p = stack.pop();
                goLeft(p.right);
                return p;
            }
        }

        private void goLeft(WBNode<K> node) {
            WBNode<K> curr = node;
            while (curr != null) {
                stack.push(curr);
                curr = curr.left;
            }
        }
    }


}
