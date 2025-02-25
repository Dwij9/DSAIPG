package com.phasmidsoftware.dsaipg.util;

import com.phasmidsoftware.dsaipg.adt.pq.FourAryHeap;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class HeapBenchmark {
    private static final int M = 4095;
    private static final int INSERTS = 16000;
    private static final int REMOVALS = 4000;
    private static final Random random = new Random();

    static class Result {
        double timeMillis;
        Integer maxSpilled;

        Result(double timeMillis, Integer maxSpilled) {
            this.timeMillis = timeMillis;
            this.maxSpilled = maxSpilled;
        }
    }

    private static Result benchmarkFourAryHeap(boolean useFloyd) {
        FourAryHeap<Integer> heap = new FourAryHeap<>(M, Comparator.naturalOrder(), useFloyd);
        List<Integer> spilled = new ArrayList<>();
        Supplier<Integer> supplier = random::nextInt;

        Consumer<Integer> insertOperation = i -> {
            if (heap.size() >= M) {
                spilled.add(heap.take());
            }
            heap.give(i);
        };

        Consumer<Integer> removeOperation = i -> heap.take();

        Benchmark_Timer<Integer> insertTimer = new Benchmark_Timer<>(
                "4-ary Heap Insert " + (useFloyd ? "Floyd" : "Basic"), insertOperation);
        Benchmark_Timer<Integer> removeTimer = new Benchmark_Timer<>(
                "4-ary Heap Remove " + (useFloyd ? "Floyd" : "Basic"), removeOperation);

        double insertTime = insertTimer.runFromSupplier(supplier, INSERTS);
        double removeTime = removeTimer.runFromSupplier(supplier, REMOVALS);

        double totalTime = insertTime + removeTime;
        Integer maxSpilled = spilled.stream().max(Comparator.naturalOrder()).orElse(null);
        return new Result(totalTime, maxSpilled);
    }

    public static void main(String[] args) {
        Result fourAryBasic = benchmarkFourAryHeap(false);
        Result fourAryFloyd = benchmarkFourAryHeap(true);

        System.out.println("Benchmark Results (Total time in ms):");
        System.out.printf("4-ary Heap (Basic): %.3f ms, Max Spilled: %d%n",
                fourAryBasic.timeMillis, fourAryBasic.maxSpilled);
        System.out.printf("4-ary Heap (Floyd): %.3f ms, Max Spilled: %d%n",
                fourAryFloyd.timeMillis, fourAryFloyd.maxSpilled);

        System.out.println("\nLog/Log Plot Data:");
        double logN = Math.log(M);
        System.out.println("log(N) = " + logN);
        System.out.println("4-ary Basic: log(time) = " + Math.log(fourAryBasic.timeMillis));
        System.out.println("4-ary Floyd: log(time) = " + Math.log(fourAryFloyd.timeMillis));
    }
}