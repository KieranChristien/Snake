package main.java.snake.math;

public class GridPos extends Vec2i {
    public static final GridPos ZERO;
    public static final int PACKED_HORIZONTAL_LENGTH;
    public static final int PACKED_Y_LENGTH;
    private static final long PACKED_X_MASK;
    private static final long PACKED_Y_MASK;
    private static final int Y_OFFSET = 0;
    private static final int X_OFFSET;
    public static final int MAX_HORIZONTAL_COORDINATE;

    public GridPos(int x, int y) {
        super(x, y);
    }

    public GridPos(Vec2i pos) {
        this(pos.getX(), pos.getY());
    }

    public static long offset(final long gridNode, final Direction offset) {
        return offset(gridNode, offset.getStepX(), offset.getStepY());
    }

    public static long offset(final long gridNode, final int stepX, final int stepY) {
        return asLong(getX(gridNode) + stepX, getY(gridNode) + stepY);
    }

    public static int getX(final long gridNode) {
        return (int) (gridNode << (64 - X_OFFSET - PACKED_HORIZONTAL_LENGTH) >> (64 - PACKED_HORIZONTAL_LENGTH));
    }

    public static int getY(final long gridNode) {
        return (int) (gridNode << (64 - PACKED_Y_LENGTH) >> (64 - PACKED_Y_LENGTH));
    }

    public static GridPos of(final long gridNode) {
        return new GridPos(getX(gridNode), getY(gridNode));
    }

    public static GridPos containing(final double x, final double y) {
        return new GridPos((int) java.lang.Math.floor(x), (int) java.lang.Math.floor(y));
    }

    public static GridPos containing(final Position pos) {
        return containing(pos.x(), pos.y());
    }

    public static GridPos min(final GridPos a, final GridPos b) {
        return new GridPos(java.lang.Math.min(a.getX(), b.getX()), java.lang.Math.min(a.getY(), b.getY()));
    }

    public static GridPos max(final GridPos a, final GridPos b) {
        return new GridPos(java.lang.Math.max(a.getX(), b.getX()), java.lang.Math.max(a.getY(), b.getY()));
    }

    public long asLong() {
        return asLong(this.getX(), this.getY());
    }

    public static long asLong(final int x, final int y) {
        long node = 0L;
        node |= ((long) x & PACKED_X_MASK) << X_OFFSET;
        node |= ((long) y & PACKED_Y_MASK);
        return node;
    }

    public static long getFlatIndex(final long neighborBlockNode) {
        return neighborBlockNode & -16L;
    }

    public GridPos offset(final int x, final int y) {
        return x == 0 && y == 0 ? this : new GridPos(this.getX() + x, this.getY() + y);
    }

    public GridPos offset(final Vec2i vec) {
        return this.offset(vec.getX(), vec.getY());
    }

    public GridPos subtract(final Vec2i vec) {
        return this.offset(-vec.getX(), -vec.getY());
    }

    public GridPos multiply(final int scale) {
        if (scale == 1) {
            return this;
        } else {
            return scale == 0 ? ZERO : new GridPos(this.getX() * scale, this.getY() * scale);
        }
    }

    public GridPos up() {
        return this.relative(Direction.UP);
    }

    public GridPos up(final int steps) {
        return this.relative(Direction.UP, steps);
    }

    public GridPos down() {
        return this.relative(Direction.DOWN);
    }

    public GridPos down(final int steps) {
        return this.relative(Direction.DOWN, steps);
    }

    public GridPos left() {
        return this.relative(Direction.LEFT);
    }

    public GridPos left(final int steps) {
        return this.relative(Direction.LEFT, steps);
    }

    public GridPos right() {
        return this.relative(Direction.RIGHT);
    }

    public GridPos right(final int steps) {
        return this.relative(Direction.RIGHT, steps);
    }

    public GridPos relative(final Direction direction) {
        return new GridPos(this.getX() + direction.getStepX(), this.getY() + direction.getStepY());
    }

    public GridPos relative(final Direction direction, final int steps) {
        return steps == 0 ? this : new GridPos(this.getX() + direction.getStepX() * steps, this.getY() + direction.getStepY() * steps);
    }

    public GridPos atY(final int y) {
        return new GridPos(this.getX(), y);
    }

    public GridPos immutable() {
        return this;
    }

    public MutableGridPos mutable() {
        return new MutableGridPos(this.getX(), this.getY());
    }

