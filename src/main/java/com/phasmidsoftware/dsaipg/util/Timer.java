package com.phasmidsoftware.dsaipg.util;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

/**
 * Class which is able to time the running of functions.
 */
public class Timer {

    // Instance variables
    private long ticks = 0L;
    private int laps = 0;
    private boolean running = false;

    final static LazyLogger logger = new LazyLogger(Timer.class);

    /**
     * Run the given function n times, once per "lap" and then return the result of calling meanLapTime().
     * The clock will be running when the method is invoked and when it is quit.
     * <p>
     * This is the simplest form of repeat.
     *
     * @param n        the number of repetitions.
     * @param function a function which yields a T.
     * @param <T>      the type supplied by function (may be Void).
     * @return the average milliseconds per repetition.
     */
    public <T> double repeat(int n, Supplier<T> function) {
        for (int i = 0; i < n; i++) {
            function.get();
            lap();
        }
        pause();
        final double result = meanLapTime();
        resume();
        return result;
    }

    /**
     * Run the given functions n times, once per "lap" and then return the mean lap time.
     *
     * @param n        the number of repetitions.
     * @param supplier a function which supplies a different T value for each repetition.
     * @param function a function T=>U and which is to be timed.
     * @param <T>      the type which is supplied by supplier and passed in to function.
     * @param <U>      the type which is the result of <code>function</code> (may be Void).
     * @return the average milliseconds per repetition.
     */
    public <T, U> double repeat(int n, Supplier<T> supplier, Function<T, U> function) {
        return repeat(n, false, supplier, function, null, null);
    }

    /**
     * Pause (without counting a lap); run the given functions n times while being timed, i.e., once per "lap",
     * and finally return the result of calling meanLapTime().
     *
     * @param n            the number of repetitions.
     * @param warmup       true if this is in the warmup phase.
     * @param supplier     a function which supplies a T value.
     * @param function     a function T=>U and which is to be timed.
     * @param preFunction  a function which pre-processes a T value and which precedes the call of function, but which is not timed (may be null).
     * @param postFunction a function which consumes a U and which succeeds the call of function, but which is not timed (may be null).
     * @param <T>          the type which is supplied by supplier, processed by prefunction (if any), and passed in to function.
     * @param <U>          the type which is the result of function and the input to postFunction (if any).
     * @return the average milliseconds per repetition.
     */
    public <T, U> double repeat(int n, boolean warmup, Supplier<T> supplier, Function<T, U> function, UnaryOperator<T> preFunction, Consumer<U> postFunction) {
        if (warmup) {
            // Warmup phase (run without timing)
            for (int i = 0; i < n; i++) {
                T value = supplier.get();
                if (preFunction != null) {
                    value = preFunction.apply(value);
                }
                function.apply(value);
                if (postFunction != null) {
                    postFunction.accept(function.apply(value));
                }
            }
        }

        // Timing phase
        for (int i = 0; i < n; i++) {
            T value = supplier.get();
            if (preFunction != null) {
                value = preFunction.apply(value);
            }
            long startTime = getClock();
            U result = function.apply(value);
            long endTime = getClock();
            ticks += (endTime - startTime);
            if (postFunction != null) {
                postFunction.accept(result);
            }
            lap();
        }

        pause();
        final double result = meanLapTime();
        resume();
        return result;
    }

    /**
     * Stop this Timer and return the mean lap time in milliseconds.
     *
     * @return the average milliseconds used by each lap.
     * @throws TimerException if this Timer is not running.
     */
    public double stop() {
        pauseAndLap();
        doTrace(() -> "stop timer");
        return meanLapTime();
    }

    /**
     * Return the mean lap time in milliseconds for this paused timer.
     *
     * @return the average milliseconds used by each lap.
     * @throws TimerException if this Timer is running.
     */
    public double meanLapTime() {
        if (running) throw new TimerException();
        return toMillisecs(ticks) / laps;
    }

    /**
     * Pause this timer at the end of a "lap" (repetition).
     * The lap counter will be incremented by one.
     *
     * @throws TimerException if this Timer is not running.
     */
    public void pauseAndLap() {
        lap();
        ticks += getClock();
        running = false;
        doTrace(() -> "pause timer and lap after millisecs: " + ticks * 1.0E-6);
    }

    /**
     * Resume this timer to begin a new "lap" (repetition).
     *
     * @throws TimerException if this Timer is already running.
     */
    public void resume() {
        if (running) throw new TimerException();
        ticks -= getClock();
        doTrace(() -> "resume timer");
        running = true;
    }

    /**
     * Increment the lap counter without pausing.
     * This is the equivalent of calling pause and resume.
     *
     * @throws TimerException if this Timer is not running.
     */
    public void lap() {
        if (!running) throw new TimerException();
        laps++;
        doTrace(() -> "lap " + laps);
    }

    /**
     * Pause this timer during a "lap" (repetition).
     * The lap counter will remain the same.
     *
     * @throws TimerException if this Timer is not running.
     */
    public void pause() {
        pauseAndLap();
        laps--;
        doTrace(() -> "pause timer");
    }

    /**
     * Method to yield the total number of milliseconds elapsed.
     * NOTE: an exception will be thrown if this is called while the timer is running.
     *
     * @return the total number of milliseconds elapsed for this timer.
     */
    public double millisecs() {
        if (running) throw new TimerException();
        return toMillisecs(ticks);
    }

    @Override
    public String toString() {
        return "Timer{" +
                "ticks=" + ticks +
                ", laps=" + laps +
                ", running=" + running +
                '}';
    }

    /**
     * Construct a new Timer and set it running.
     */
    public Timer() {
        Supplier<String> f = () -> "create new timer";
        doTrace(f);
        resume();
    }

    private static <T> void doTrace(final boolean condition, Supplier<String> messageFunction) {
        if (logger.isTraceEnabled() && condition) logger.trace(messageFunction.get());
    }

    private static void doTrace(Supplier<String> f) {
        doTrace(true, f);
    }

    /**
     * Get the number of ticks from the system clock.
     * <p>
     * NOTE: (Maintain consistency) There are two system methods for getting the clock time.
     * Ensure that this method is consistent with toMillisecs.
     *
     * @return the number of ticks for the system clock. Currently defined as nano time.
     */
    private static long getClock() {
        return System.nanoTime(); // returns the system time in nanoseconds
    }

    /**
     * NOTE: (Maintain consistency) There are two system methods for getting the clock time.
     * Ensure that this method is consistent with getTicks.
     *
     * @param ticks the number of clock ticks -- currently in nanoseconds.
     * @return the corresponding number of milliseconds.
     */
    private static double toMillisecs(long ticks) {
        return ticks * 1.0E-6; // Convert nanoseconds to milliseconds
    }

    // TimerException is a custom unchecked exception used to indicate errors or invalid states
    static class TimerException extends RuntimeException {
        public TimerException() {
        }

        public TimerException(String message) {
            super(message);
        }

        public TimerException(String message, Throwable cause) {
            super(message, cause);
        }

        public TimerException(Throwable cause) {
            super(cause);
        }
    }
}
