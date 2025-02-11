package com.phasmidsoftware.dsaipg.misc.randomwalk;

import java.util.concurrent.ThreadLocalRandom;

/**
 * The RandomWalk class simulates a two-dimensional random walk.
 * A "drunkard" moves in a random direction for a specified number of steps,
 * and the distance from the starting point is measured. Additionally,
 * multiple random walk experiments can be performed to compute average distances.
 */
public class RandomWalk {

    private int x = 0;
    private int y = 0;

    /**
     * Method to compute the distance from the origin (the lamp-post where the drunkard starts)
     * to his current position.
     *
     * @return the (Euclidean) distance from the origin to the current position.
     */
    public double distance() {
        return Math.sqrt((long)x * x + (long)y * y);
    }

    /**
     * Private method to move the current position.
     *
     * @param dx the distance moved in the x direction
     * @param dy the distance moved in the y direction
     */
    private void move(int dx, int dy) {
        x += dx;
        y += dy;
    }

    /**
     * Perform a random walk of m steps.
     *
     * @param m the number of steps the drunkard takes
     */
    private void randomWalk(int m) {
        for (int i = 0; i < m; i++) {
            randomMove();
        }
    }

    /**
     * Private method to generate a random move according to the rules.
     * Moves are one of: (+-1, 0), (0, +-1)
     */
    private void randomMove() {
        int direction = ThreadLocalRandom.current().nextInt(4);  // Generates a random number between 0 and 3
        switch (direction) {
            case 0: move(1, 0); break; // move right
            case 1: move(-1, 0); break; // move left
            case 2: move(0, 1); break; // move up
            case 3: move(0, -1); break; // move down
        }
    }

    /**
     * Perform multiple random walk experiments, returning the mean distance.
     *
     * @param m the number of steps for each experiment
     * @param n the number of experiments to run
     * @return the mean distance
     */
    public static double randomWalkMulti(int m, int n) {
        double totalDistance = 0;
        for (int i = 0; i < n; i++) {
            RandomWalk walk = new RandomWalk();
            walk.randomWalk(m);
            totalDistance += walk.distance();
        }
        return totalDistance / n;
    }

    /**
     * The main method serves as the entry point to the RandomWalk program.
     * It performs either a single random walk experiment or several experiments,
     * based on the provided input arguments, and prints the mean distance.
     *
     * @param args command-line arguments:
     *             args[0] specifies the number of steps for a random walk (required),
     *             and args[1] optionally specifies the number of experiments (default is 30).
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            throw new RuntimeException("Syntax: RandomWalk steps [experiments]");
        }

        int m = Integer.parseInt(args[0]);
        int n = 30;
        if (args.length > 1) n = Integer.parseInt(args[1]);

        long startTime = System.nanoTime();  // Start time for performance measurement
        double meanDistance = randomWalkMulti(m, n);
        long endTime = System.nanoTime();  // End time for performance measurement

        System.out.println(m + " steps: " + meanDistance + " over " + n + " experiments");
        System.out.println("Execution time: " + (endTime - startTime) + " nanoseconds");
    }
}
