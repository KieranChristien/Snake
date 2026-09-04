package main.java.snake.math;

public final class Time {
    private static final long NANOS_PER_SECOND = 1_000_000_000L;

    private Time() {}

    public static long now() {
        return System.nanoTime();
    }

    public static long secondsToNanos(double seconds) {
        return (long) (seconds * NANOS_PER_SECOND);
    }

    public static double nanosToSeconds(long nanos) {
        return nanos / (double) NANOS_PER_SECOND;
    }
}
