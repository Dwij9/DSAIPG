package com.phasmidsoftware.dsaipg.adt.threesum;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

/**
 * Implementation of ThreeSum using a "calipers" approach.
 * This class requires the input array to be sorted.
 */
public class ThreeSumQuadraticWithCalipers implements ThreeSum {

    /**
     * Constructor requires a sorted array.
     *
     * @param ints a sorted array of integers.
     */
    public ThreeSumQuadraticWithCalipers(int[] ints) {
        this.a = ints;
        this.length = ints.length;
    }

    /**
     * Get all unique Triples where the sum is zero.
     *
     * @return an array of Triple objects.
     */
    public Triple[] getTriples() {
        List<Triple> triples = new ArrayList<>();
        for (int i = 0; i < length - 2; i++) {
            triples.addAll(calipers(a, i, Triple::sum));
        }
        return triples.stream().distinct().toArray(Triple[]::new);
    }

    /**
     * Performs a two-pointer search from both ends, fixing a[i] as the first element.
     *
     * @param a        a sorted array of ints.
     * @param i        the index of the first element in the triple.
     * @param function function to test the triple (e.g., checking if sum == 0).
     * @return a list of valid triples.
     */
    public static List<Triple> calipers(int[] a, int i, Function<Triple, Integer> function) {
        List<Triple> triples = new ArrayList<>();
        int left = i + 1;
        int right = a.length - 1;

        while (left < right) {
            Triple t = new Triple(a[i], a[left], a[right]);
            int value = function.apply(t);

            if (value == 0) {
                triples.add(t);
                left++;
                right--;

                // Skip duplicates
                while (left < right && a[left] == a[left - 1]) left++;
                while (left < right && a[right] == a[right + 1]) right--;

            } else if (value < 0) {
                left++;
            } else {
                right--;
            }
        }
        return triples;
    }

    private final int[] a;
    private final int length;
}
