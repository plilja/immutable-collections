package se.plilja.imcollect;

import java.util.*;
import java.util.function.Function;

final class WeightBalancedTree<K> {
    private static final double OMEGA = 2.5;
    private static final double ALPHA = 1.5;
    private static final double DELTA = 1;
    private static final int LT = -1;
    private static final int EQ = 0;
    private static final int GT = 1;

    private final Node<K> root;
    private final Comparator<K> comp;

    private WeightBalancedTree(Node<K> root, Comparator<K> comparator) {
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

    public Optional<K> lookup(Node<K> node, K key) {
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
        Node<K> newRoot = add(root, key);
        return new WeightBalancedTree<>(newRoot, comp);
    }

    private Node<K> add(Node<K> node, K key) {
        if (node == null) {
            return new Node<>(key, null, null, 1);
        } else {
            switch (compare(key, node.key)) {
                case LT:
                    Node<K> newLeft = add(node.left, key);
                    return balance(node(node.key, newLeft, node.right));
                case EQ:
                    return node(key, node.left, node.right);
                case GT:
                    Node<K> newRight = add(node.right, key);
                    return balance(node(node.key, node.left, newRight));
                default:
                    throw new IllegalStateException();
            }
        }
    }

    private Node<K> balance(Node<K> node) {
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

    private long nodeSize(Node<K> node) {
        if (node == null) {
            return 0;
        } else {
            return node.size;
        }
    }

    private Node<K> doubleRight(Node<K> node) {
        Node<K> L = node.left;
        Node<K> R = node.right;
        Node<K> LR = L.right;
        return node(LR.key,
                node(L.key, L.left, LR.left),
                node(node.key, LR.right, R));
    }

    private Node<K> singleRight(Node<K> node) {
        Node<K> L = node.left;
        Node<K> R = node.right;
        Node<K> newR = node(node.key, L.right, R);
        return node(L.key, L.left, newR);
    }

    private Node<K> doubleLeft(Node<K> node) {
        Node<K> L = node.left;
        Node<K> R = node.right;
        Node<K> RL = R.left;
        return node(RL.key,
                node(node.key, L, RL.left),
                node(R.key, RL.right, R.right));
    }

    private Node<K> singleLeft(Node<K> node) {
        Node<K> left = node.left;
        Node<K> right = node.right;
        Node<K> newLeft = node(node.key, left, right.left);
        return node(right.key, newLeft, right.right);
    }

    public WeightBalancedTree<K> remove(K key) {
        Node<K> newRoot = remove(root, key);
        return new WeightBalancedTree<>(newRoot, comp);
    }

    private Node<K> remove(Node<K> node, K key) {
        if (node == null) {
            return null;
        } else {
            switch (compare(key, node.key)) {
                case LT:
                    Node<K> newLeft = remove(node.left, key);
                    return balance(node(node.key, newLeft, node.right));
                case EQ:
                    return concat(node.left, node.right);
                case GT:
                    Node<K> newRight = remove(node.right, key);
                    return balance(node(node.key, node.left, newRight));
                default:
                    throw new IllegalStateException();
            }
        }
    }

    private Node<K> concat(Node<K> left, Node<K> right) {
        if (right == null) {
            return left;
        } else if (left == null) {
            return right;
        } else if (nodeSize(left) > nodeSize(right)) {
            Pair<K, Node<K>> r = popMax(left);
            K m = r.first;
            Node<K> newLeft = r.second;
            return node(m, newLeft, right);
        } else {
            Pair<K, Node<K>> r = popMin(right);
            K m = r.first;
            Node<K> newRight = r.second;
            return node(m, left, newRight);
        }
    }

    private Pair<K, Node<K>> popMin(Node<K> node) {
        if (node.left == null) {
            return Pair.make(node.key, node.right);
        } else {
            Pair<K, Node<K>> r = popMin(node.left);
            return Pair.make(r.first, balance(node(node.key, r.second, node.right)));
        }
    }

    private Pair<K, Node<K>> popMax(Node<K> node) {
        if (node.right == null) {
            return Pair.make(node.key, node.left);
        } else {
            Pair<K, Node<K>> r = popMax(node.right);
            return Pair.make(r.first, balance(node(node.key, node.left, r.second)));
        }
    }

    public <T> Iterator<T> iterator(Function<K, T> mapping) {
        return new WeightBalancedTreeIterator<>(root, mapping);
    }

    public long size() {
        return nodeSize(root);
    }

    /**
     * Test method. Validates that the tree is consistent.
     */
    Pair<Boolean, String> isConsistent() {
        return isConsistent(root);
    }

    private Pair<Boolean, String> isConsistent(Node<K> node) {
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

    private Node<K> node(K key, Node<K> left, Node<K> right) {
        long n = nodeSize(left) + nodeSize(right) + 1;
        return new Node<>(key, left, right, n);
    }

    private class Node<T> {
        final T key;
        final Node<T> left;
        final Node<T> right;
        final long size;

        private Node(T key, Node<T> left, Node<T> right, long size) {
            this.key = key;
            this.left = left;
            this.right = right;
            this.size = size;
        }
    }

    private class WeightBalancedTreeIterator<T> implements Iterator<T> {
        private Deque<Node<K>> stack;
        private Node<K> next;
        private Function<K, T> nodeMapper;

        public WeightBalancedTreeIterator(Node<K> root, Function<K, T> nodeMapper) {
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
            Node<K> res = next;
            next = extractNext();
            return nodeMapper.apply(res.key);
        }

        private Node<K> extractNext() {
            if (stack.isEmpty()) {
                return null;
            } else {
                Node<K> p = stack.pop();
                goLeft(p.right);
                return p;
            }
        }

        private void goLeft(Node<K> node) {
            Node<K> curr = node;
            while (curr != null) {
                stack.push(curr);
                curr = curr.left;
            }
        }
    }


}
