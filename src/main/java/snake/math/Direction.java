package main.java.snake.math;

import java.util.stream.Stream;

public enum Direction {
    DOWN(1, "down", new Vec2i(0, -1)),
    UP(0, "up", new Vec2i(0, 1)),
    LEFT(3, "left", new Vec2i(-1, 0)),
    RIGHT(2, "right", new Vec2i(1, 0));

    private final int oppositeIndex;
    private final String name;
    private final Vec2i normal;
    private final Vec2 normalVec2;
    private static final Direction[] VALUES = values();

    Direction(final int oppositeIndex, final String name, final Vec2i normal) {
        this.oppositeIndex = oppositeIndex;
        this.name = name;
        this.normal = normal;
        this.normalVec2 = new Vec2(normal.getX(), normal.getY());
    }

    public static Stream<Direction> stream() {
        return Stream.of(VALUES);
    }

    public Direction getOpposite() {
        return Direction.values()[this.oppositeIndex];
    }

    public Direction getClockWise() {
        return switch (this) {
            case DOWN -> LEFT;
            case UP -> RIGHT;
            case LEFT -> UP;
            case RIGHT -> DOWN;
        };
    }

    public Direction getCounterClockWise() {
        return switch (this) {
            case DOWN -> RIGHT;
            case UP -> LEFT;
            case LEFT -> DOWN;
            case RIGHT -> UP;
        };
    }

    public int getStepX() {
        return this.normal.getX();
    }

    public int getStepY() {
        return this.normal.getY();
    }

    public Vec2i getUnitVec2i() {
        return this.normal;
    }

    public Vec2 getUnitVec2() {
        return this.normalVec2;
    }

    public String getName() {
        return this.name;
    }

    public boolean isHorizontal() {
        return this == LEFT || this == RIGHT;
    }

    public boolean isVertical() {
        return this == DOWN || this == UP;
    }

    public static Direction getApproximateNearest(final double dx, final double dy) {
        return getApproximateNearest((float)dx, (float)dy);
    }

    public static Direction getApproximateNearest(final float dx, final float dy) {
        Direction result = UP;
        float highestDot = Float.MIN_VALUE;

        for(Direction direction : VALUES) {
            float dot = dx * (float)direction.normal.getX() + dy * (float)direction.normal.getY();
            if (dot > highestDot) {
                highestDot = dot;
                result = direction;
            }
        }

        return result;
    }

    public static Direction getApproximateNearest(final Vec2i vec) {
        return getApproximateNearest(vec.getX(), vec.getY());
    }
}
