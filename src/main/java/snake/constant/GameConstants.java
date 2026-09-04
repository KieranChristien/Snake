package main.java.snake.constant;

import main.java.snake.math.Direction;
import main.java.snake.math.Time;

public final class GameConstants {
    private GameConstants() {}

    public static boolean debugHitboxes = false;
    public static final Direction START_DIR = Direction.RIGHT;
    public static final long START_NANO = Time.now();
}
