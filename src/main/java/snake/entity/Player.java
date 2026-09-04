package main.java.snake.entity;

import main.java.snake.constant.ColourConstants;
import main.java.snake.constant.GameConstants;
import main.java.snake.core.Level;
import main.java.snake.constant.LevelConstants;
import main.java.snake.math.*;
import main.java.snake.rendering.sprite.ImageUtils;
import main.java.snake.rendering.sprite.AnimatedSprite;
import main.java.snake.rendering.sprite.Sprite;

import java.awt.*;
import java.util.*;

public class Player {
    private static final float SEGMENT_SIZE = 0.75F;
    private static final int SEGMENT_VISUAL_SIZE = (int) (SEGMENT_SIZE * LevelConstants.GRID_SCALE);
    private static final int MAX_LENGTH = 255;
    private final Level level;
    private final int segmentOffset;
    private final ArrayList<Segment> segments = new ArrayList<>();
    private boolean isAlive = true;

    private static final int BLINK_MIN_SEC = 1;
    private static final int BLINK_MAX_SEC = 3;
    private static final int TONGUE_MIN_SEC = 10;
    private static final int TONGUE_MAX_SEC = 15;
    private final AnimatedSprite eyeSprite;
    private final AnimatedSprite deathSprite;
    private final AnimatedSprite mouthSprite;
    private final AnimatedSprite tongueSprite;
    private long blinkTime;
    private long tongueTime;

    public Player(Level level, Direction facing, Vec2 position) {
        if (Math.abs(facing.isHorizontal() ? position.y() : position.x() % 1) != 0.5) {
            throw new IllegalArgumentException("Invalid starting position " + position + " for direction " + facing);
        }

        this.level = level;
        this.addHead(facing, position);
        this.addSegments(2);

        this.segmentOffset = (level.getGridScale() - (int) (SEGMENT_SIZE * level.getGridScale())) / 2;

        this.eyeSprite = new AnimatedSprite(
                ImageUtils.fromPath("snake/blink.png"),
                24,
                AnimatedSprite.LoopType.PLAY_ONCE
        );
        this.eyeSprite.setScaleMethod(Sprite.ScaleMethod.BICUBIC);
        this.eyeSprite.setScale(0.4);
        this.blinkTime = this.getNextRandomTime(level, BLINK_MIN_SEC, BLINK_MAX_SEC);

        this.deathSprite = new AnimatedSprite(
                ImageUtils.fromPath("snake/die.png"),
                37,
                24,
                AnimatedSprite.LoopType.HOLD_ON_LAST_FRAME
        );
        this.deathSprite.setScale(0.5);

        this.mouthSprite = new AnimatedSprite(
                ImageUtils.fromPath("snake/eat.png"),
                15,
                -1,
                AnimatedSprite.LoopType.PLAY_ONCE
        );

        this.tongueSprite = new AnimatedSprite(
                ImageUtils.fromPath("snake/tongue.png"),
                21,
                21,
                AnimatedSprite.LoopType.PLAY_ONCE
        );
        this.eyeSprite.setScaleMethod(Sprite.ScaleMethod.BICUBIC);
        this.tongueTime = this.getNextRandomTime(level, TONGUE_MIN_SEC, TONGUE_MAX_SEC);
    }

    public final void reset(Direction facing, Vec2 position) {
        this.segments.clear();
        this.addHead(facing, position);
        this.addSegments(2);
        this.isAlive = true;
    }

    public final Long getNextRandomTime(Level level, int origin, int bound) {
        return Time.now() + Time.secondsToNanos(level.getRandom().nextInt(origin, bound));
    }

