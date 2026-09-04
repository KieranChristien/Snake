package main.java.snake.math;

public class Vec2i implements Comparable<Vec2i> {
    public static final Vec2i ZERO;
    private int x;
    private int y;

    public Vec2i(final int x, final int y) {
        this.x = x;
        this.y = y;
    }

    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        } else if (!(o instanceof Vec2i vec2i)) {
            return false;
        } else {
            return this.getX() == vec2i.getX() && this.getY() == vec2i.getY();
        }
    }

    public int hashCode() {
        return this.getY() * 31 + this.getX();
    }

    @Override
    public int compareTo(final Vec2i pos) {
        if (this.getY() == pos.getY()) {
            return this.getX() - pos.getX();
        } else {
            return this.getY() - pos.getY();
        }
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    protected Vec2i setX(final int x) {
        this.x = x;
        return this;
    }

    protected Vec2i setY(final int y) {
        this.y = y;
        return this;
    }

    public Vec2i offset(final int x, final int y) {
        return x == 0 && y == 0 ? this : new Vec2i(this.getX() + x, this.getY() + y);
    }

    public Vec2i offset(final Vec2i vec) {
        return this.offset(vec.getX(), vec.getY());
    }

    public Vec2i subtract(final Vec2i vec) {
        return this.offset(-vec.getX(), -vec.getY());
    }

    public Vec2i multiply(final int scale) {
        if (scale == 1) {
            return this;
        } else {
            return scale == 0 ? ZERO : new Vec2i(this.getX() * scale, this.getY() * scale);
        }
    }

    public Vec2i multiply(final int xScale, final int yScale) {
        if (xScale == 1 && yScale == 1) return this;
        else return new Vec2i(this.getX() * xScale, this.getY() * yScale);
    }

    public Vec2i up() {
        return this.up(1);
    }

    public Vec2i up(final int steps) {
        return this.relative(Direction.UP, steps);
    }

    public Vec2i down() {
        return this.down(1);
    }

    public Vec2i down(final int steps) {
        return this.relative(Direction.DOWN, steps);
    }

    public Vec2i left() {
        return this.left(1);
    }

    public Vec2i left(final int steps) {
        return this.relative(Direction.LEFT, steps);
    }

    public Vec2i right() {
        return this.right(1);
    }

    public Vec2i right(final int steps) {
        return this.relative(Direction.RIGHT, steps);
    }

    public Vec2i relative(final Direction direction) {
        return this.relative(direction, 1);
    }

    public Vec2i relative(final Direction direction, final int steps) {
        return steps == 0 ? this : new Vec2i(this.getX() + direction.getStepX() * steps, this.getY() + direction.getStepY() * steps);
    }

    public String toString() {
        return this.getX() + ", " + this.getY();
    }

    static {
        ZERO = new Vec2i(0, 0);
    }
}
