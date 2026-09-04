package main.java.snake.core;

import main.java.snake.constant.ColourConstants;
import main.java.snake.constant.GameConstants;
import main.java.snake.entity.Fruit;
import main.java.snake.entity.Player;
import main.java.snake.math.Direction;
import main.java.snake.math.GridPos;
import main.java.snake.math.Vec2;
import main.java.snake.math.Vec2i;

import java.awt.*;
import java.util.Random;

import static main.java.snake.constant.LevelConstants.*;

public class Level {
    private static final Vec2 FRUIT_START = new Vec2(12.5, 7.5);
    private static final Vec2 PLAYER_START = new Vec2(3.5F, 7.5);

    private final Fruit fruit;
    private final Player player;
    private final Random random;
    private final OccupancyGrid grid;

    Level() {
        this.random = new Random();
        this.fruit = new Fruit(this, FRUIT_START,  0.8F);
        this.player = new Player(this, GameConstants.START_DIR, PLAYER_START);

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
                graphics.setColor((row % 2 == 0) != (col % 2 == 0) ? ColourConstants.GRID1 : ColourConstants.GRID2);

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

    public final Vec2i gridToScreen(Vec2 position) {
        return gridToScreen(position.x(), position.y());
    }

    public final Vec2i gridToScreen(double column, double row) {
        // Grid row is negative because drawn y-coordinates are flipped
        return new Vec2i(MIN_X + (int) (column * GRID_SCALE), MAX_Y + (int) (-row * GRID_SCALE));
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

    public final int getScore() {
        return Math.clamp(this.player.length() - 3, 0, (COLUMNS * ROWS) - 3);
    }

    public final Fruit getFruit() {
        return this.fruit;
    }

    public final Player getPlayer() {
        return this.player;
    }

    public final Random getRandom() {
        return this.random;
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
                this.free[i] = i;
                this.indexOf[i] = i;
            }
        }

        private Vec2i asGrid(int index) {
            this.checkIndex(index);
            return new Vec2i(index % COLUMNS, index / COLUMNS);
        }

        private int asIndex(Vec2i pos) {
            this.checkPos(pos.getX(), pos.getY());
            return pos.getY() * COLUMNS + pos.getX();
        }

        private void checkIndex(int pos) {
            if (pos < 0 || pos >= this.free.length) throw new IndexOutOfBoundsException("pos: " + pos);
        }

        private void checkPos(int x, int y) {
            if (x < 0 || x >= COLUMNS || y < 0 || y >= ROWS)
                throw new IndexOutOfBoundsException("x,y: " + x + "," + y);
        }

        public Vec2i pickRandomFree() {
            if (this.freeCount == 0) throw new IllegalStateException("no free positions");
            int random = this.random.nextInt(this.freeCount);
            int index = this.free[random];

            this.occupyPosition(index);
            return this.asGrid(index);
        }

        void occupyPosition(Vec2i pos) {
            this.checkPos(pos.getX(), pos.getY());
            this.occupyPosition(this.asIndex(pos));
        }

        void occupyPosition(int pos) {
            this.checkIndex(pos);
            int index = this.indexOf[pos];
            if (this.isOccupied(pos)) return;

            int last = this.free[this.freeCount - 1];
            this.free[index] = last;
            this.indexOf[last] = index;
            this.freeCount--;
            this.indexOf[pos] = -1;
        }

        void freePosition(Vec2i pos) {
            this.checkPos(pos.getX(), pos.getY());
            this.freePosition(this.asIndex(pos));
        }

        void freePosition(int index) {
            this.checkIndex(index);
            if (!this.isOccupied(index)) return;
            this.free[this.freeCount] = index;
            this.indexOf[index] = this.freeCount;
            this.freeCount++;
        }

        public boolean isOccupied(Vec2i pos) {
            this.checkPos(pos.getX(), pos.getY());
            return this.isOccupied(this.asIndex(pos));
        }

        public boolean isOccupied(int index) {
            this.checkIndex(index);
            return this.indexOf[index] == -1;
        }

        public int freeCount() {
            return this.freeCount;
        }

        public int capacity() {
            return this.free.length;
        }

        public void reset() {
            for (int index = 0; index < this.free.length; index++) {
                this.free[index] = index;
                this.indexOf[index] = index;
            }
            this.freeCount = this.free.length;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            for (int y = ROWS - 1; y >= 0; y--) {
                for (int x = 0; x < COLUMNS; x++) {
                    int pos = y * COLUMNS + x;
                    sb.append(isOccupied(pos) ? '1' : '0');
                    if (x < COLUMNS - 1) sb.append(' ');
                }
                if (y > 0) sb.append('\n');
            }
            return sb.toString();
        }
    }
}