    public final void draw(Graphics2D graphics) {
        // Enable antialiasing for shapes
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        ListIterator<Segment> reversed = this.segments.reversed().listIterator();
        Segment next = reversed.hasNext() ? reversed.next() : null;
        Vec2i connectFrom = next != null ? this.level.gridToScreen(next.position()) : null;

        Vec2 tonguePos = this.getHead().position().relative(this.getFacing(), 0.8);
        Vec2i tongueVisualPos = this.level.gridToScreen(tonguePos);

        // Rotation of sprite based on facing direction
        double spriteRotation = switch (this.getFacing()) {
            case DOWN -> 90.0D;
            case UP -> -90.0D;
            case LEFT -> 180.0D;
            default -> 0.0D;
        };

        if (this.isAlive) {
            this.tongueSprite.setRotationDegrees(spriteRotation);

            if (!this.tongueSprite.isStarted() && Time.now() >= this.tongueTime) {
                this.tongueSprite.start();

                this.tongueTime = this.getNextRandomTime(this.level, TONGUE_MIN_SEC, TONGUE_MAX_SEC);
            }
            if (this.tongueSprite.isStarted()) {
                this.tongueSprite.draw(graphics, tongueVisualPos.getX(), tongueVisualPos.getY());
            }
        }

        graphics.setColor(ColourConstants.SNAKE);

        while (next != null) {
            Segment current = next;
            next = reversed.hasNext() ? reversed.next() : null;

            Vec2i visualPos = current.level().gridToScreen(current.position());
            boolean isEnd = current.equals(this.getHead()) || current.equals(this.segments.getLast());

            if (isEnd) graphics.fillOval(
                    visualPos.getX() - (this.level.getHalfGridScale()) + this.segmentOffset,
                    visualPos.getY() - (this.level.getHalfGridScale()) + this.segmentOffset,
                    SEGMENT_VISUAL_SIZE,
                    SEGMENT_VISUAL_SIZE
            );

            Vec2i connectTo = null;
            if (next != null) {
                if (current.facing() != next.facing()) {
                    Vec2 cornerPos = current.facing().isHorizontal() && next.facing().isVertical() ? new Vec2(next.position().x(), current.position().y()) : new Vec2(current.position().x(), next.position().y());
                    Vec2i cornerVisual = this.level.gridToScreen(cornerPos);

                    graphics.fillOval(
                            cornerVisual.getX() - (this.level.getHalfGridScale()) + this.segmentOffset,
                            cornerVisual.getY() - (this.level.getHalfGridScale()) + this.segmentOffset,
                            SEGMENT_VISUAL_SIZE,
                            SEGMENT_VISUAL_SIZE
                    );

                    connectTo = cornerVisual;
                }
            } else {
                connectTo = visualPos;
            }

            if (connectTo != null) {
                Vec2i to = connectTo.subtract(connectFrom);
                Direction dirTo = Direction.getApproximateNearest(to);
                Direction offset = switch (dirTo) {
                    case UP, DOWN -> Direction.LEFT;
                    case LEFT, RIGHT -> Direction.DOWN;
                };

                boolean isNegative = dirTo == Direction.DOWN || dirTo == Direction.LEFT;
                Vec2i connectPos = (isNegative ? connectTo : connectFrom).relative(offset, SEGMENT_VISUAL_SIZE / 2);

                int width = dirTo.isHorizontal() ? Math.abs(to.getX()) : SEGMENT_VISUAL_SIZE;
                int height = dirTo.isVertical() ? Math.abs(to.getY()) : SEGMENT_VISUAL_SIZE;

                graphics.fillRect(
                        connectPos.getX(),
                        connectPos.getY(),
                        width,
                        height
                );

                connectFrom = connectTo;
            }
        }

        Vec2 mouthPos = this.getHead().position()
                .relative(this.getFacing(), 0.25);
        Vec2i mouthVisualPos = this.level.gridToScreen(mouthPos);

        // Start opening when close to fruit
        // Pause at 0.5 until fruit is far
        // Then continue animation
        double distanceToFruit = this.level().getFruit().position().distanceTo(this.getFacePosition());
        if (distanceToFruit <= 2.5) {
            if (this.mouthSprite.getProgress() < 0.5)
                this.mouthSprite.setProgress(this.mouthSprite.getProgress() + 0.02);
        } else if (this.mouthSprite.getProgress() > 0 && this.mouthSprite.getProgress() < 1) {
            this.mouthSprite.setProgress(this.mouthSprite.getProgress() + 0.02);
            if (this.mouthSprite.getProgress() == 1) this.mouthSprite.setProgress(0);
        }

        if (this.isAlive) {
            this.mouthSprite.setRotationDegrees(spriteRotation);
            this.mouthSprite.draw(graphics, mouthVisualPos.getX(), mouthVisualPos.getY());

            float eyeSpacing = 0.3F;
            float eyeOffset = 0.4F;
            Vec2 leftEyePos = this.getHead().position()
                    .relative(this.getFacing().getCounterClockWise(), eyeSpacing)
                    .relative(this.getFacing().getOpposite(), eyeOffset);
            Vec2i leftEyeVisualPos = this.level.gridToScreen(leftEyePos);
            Vec2 rightEyePos = this.getHead().position()
                    .relative(this.getFacing().getClockWise(), eyeSpacing)
                    .relative(this.getFacing().getOpposite(), eyeOffset);
            Vec2i rightEyeVisualPos = this.level.gridToScreen(rightEyePos);

            // Look towards the fruit
            this.eyeSprite.setRotationDegrees(
                    this.getFacePosition().vectorTo(
                            this.level().getFruit().position()
                    ).normalize().toDegrees() - 90
            );

            this.eyeSprite.draw(graphics, leftEyeVisualPos.getX(), leftEyeVisualPos.getY());
            this.eyeSprite.draw(graphics, rightEyeVisualPos.getX(), rightEyeVisualPos.getY());
            if (!this.eyeSprite.isStarted() && Time.now() >= this.blinkTime) {
                this.eyeSprite.start();

                this.blinkTime = this.getNextRandomTime(this.level, BLINK_MIN_SEC, BLINK_MAX_SEC);
            }

            if (this.deathSprite.isStarted()) this.deathSprite.stop();
        } else {
            Vec2 deathPos = this.getHead().position()
                    .relative(this.getFacing(), -0.175);
            Vec2i deathVisualPos = this.level.gridToScreen(deathPos);

            this.deathSprite.setRotationDegrees(spriteRotation);

            this.deathSprite.startIfStopped();
            this.deathSprite.draw(graphics, deathVisualPos.getX(), deathVisualPos.getY());

        }

        // Display hitbox
        if (GameConstants.debugHitboxes) {
            graphics.setColor(Color.white);
            int grid = this.level().getGridScale();

            for (Segment segment : this.segments) {

                Hitbox hitbox = segment.hitbox();
                Vec2i hitboxCenter = segment.level().gridToScreen(hitbox.getCenter());
                int width = (int) (segment.getHitboxWidth() * grid);
                int height = (int) (segment.getHitboxHeight() * grid);
                graphics.drawRect(
                        hitboxCenter.getX() - (width / 2),
                        hitboxCenter.getY() - (height / 2),
                        width,
                        height
                );
            }
        }
    }

