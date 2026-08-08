package main.java.snake;

import main.java.snake.constant.ColourConstants;
import main.java.snake.constant.GameConstants;
import main.java.snake.entity.Fruit;
import main.java.snake.entity.Player;
import main.java.snake.util.*;

import java.awt.*;
import java.util.Random;

public class Level {
    public static final int GRID_SCALE = 38;
    public static final int HALF_GRID_SCALE = GRID_SCALE / 2;
    private static final int COLUMNS = 17;
    private static final int ROWS = 15;

    private static final int WIDTH = GRID_SCALE * COLUMNS;
    private static final int HEIGHT = GRID_SCALE * ROWS;
    private static final int MIN_X = (GameConstants.WINDOW_WIDTH - WIDTH) / 2;
    private static final int MAX_X = MIN_X + WIDTH;
    private static final int MIN_Y = (GameConstants.WINDOW_HEIGHT - HEIGHT - MIN_X);
    private static final int MAX_Y = MIN_Y + HEIGHT;

    private static final Vec2 FRUIT_START = new Vec2(12.5, 7.5);
    private static final Vec2 PLAYER_START = new Vec2(3.5F, 7.5);

    private final Fruit fruit;
    private final Player player;
    private final Random random;
    private final OccupancyGrid grid;

    Level() {
        this.fruit = new Fruit(this, FRUIT_START,  0.8F);
        this.player = new Player(this, GameConstants.START_DIR, PLAYER_START);
        this.random = new Random();

        this.grid = new OccupancyGrid(COLUMNS * ROWS, this.random);
    }

    public final void tick(Direction input) {
        // Change player direction when aligned to grid
        if (this.player.isAligned()) {
            this.player.setFacing(input);
        }

        this.player.move();

        if (this.player.isAligned()) {
            // Update Occupancy
            this.grid.reset();
            for (GridPos pos : player.positions()) {
                this.grid.occupyPosition(pos);
            }

            // Fruit Collision
            if (this.player.getHead().hitbox().intersects(this.fruit.hitbox())) {
                this.fruit.setPos(Vec2.atCenterOf(this.grid.pickRandomFree()));

                this.player.addSegment(true);
            }
        }
    }

    public final void draw(Graphics graphics, Graphics2D graphics2D) {
        // Game Level Grid
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLUMNS; col++) {
                graphics.setColor((row % 2 == 0) != (col % 2 == 0) ? ColourConstants.GRID_COLOUR1 : ColourConstants.GRID_COLOUR2);

                graphics.fillRect(
                        MIN_X + (col * GRID_SCALE),
                        MIN_Y + (row * GRID_SCALE),
                        GRID_SCALE,
                        GRID_SCALE
                );
            }
        }

        this.fruit.draw(graphics2D);
        this.player.draw(graphics2D);
    }

    public final void reset() {
        this.fruit.setPos(FRUIT_START);
        this.grid.reset();
        this.player.reset(GameConstants.START_DIR, PLAYER_START);
    }

    public final Vec2i gridToActual(Vec2 position) {
        return gridToActual(position.x(), position.y());
    }

    public final Vec2i gridToActual(double column, double row) {
        // Grid row is negative because drawn y-coordinates are flipped
        if (column >= COLUMNS || row >= ROWS || column < 0 || row < 0) throw new IllegalArgumentException("Position out of bounds: (" + column + ", " + row + ")");
        return new Vec2i(MIN_X + (int) (column * GRID_SCALE), MAX_Y + (int) (-row * GRID_SCALE));
    }

    public final Vec2 actualToGrid(Vec2i position) {
        return actualToGrid(position.getX(), position.getY());
    }

    public final Vec2 actualToGrid(int x, int y) {
        int column = (x - MIN_X) / GRID_SCALE;
        int row = (y - MIN_Y) / GRID_SCALE;
        if (column >= COLUMNS || row >= ROWS || column < 0 || row < 0) throw new IllegalArgumentException("Position out of bounds");
        return new Vec2(x, y);
    }

    public final boolean didPlayerHitWall() {
        return this.player.getFacePosition().x() <= 0
                || this.player.getFacePosition().x() >= COLUMNS
                || this.player.getFacePosition().y() <= 0
                || this.player.getFacePosition().y() >= ROWS;
    }

    public final boolean didPlayerHitSelf() {
        return this.player.hitSelf();
    }

    public final Player getPlayer() {
        return this.player;
    }

    public final int getGridScale() {
        return GRID_SCALE;
    }

    public final int getHalfGridScale() {
        return HALF_GRID_SCALE;
    }

    static class OccupancyGrid {
        private final int[] free;
        private final int[] indexOf;
        private int freeCount;
        private final Random random;

        public OccupancyGrid(int size, Random random) {
            this.free = new int[size];
            this.indexOf = new int[size];
            this.freeCount = size;
            this.random = random;

            for (int i = 0; i < size; i++) {
                free[i] = i;
                indexOf[i] = i;
            }
        }

        private Vec2i toPosition(int pos) {
            this.checkPos(pos);
            return new Vec2i(pos % COLUMNS, pos / COLUMNS);
        }

        private int toPos(Vec2i pos) {
            this.checkXY(pos.getX(), pos.getY());
            return pos.getY() * COLUMNS + pos.getX();
        }

        private void checkPos(int pos) {
            if (pos < 0 || pos >= free.length) throw new IndexOutOfBoundsException("pos: " + pos);
        }

        private void checkXY(int x, int y) {
            if (x < 0 || x >= COLUMNS || y < 0 || y >= ROWS)
                throw new IndexOutOfBoundsException("x,y: " + x + "," + y);
        }

        public Vec2i pickRandomFree() {
            if (this.freeCount == 0) throw new IllegalStateException("no free positions");
            int r = this.random.nextInt(this.freeCount);
            int pos = this.free[r];

            this.occupyPosition(pos);
            return this.toPosition(pos);
        }

        void occupyPosition(Vec2i pos) {
            this.checkXY(pos.getX(), pos.getY());
            this.occupyPosition(this.toPos(pos));
        }

        void occupyPosition(int pos) {
            this.checkPos(pos);
            int idx = this.indexOf[pos];
            if (this.isOccupied(pos)) return;

            int last = this.free[this.freeCount - 1];
            this.free[idx] = last;
            this.indexOf[last] = idx;
            this.freeCount--;
            this.indexOf[pos] = -1;
        }

        void freePosition(Vec2i pos) {
            this.checkXY(pos.getX(), pos.getY());
            this.freePosition(this.toPos(pos));
        }

        void freePosition(int pos) {
            this.checkPos(pos);
            if (!this.isOccupied(pos)) return;
            free[freeCount] = pos;
            indexOf[pos] = freeCount;
            freeCount++;
        }

        public boolean isOccupied(Vec2i pos) {
            this.checkXY(pos.getX(), pos.getY());
            return this.isOccupied(this.toPos(pos));
        }

        public boolean isOccupied(int pos) {
            this.checkPos(pos);
            return indexOf[pos] == -1;
        }

        public int freeCount() {
            return freeCount;
        }

        public int capacity() {
            return free.length;
        }

        public void reset() {
            for (int i = 0; i < free.length; i++) {
                free[i] = i;
                indexOf[i] = i;
            }
            freeCount = free.length;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            for (int y = 0; y < ROWS; y++) {
                for (int x = 0; x < COLUMNS; x++) {
                    int pos = y * COLUMNS + x;
                    sb.append(isOccupied(pos) ? '1' : '0');
                    if (x < COLUMNS - 1) sb.append(' ');
                }
                if (y < ROWS - 1) sb.append('\n');
            }
            return sb.toString();
        }
    }
}
