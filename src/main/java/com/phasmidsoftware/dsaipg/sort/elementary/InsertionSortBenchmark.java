package com.phasmidsoftware.dsaipg.sort.elementary;

import java.util.Random;

public class InsertionSortBenchmark {

    public static void main(String[] args) {
        int[] testSizes = {1, 2, 4, 8, 16, 32, 64}; // Doubling n sizes
        Random random = new Random();

        for (int n : testSizes) {
            System.out.println("Testing with array size: " + n);

            // Create and sort each type of array
            Integer[] randomArray = generateRandomArray(n, random);
            Integer[] orderedArray = generateOrderedArray(n);
            Integer[] partiallyOrderedArray = generatePartiallyOrderedArray(n, random);
            Integer[] reverseOrderedArray = generateReverseOrderedArray(n);

            // Measure and print the time for each type of array
            System.out.println("Random array:");
            measureSortingTime(randomArray);

            System.out.println("Ordered array:");
            measureSortingTime(orderedArray);

            System.out.println("Partially-ordered array:");
            measureSortingTime(partiallyOrderedArray);

            System.out.println("Reverse-ordered array:");
            measureSortingTime(reverseOrderedArray);

            System.out.println();
        }
    }

    /**
     * Measure the sorting time using InsertionSortBasic.
     */
    private static void measureSortingTime(Integer[] array) {
        long startTime = System.nanoTime();
        InsertionSortBasic<Integer> sorter = new InsertionSortBasic<>(Comparable::compareTo);
        sorter.sort(array);
        long endTime = System.nanoTime();
        long duration = endTime - startTime; // in nanoseconds
        System.out.println("Time taken: " + duration + " ns");
    }

    /**
     * Generate an array of random integers.
     */
    private static Integer[] generateRandomArray(int size, Random random) {
        Integer[] array = new Integer[size];
        for (int i = 0; i < size; i++) {
            array[i] = random.nextInt(10000);  // Random integers between 0 and 9999
        }
        return array;
    }

    /**
     * Generate an already ordered array.
     */
    private static Integer[] generateOrderedArray(int size) {
        Integer[] array = new Integer[size];
        for (int i = 0; i < size; i++) {
            array[i] = i;  // Ordered from 0 to size-1
        }
        return array;
    }

    /**
     * Generate a partially ordered array.
     * (For example, first half sorted, second half random)
     */
    private static Integer[] generatePartiallyOrderedArray(int size, Random random) {
        Integer[] array = new Integer[size];
        for (int i = 0; i < size / 2; i++) {
            array[i] = i;  // First half ordered
        }
        for (int i = size / 2; i < size; i++) {
            array[i] = random.nextInt(10000);  // Second half random
        }
        return array;
    }

    /**
     * Generate a reverse-ordered array.
     */
    private static Integer[] generateReverseOrderedArray(int size) {
        Integer[] array = new Integer[size];
        for (int i = 0; i < size; i++) {
            array[i] = size - i - 1;  // Reverse order from size-1 to 0
        }
        return array;
    }
}
