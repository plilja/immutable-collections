package se.plilja.imcollect;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Optional;
import java.util.Stack;
import java.util.function.Function;

final class WeightBalancedTree<K, V> implements ImmutableMap<K, V> {
    private static final double OMEGA = 2.5;
    private static final double ALPHA = 1.5;
    private static final double DELTA = 1;
    private static final int LT = -1;
    private static final int EQ = 0;
    private static final int GT = 1;

    private final Node<K, V> root;
    private final Comparator<K> comp;

    public WeightBalancedTree(Node<K, V> root, Comparator<K> comparator) {
        this.root = root;
        this.comp = comparator;
    }

    @Override
    public Optional<V> lookup(K key) {
        if (key == null) {
            return Optional.empty();
        } else {
            return lookup(root, key);
        }
    }

    public Optional<V> lookup(Node<K, V> node, K key) {
        if (node == null) {
            return Optional.empty();
        } else {
            switch (compare(key, node.key)) {
                case LT:
                    return lookup(node.left, key);
                case EQ:
                    return Optional.of(node.value);
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

    @Override
    public WeightBalancedTree<K, V> put(K key, V value) {
        Node<K, V> newRoot = put(root, key, value);
        return new WeightBalancedTree<>(newRoot, comp);
    }

    private Node<K, V> put(Node<K, V> node, K key, V value) {
        if (node == null) {
            return new Node<>(key, value, null, null, 1);
        } else {
            switch (compare(key, node.key)) {
                case LT:
                    Node<K, V> newLeft = put(node.left, key, value);
                    return balance(node(node.key, node.value, newLeft, node.right));
                case EQ:
                    return node(key, value, node.left, node.right);
                case GT:
                    Node<K, V> newRight = put(node.right, key, value);
                    return balance(node(node.key, node.value, node.left, newRight));
                default:
                    throw new IllegalStateException();
            }
        }
    }

    private Node<K, V> balance(Node<K, V> node) {
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

    private long nodeSize(Node<K, V> node) {
        if (node == null) {
            return 0;
        } else {
            return node.size;
        }
    }

    private Node<K, V> doubleRight(Node<K, V> node) {
        Node<K, V> L = node.left;
        Node<K, V> R = node.right;
        Node<K, V> LR = L.right;
        return node(LR.left.key, LR.left.value,
                node(L.key, L.value, L.left, LR.left),
                node(node.key, node.value, LR.right, R));
    }

    private Node<K, V> singleRight(Node<K, V> node) {
        Node<K, V> L = node.left;
        Node<K, V> R = node.right;
        Node<K, V> newR = node(node.key, node.value, L.right, R);
        return node(L.key, L.value, L.left, newR);
    }

    private Node<K, V> doubleLeft(Node<K, V> node) {
        Node<K, V> L = node.left;
        Node<K, V> R = node.right;
        Node<K, V> RL = R.left;
        return node(RL.left.key, RL.left.value,
                node(node.key, node.value, L, RL.left),
                node(R.key, R.value, RL.right, R.right));
    }

    private Node<K, V> singleLeft(Node<K, V> node) {
        Node<K, V> left = node.left;
        Node<K, V> right = node.right;
        Node<K, V> newLeft = node(node.key, node.value, left, right.left);
        return node(right.key, right.value, newLeft, right.right);
    }

    @Override
    public ImmutableMap<K, V> remove(K key) {
        Node<K, V> newRoot = remove(root, key);
        return new WeightBalancedTree<>(newRoot, comp);
    }

    private Node<K, V> remove(Node<K, V> node, K key) {
        if (node == null) {
            return null;
        } else {
            switch (compare(key, node.key)) {
                case LT:
                    Node<K, V> newLeft = remove(node.left, key);
                    return balance(node(node.key, node.value, newLeft, node.right));
                case EQ:
                    return concat(node.left, node.right);
                case GT:
                    Node<K, V> newRight = remove(node.right, key);
                    return balance(node(node.key, node.value, node.left, newRight));
                default:
                    throw new IllegalStateException();
            }
        }
    }

    private Node<K, V> concat(Node<K, V> left, Node<K, V> right) {
        if (right == null) {
            return left;
        } else if (left == null) {
            return right;
        } else if (nodeSize(left) > nodeSize(right)) {
            Pair<Pair<K, V>, Node<K, V>> r = popMax(left);
            Pair<K, V> m = r.first;
            Node<K, V> newLeft = r.second;
            return node(m.first, m.second, newLeft, right);
        } else {
            Pair<Pair<K, V>, Node<K, V>> r = popMin(right);
            Pair<K, V> m = r.first;
            Node<K, V> newRight = r.second;
            return node(m.first, m.second, left, newRight);
        }
    }

    private Pair<Pair<K, V>, Node<K, V>> popMin(Node<K, V> node) {
        if (node.left == null) {
            return Pair.make(Pair.make(node.key, node.value), node.right);
        } else {
            Pair<Pair<K, V>, Node<K, V>> r = popMin(node.left);
            return Pair.make(r.first, balance(node(node.key, node.value, r.second.left, node.right)));
        }
    }

    private Pair<Pair<K, V>, Node<K, V>> popMax(Node<K, V> node) {
        if (node.right == null) {
            return Pair.make(Pair.make(node.key, node.value), node.left);
        } else {
            Pair<Pair<K, V>, Node<K, V>> r = popMax(node.right);
            return Pair.make(r.first, balance(node(node.key, node.value, node.left, r.second.right)));
        }
    }

    @Override
    public Iterable<K> keys() {
        return () -> new WeightBalancedTreeIterator<K>(root, node -> node.key);
    }

    @Override
    public Iterable<V> values() {
        return () -> new WeightBalancedTreeIterator<V>(root, node -> node.value);
    }

    @Override
    public long size() {
        return root.size;
    }

    private Node<K, V> node(K key, V value, Node<K, V> left, Node<K, V> right) {
        int n = 1;
        if (left != null) {
            n += nodeSize(left);
        }
        if (right != null) {
            n += nodeSize(right);
        }
        return new Node<>(key, value, left, right, n);
    }

    private class Node<K, V> {
        final K key;
        final V value;
        final Node<K, V> left;
        final Node<K, V> right;
        final long size;

        private Node(K key, V value, Node<K, V> left, Node<K, V> right, long size) {
            this.key = key;
            this.value = value;
            this.left = left;
            this.right = right;
            this.size = size;
        }
    }

    private class WeightBalancedTreeIterator<T> implements Iterator<T> {
        private Stack<Node<K, V>> s;
        private T next;
        private Function<Node<K, V>, T> f;

        public WeightBalancedTreeIterator(Node<K, V> root, Function<Node<K, V>, T> f) {
            this.f = f;
            s.push(root);
            next = extractNext();
        }

        @Override
        public boolean hasNext() {
            return next != null;
        }

        @Override
        public T next() {
            T res = next;
            next = extractNext();
            return res;
        }

        private T extractNext() {
            return null; //todo
        }

    }


}
