package main.java.snake.constant;

import main.java.snake.util.Direction;

public final class GameConstants {
    private GameConstants() {}

    public static boolean debugHitboxes = false;
    public static final Direction START_DIR = Direction.RIGHT;
    public static final long START_NANO = System.nanoTime();
}
