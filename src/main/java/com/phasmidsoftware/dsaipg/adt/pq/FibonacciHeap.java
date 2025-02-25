package com.phasmidsoftware.dsaipg.adt.pq;

import java.util.*;

public class FibonacciHeap<K> {
    private static class Node<K> {
        K key;
        Node<K> parent, child, left, right;
        int degree;
        boolean mark;

        Node(K key) {
            this.key = key;
            this.left = this.right = this;
        }
    }

    private Node<K> min;
    private int size;
    private final Comparator<K> comparator;

    public FibonacciHeap(Comparator<K> comparator) {
        this.comparator = comparator;
    }

    public void give(K key) {
        Node<K> node = new Node<>(key);
        if (min == null) {
            min = node;
        } else {
            insertIntoRootList(node);
            if (comparator.compare(key, min.key) < 0) min = node;
        }
        size++;
    }

    public K take() {
        if (min == null) return null;
        K result = min.key;
        if (min.child != null) {
            Node<K> child = min.child;
            do {
                Node<K> next = child.right;
                insertIntoRootList(child);
                child.parent = null;
                child = next;
            } while (child != min.child);
        }
        removeFromRootList(min);
        size--;
        if (size == 0) {
            min = null;
        } else {
            min = min.right;
            consolidate();
        }
        return result;
    }

    private void insertIntoRootList(Node<K> node) {
        if (min == null) {
            min = node;
            return;
        }
        node.right = min.right;
        node.left = min;
        min.right.left = node;
        min.right = node;
    }

    private void removeFromRootList(Node<K> node) {
        node.left.right = node.right;
        node.right.left = node.left;
    }

    private void consolidate() {
        int maxDegree = (int) Math.floor(Math.log(size) / Math.log(2)) + 1;
        Node<K>[] degreeTable = new Node[maxDegree];
        List<Node<K>> roots = new ArrayList<>();
        Node<K> current = min;
        if (current != null) {
            do {
                roots.add(current);
                current = current.right;
            } while (current != min);
        }
        for (Node<K> x : roots) {
            int d = x.degree;
            while (degreeTable[d] != null) {
                Node<K> y = degreeTable[d];
                if (comparator.compare(x.key, y.key) > 0) {
                    Node<K> temp = x;
                    x = y;
                    y = temp;
                }
                link(y, x);
                degreeTable[d] = null;
                d++;
            }
            degreeTable[d] = x;
        }
        min = null;
        for (Node<K> node : degreeTable) {
            if (node != null) {
                if (min == null) {
                    min = node;
                } else {
                    insertIntoRootList(node);
                    if (comparator.compare(node.key, min.key) < 0) min = node;
                }
            }
        }
    }

    private void link(Node<K> child, Node<K> parent) {
        removeFromRootList(child);
        child.parent = parent;
        if (parent.child == null) {
            parent.child = child;
            child.right = child.left = child;
        } else {
            child.right = parent.child.right;
            child.left = parent.child;
            parent.child.right.left = child;
            parent.child.right = child;
        }
        parent.degree++;
        child.mark = false;
    }

    public int size() {
        return size;
    }
}