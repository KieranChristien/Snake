package main.java.snake.math;

public class Vec2 implements Position {
    public static final Vec2 ZERO;
    public static final Vec2 X_AXIS;
    public static final Vec2 Y_AXIS;
    public final double x;
    public final double y;

    public static Vec2 atCenterOf(final Vec2i pos) {
        return new Vec2((double) pos.getX() + 0.5F, (double) pos.getY() + 0.5F);
    }

    public Vec2(final double x, final double y) {
        this.x = x;
        this.y = y;
    }

    public Vec2(final Vec2i vec) {
        this(vec.getX(), vec.getY());
    }

    public Vec2 vectorTo(final Vec2 vec) {
        return new Vec2(vec.x - this.x, vec.y - this.y);
    }

    public Vec2 normalize() {
        double dist = Math.sqrt(this.x * this.x + this.y * this.y);
        return dist < Mth.EPSILON ? ZERO : new Vec2(this.x / dist, this.y / dist);
    }

    public double dot(final Vec2 vec) {
        return this.x * vec.x + this.y * vec.y;
    }

    public Vec2 subtract(final Vec2 vec) {
        return this.subtract(vec.x, vec.y);
    }

    public Vec2 subtract(final double value) {
        return this.subtract(value, value);
    }

    public Vec2 subtract(final double x, final double y) {
        return this.add(-x, -y);
    }

    public Vec2 add(final double value) {
        return this.add(value, value);
    }

    public Vec2 add(final Vec2 vec) {
        return this.add(vec.x, vec.y);
    }

    public Vec2 add(final double x, final double y) {
        return new Vec2(this.x + x, this.y + y);
    }

    public boolean closerThan(final Position pos, final double distance) {
        return this.distanceToSqr(pos.x(), pos.y()) < distance * distance;
    }

    public double distanceTo(final Vec2 vec) {
        double xd = vec.x - this.x;
        double yd = vec.y - this.y;
        return Math.sqrt(xd * xd + yd * yd);
    }

    public double distanceToSqr(final Vec2 vec) {
        double xd = vec.x - this.x;
        double yd = vec.y - this.y;
        return xd * xd + yd * yd;
    }

    public double distanceToSqr(final double x, final double y) {
        double xd = x - this.x;
        double yd = y - this.y;
        return xd * xd + yd * yd;
    }

    public Vec2 scale(final double scale) {
        return this.multiply(scale, scale);
    }

    public Vec2 reverse() {
        return this.scale(-1.0F);
    }

    public Vec2 multiply(final Vec2 scale) {
        return this.multiply(scale.x, scale.y);
    }

    public Vec2 multiply(final double xScale, final double yScale) {
        return new Vec2(this.x * xScale, this.y * yScale);
    }

    public Vec2 up() {
        return this.up(1);
    }

    public Vec2 up(final double steps) {
        return this.relative(Direction.UP, steps);
    }

    public Vec2 down() {
        return this.down(1);
    }

    public Vec2 down(final double steps) {
        return this.relative(Direction.DOWN, steps);
    }

    public Vec2 left() {
        return this.left(1);
    }

    public Vec2 left(final double steps) {
        return this.relative(Direction.LEFT, steps);
    }

    public Vec2 right() {
        return this.right(1);
    }

    public Vec2 right(final double steps) {
        return this.relative(Direction.RIGHT, steps);
    }

    public Vec2 relative(final Direction direction) {
        return this.relative(direction, 1);
    }

    public Vec2 relative(final Direction direction, final double steps) {
        return steps == 0 ? this : new Vec2(this.x() + direction.getStepX() * steps, this.y() + direction.getStepY() * steps);
    }

    public Vec2 horizontal() {
        return new Vec2(this.x, 0.0F);
    }

    public Vec2 vertical() {
        return new Vec2(0.0F, this.y);
    }

    public double length() {
        return Math.sqrt(this.x * this.x + this.y * this.y);
    }

    public double lengthSqr() {
        return this.x * this.x + this.y * this.y;
    }

    public double horizontalDistance() {
        return Math.sqrt(this.x * this.x);
    }

    public double horizontalDistanceSqr() {
        return this.x * this.x;
    }

    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        } else if (o instanceof Vec2 vec3) {
            if (Double.compare(vec3.x, this.x) != 0) {
                return false;
            } else {
                return (Double.compare(vec3.y, this.y) == 0);
            }
        } else {
            return false;
        }
    }

    public int hashCode() {
        return 31 * Double.hashCode(this.x) + Double.hashCode(this.y);
    }

    public String toString() {
        return "(" + this.x + ", " + this.y + ")";
    }

    public Vec2 lerp(final Vec2 vec, final double a) {
        double x = Mth.lerp(a, this.x, vec.x);
        double y = Mth.lerp(a, this.y, vec.y);
        return new Vec2(x, y);
    }

    public final double x() {
        return this.x;
    }

    public final double y() {
        return this.y;
    }

    public Vec2 projectedOn(final Vec2 onto) {
        return onto.lengthSqr() == (double) 0.0F ? onto : onto.scale(this.dot(onto)).scale((double) 1.0F / onto.lengthSqr());
    }

    public boolean isFinite() {
        return Double.isFinite(this.x) && Double.isFinite(this.y);
    }

    static {
        ZERO = new Vec2(0.0F, 0.0F);
        X_AXIS = new Vec2(1.0F, 0.0F);
        Y_AXIS = new Vec2(0.0F, 1.0F);
    }
}
