package main.java.snake.util;

import java.util.concurrent.atomic.AtomicLong;

public final class Now {
    private static final AtomicLong NOW = new AtomicLong();

    private Now() {
    }

    public static void set(long nanos) {
        NOW.set(nanos);
    }

    public static long now() {
        return NOW.get();
    }
}
