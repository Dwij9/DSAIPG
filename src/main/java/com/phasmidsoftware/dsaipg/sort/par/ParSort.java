package com.phasmidsoftware.dsaipg.sort.par;

import java.util.Arrays;
import java.util.concurrent.RecursiveAction;

/**
 * ParSort is a parallel sorting algorithm that uses the Fork/Join Framework
 * to sort arrays in parallel. It switches to sequential sorting for small arrays
 * based on a cutoff value.
 */
public class ParSort extends RecursiveAction {

    public static int cutoff = 1000; // Default cutoff for parallel sorting

    private final int[] array;
    private final int start;
    private final int end;

    /**
     * Constructor for ParSort.
     *
     * @param array The array to be sorted.
     * @param start The starting index of the range to sort.
     * @param end   The ending index of the range to sort.
     */
    public ParSort(int[] array, int start, int end) {
        this.array = array;
        this.start = start;
        this.end = end;
    }

    /**
     * Sorts the given array in parallel or sequentially based on the cutoff value.
     *
     * @param array The array to be sorted.
     * @param start The starting index of the range to sort.
     * @param end   The ending index of the range to sort.
     */
    public static void sort(int[] array, int start, int end) {
        if (array == null || start < 0 || end > array.length || start > end) {
            throw new ArrayIndexOutOfBoundsException("Invalid array or range");
        }

        ParSort task = new ParSort(array, start, end);
        task.invoke(); // Start the Fork/Join task
    }

    @Override
    protected void compute() {
        if (end - start <= cutoff) {
            // Sequential sort for small arrays
            Arrays.sort(array, start, end);
        } else {
            // Divide the array into two halves and sort them in parallel
            int mid = start + (end - start) / 2;

            ParSort leftTask = new ParSort(array, start, mid);
            ParSort rightTask = new ParSort(array, mid, end);

            // Fork both tasks to run in parallel
            invokeAll(leftTask, rightTask);

            // Merge the sorted halves
            merge(array, start, mid, end);
        }
    }

    /**
     * Merges two sorted subarrays into a single sorted array.
     *
     * @param array The array containing the subarrays.
     * @param start The starting index of the first subarray.
     * @param mid   The ending index of the first subarray and the starting index of the second subarray.
     * @param end   The ending index of the second subarray.
     */
    private void merge(int[] array, int start, int mid, int end) {
        int[] temp = new int[end - start];
        int i = start, j = mid, k = 0;

        // Merge the two halves into the temporary array
        while (i < mid && j < end) {
            if (array[i] <= array[j]) {
                temp[k++] = array[i++];
            } else {
                temp[k++] = array[j++];
            }
        }

        // Copy remaining elements from the first half (if any)
        while (i < mid) {
            temp[k++] = array[i++];
        }

        // Copy remaining elements from the second half (if any)
        while (j < end) {
            temp[k++] = array[j++];
        }

        // Copy the merged elements back into the original array
        System.arraycopy(temp, 0, array, start, temp.length);
    }
}