    private Vec2 getMoveDelta(Direction facing, double scale) {
        return facing.getUnitVec2().scale(scale);
    }

    public final void travel() {
        for (int i = this.segments.size() - 1; i >= 0; --i) {
            Segment current = this.segments.get(i);
            Vec2 delta = this.getMoveDelta(current.facing(), 0.05);

            // Move body: if not head, check gap to predecessor
            if (i > 0) {
                Segment next = this.segments.get(i - 1); // predecessor toward head
                Vec2 nextDiff = next.position().subtract(current.position());
                if ((Mth.round(Math.abs(nextDiff.x()), 2) + Mth.round(Math.abs(nextDiff.y()), 2) >= 1.0)) {
                    current.setPos(current.position().add(delta));
                }
            } else {
                // head always moves forward
                current.setPos(current.position().add(delta));
            }

            // Update facing and nextPosition when reached nextPosition
            Vec2 diff = current.position().subtract(current.nextPosition());
            if ((Mth.round(Math.abs(diff.x()), 2) + Mth.round(Math.abs(diff.y()), 2) == 0.0)) {
                if (i > 0) current.setFacing(this.segments.get(i - 1).facing());
                current.setNextPosition();
            }
        }
    }

    public final boolean isAligned() {
        return Mth.round(this.getX() % 1, 2) == 0.5 && Mth.round(this.getY() % 1, 2) == 0.5;
    }

