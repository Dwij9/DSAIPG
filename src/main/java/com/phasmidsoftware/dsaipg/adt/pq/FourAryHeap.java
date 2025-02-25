package com.phasmidsoftware.dsaipg.adt.pq;

import java.util.Comparator;

public class FourAryHeap<K> {
    private final K[] heap;
    private int size;
    private final Comparator<K> comparator;
    private final boolean useFloyd;

    @SuppressWarnings("unchecked")
    public FourAryHeap(K capacity, Comparator<K> comparator, boolean useFloyd) {
        this.heap = (K[]) new Object[(int) capacity + 1];
        this.size = 0;
        this.comparator = comparator;
        this.useFloyd = useFloyd;
    }

    public void give(K key) {
        if (size == heap.length - 1) return;
        heap[++size] = key;
        swim(size);
    }

    public K take() {
        if (size == 0) return null;
        K result = heap[1];
        swap(1, size--);
        heap[size + 1] = null;
        if (useFloyd) snake(1);
        else sink(1);
        return result;
    }

    private void swim(int k) {
        while (k > 1 && less(parent(k), k)) {
            swap(k, parent(k));
            k = parent(k);
        }
    }

    private int sink(int k) {
        while (firstChild(k) <= size) {
            int j = firstChild(k);
            int maxChild = j;
            for (int i = 1; i < 4 && j + i <= size; i++) {
                if (less(maxChild, j + i)) maxChild = j + i;
            }
            if (!less(k, maxChild)) break;
            swap(k, maxChild);
            k = maxChild;
        }
        return k;
    }

    private void snake(int k) {
        int pos = sink(k);
        swim(pos);
    }

    private int parent(int k) {
        return (k - 2) / 4 + 1;
    }

    private int firstChild(int k) {
        return (k - 1) * 4 + 2;
    }

    private boolean less(int i, int j) {
        return comparator.compare(heap[i], heap[j]) < 0;
    }

    private void swap(int i, int j) {
        K temp = heap[i];
        heap[i] = heap[j];
        heap[j] = temp;
    }

    public int size() {
        return size;
    }
}