    public Vec2 clampLocationWithin(final Vec2 location) {
        return new Vec2(Math.clamp(location.x(), (float) this.getX() + 1.0E-5F, (double) this.getX() + (double) 1.0F - (double) 1.0E-5F), Math.clamp(location.y(), (float) this.getY() + 1.0E-5F, (double) this.getY() + (double) 1.0F - (double) 1.0E-5F));
    }

    static {
        ZERO = new GridPos(0, 0);
        PACKED_HORIZONTAL_LENGTH = 1 + GridMath.log2(GridMath.smallestEncompassingPowerOfTwo(17));
        PACKED_Y_LENGTH = 64 - 2 * PACKED_HORIZONTAL_LENGTH;
        PACKED_X_MASK = (1L << PACKED_HORIZONTAL_LENGTH) - 1L;
        PACKED_Y_MASK = (1L << PACKED_Y_LENGTH) - 1L;
        X_OFFSET = PACKED_Y_LENGTH + PACKED_HORIZONTAL_LENGTH;
        MAX_HORIZONTAL_COORDINATE = (1 << PACKED_HORIZONTAL_LENGTH) / 2 - 1;
    }

    public static class GridMath {
        private static final int[] MULTIPLY_DE_BRUIJN_BIT_POSITION = new int[]{0, 1, 28, 2, 29, 14, 24, 3, 30, 22, 20, 15, 25, 17, 4, 8, 31, 27, 13, 23, 21, 19, 16, 7, 26, 12, 18, 6, 11, 5, 10, 9};

        public static boolean isPowerOfTwo(final int input) {
            return input != 0 && (input & (input - 1)) == 0;
        }

        public static int ceillog2(int input) {
            input = isPowerOfTwo(input) ? input : smallestEncompassingPowerOfTwo(input);
            return MULTIPLY_DE_BRUIJN_BIT_POSITION[(int)(((long)input * 125613361L) >> 27) & 31];
        }

        public static int log2(final int input) {
            return ceillog2(input) - (isPowerOfTwo(input) ? 0 : 1);
        }

        public static int smallestEncompassingPowerOfTwo(final int input) {
            int result = input - 1;
            result |= result >> 1;
            result |= result >> 2;
            result |= result >> 4;
            result |= result >> 8;
            result |= result >> 16;
            return result + 1;
        }
    }

    public static class MutableGridPos extends GridPos {
        public MutableGridPos() {
            this(0, 0);
        }

        public MutableGridPos(final int x, final int y) {
            super(x, y);
        }

        public MutableGridPos(final double x, final double y) {
            this((int) Math.floor(x), (int) Math.floor(y));
        }

        public GridPos offset(final int x, final int y) {
            return super.offset(x, y).immutable();
        }

        public GridPos multiply(final int scale) {
            return super.multiply(scale).immutable();
        }

        public GridPos relative(final Direction direction, final int steps) {
            return super.relative(direction, steps).immutable();
        }

        public MutableGridPos set(final int x, final int y) {
            this.setX(x);
            this.setY(y);
            return this;
        }

        public MutableGridPos set(final double x, final double y) {
            return this.set((int) java.lang.Math.floor(x), (int) java.lang.Math.floor(y));
        }

        public MutableGridPos set(final Vec2i vec) {
            return this.set(vec.getX(), vec.getY());
        }

        public MutableGridPos set(final long pos) {
            return this.set(getX(pos), getY(pos));
        }

        public MutableGridPos setWithOffset(final Vec2i pos, final Direction direction) {
            return this.set(pos.getX() + direction.getStepX(), pos.getY() + direction.getStepY());
        }

        public MutableGridPos setWithOffset(final Vec2i pos, final int x, final int y, final int z) {
            return this.set(pos.getX() + x, pos.getY() + y);
        }

        public MutableGridPos setWithOffset(final Vec2i pos, final Vec2i offset) {
            return this.set(pos.getX() + offset.getX(), pos.getY() + offset.getY());
        }

        public MutableGridPos move(final Direction direction) {
            return this.move(direction, 1);
        }

        public MutableGridPos move(final Direction direction, final int steps) {
            return this.set(this.getX() + direction.getStepX() * steps, this.getY() + direction.getStepY() * steps);
        }

        public MutableGridPos move(final int x, final int y, final int z) {
            return this.set(this.getX() + x, this.getY() + y);
        }

        public MutableGridPos move(final Vec2i pos) {
            return this.set(this.getX() + pos.getX(), this.getY() + pos.getY());
        }

        public MutableGridPos setX(final int x) {
            super.setX(x);
            return this;
        }

        public MutableGridPos setY(final int y) {
            super.setY(y);
            return this;
        }

        public GridPos immutable() {
            return new GridPos(this);
        }
    }
}
