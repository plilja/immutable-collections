package se.plilja.imcollect;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Optional;
import java.util.Stack;
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
        return node(LR.left.key,
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
        return node(RL.left.key,
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
            return Pair.make(r.first, balance(node(node.key, r.second.left, node.right)));
        }
    }

    private Pair<K, Node<K>> popMax(Node<K> node) {
        if (node.right == null) {
            return Pair.make(node.key, node.left);
        } else {
            Pair<K, Node<K>> r = popMax(node.right);
            return Pair.make(r.first, balance(node(node.key, node.left, r.second.right)));
        }
    }

    public Iterator<K> iterator() {
        return new WeightBalancedTreeIterator<K>(root, node -> node.key);
    }

    public long size() {
        return nodeSize(root);
    }

    private Node<K> node(K key, Node<K> left, Node<K> right) {
        int n = 1;
        if (left != null) {
            n += nodeSize(left);
        }
        if (right != null) {
            n += nodeSize(right);
        }
        return new Node<>(key, left, right, n);
    }

    private class Node<K> {
        final K key;
        final Node<K> left;
        final Node<K> right;
        final long size;

        private Node(K key, Node<K> left, Node<K> right, long size) {
            this.key = key;
            this.left = left;
            this.right = right;
            this.size = size;
        }
    }

    private class WeightBalancedTreeIterator<T> implements Iterator<T> {
        private Stack<Node<K>> s;
        private T next;
        private Function<Node<K>, T> f;

        public WeightBalancedTreeIterator(Node<K> root, Function<Node<K>, T> f) {
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
