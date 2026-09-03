package main.java.snake.constant;

public final class LevelConstants {
    private LevelConstants() {}

    public static final int GRID_SCALE = 38;
    public static final int HALF_GRID_SCALE = GRID_SCALE / 2;
    public static final int COLUMNS = 17;
    public static final int ROWS = 15;
    public static final int WIDTH = GRID_SCALE * COLUMNS;
    public static final int HEIGHT = GRID_SCALE * ROWS;
    public static final int MIN_X = (WindowConstants.WIDTH - WIDTH) / 2;
    public static final int MAX_X = MIN_X + WIDTH;
    public static final int MIN_Y = (WindowConstants.HEIGHT - HEIGHT - MIN_X);
    public static final int MAX_Y = MIN_Y + HEIGHT;
}