    public final void setAlive(boolean alive) {
        this.isAlive = alive;
    }

    public final boolean isAlive() {
        return this.isAlive;
    }

    public final boolean hitSelf() {
        Player.Head head = this.getHead();
        Vec2i dimensions = switch (this.getFacing()) {
            case UP, DOWN -> new Vec2i(1, 0);
            case LEFT, RIGHT -> new Vec2i(0, 1);
        };

        Hitbox faceHitbox = Hitbox.ofSize(this.getFacePosition(), dimensions.getX(), dimensions.getY());

        for (Player.Segment segment : segments) {
            if (segment.equals(head)) continue;
            if (segment.hitbox().intersects(faceHitbox)) return true;
        }

        return false;
    }

    public final Direction getFacing() {
        return this.getHead().facing();
    }

    public final void setFacing(Direction facing) {
        if (facing == this.getHead().facing().getOpposite()) return;
        this.getHead().setFacing(facing);
    }

    public final Vec2 getFacePosition() {
        Hitbox headHitbox = this.getHead().hitbox();
        Vec2 center = headHitbox.getCenter();

        return switch (this.getFacing()) {
            case DOWN -> new Vec2(center.x(), headHitbox.minY);
            case UP -> new Vec2(center.x(), headHitbox.maxY);
            case LEFT -> new Vec2(headHitbox.minX, center.y());
            case RIGHT -> new Vec2(headHitbox.maxX, center.y());
        };
    }

    public final double getX() {
        return this.getHead().position().x();
    }

    public final double getY() {
        return this.getHead().position().y();
    }

    public final Head getHead() {
        return (Head) this.segments.getFirst();
    }

    public final void addHead(Direction facing, Vec2 position) {
        if (!this.segments.isEmpty()) throw new IllegalStateException("Cannot add head: already contains segments");

        this.segments.addFirst(new Head(this.level, facing, position));
    }

    public final void addSegment() {
        this.addSegments(1);
    }

    public final void addSegment(boolean noGap) {
        this.addSegments(1, noGap);
    }

    public final void addSegments(int amount) {
        this.addSegments(amount, false);
    }

    public void addSegments(int amount, boolean noGap) {
        for (int i = 0; i < amount; i++) {
            if (this.segments.isEmpty()) throw new IllegalStateException("Cannot add segments: head is missing");
            if (this.segments.size() >= MAX_LENGTH) return;

            Segment previous = this.segments.getLast();
            Direction prevFacing = previous.facing();

            this.segments.add(new Segment(
                    this.level,
                    previous.facing(),
                    noGap ? previous.position() : previous.position().relative(prevFacing.getOpposite())
            ));
        }
    }

    public final int length() {
        return this.segments.size();
    }

    public final Level level() {
        return this.level;
    }

    public Iterable<GridPos> positions() {
        return () -> new Iterator<>() {
            private int idx = 0;
            private final int len = length();

            @Override
            public boolean hasNext() {
                return idx < len;
            }

            @Override
            public GridPos next() {
                if (!hasNext()) throw new NoSuchElementException();
                return segments.get(idx++).gridPosition();
            }
        };
    }

    public Iterable<Segment> segments() {
        return segments::iterator;
    }

    public static class Head extends Segment {
        public Head(Level level, Direction facing, Vec2 position) {
            super(level, facing, position);
        }
    }

    public static class Segment extends Entity {
        private Direction facing;
        private Vec2 nextPosition;

        public Segment(Level level, Direction facing, Vec2 position) {
            super(level, position, SEGMENT_SIZE);
            this.facing = facing;
            this.setNextPosition();
        }

        public final Direction facing() {
            return this.facing;
        }

        public final void setFacing(Direction facing) {
            this.facing = facing;
        }

        public final Vec2 nextPosition() {
            return this.nextPosition;
        }

        public final void setNextPosition() {
            double x = this.position().x();
            double y = this.position().y();

            if (this.facing().isHorizontal()) {
                x = Math.floor(x) + 0.5;
            } else {
                y = Math.floor(y) + 0.5;
            }

            this.nextPosition = new Vec2(x, y).relative(this.facing());
        }
    }
}
