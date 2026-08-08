package main.java.snake.entity;

import main.java.snake.constant.ColourConstants;
import main.java.snake.constant.GameConstants;
import main.java.snake.Level;
import main.java.snake.util.*;

import java.awt.*;
import java.util.*;

public class Player {
    private static final float SEGMENT_SIZE = 0.75F;
    private static final int SEGMENT_VISUAL_SIZE = (int) (SEGMENT_SIZE * Level.GRID_SCALE);
    private static final int MAX_LENGTH = 255;
    private final Level level;
    private final int segmentOffset;
    private final ArrayList<Segment> segments = new ArrayList<>();

    private final AnimatedSprite sprite;

    public Player(Level level, Direction facing, Vec2 position) {
        this.level = level;
        this.addHead(facing, position);
        this.addSegments(2);

        this.segmentOffset = (level.getGridScale() - (int) (SEGMENT_SIZE * level.getGridScale())) / 2;

        this.sprite = new AnimatedSprite(ImageUtils.fromPath("snake/blink.png"), 84, 84, 9, 12);
        this.sprite.setScale(0.5);
    }

    public final void reset(Direction facing, Vec2 position) {
        this.segments.clear();
        this.addHead(facing, position);
        this.addSegments(2);
    }

    public final void draw(Graphics2D graphics) {
        // Enable antialiasing for shapes
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        ListIterator<Segment> reversed = this.segments.reversed().listIterator();
        Segment next = reversed.hasNext() ? reversed.next() : null;
        Vec2i connectFrom = next != null ? this.level.gridToActual(next.position()) : null;

        graphics.setColor(ColourConstants.PLAYER_COLOUR);

        while (next != null) {
            Segment current = next;
            next = reversed.hasNext() ? reversed.next() : null;

            Vec2i visualPos = current.level().gridToActual(current.position());
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
                    Vec2 cornerPos =  current.facing().isHorizontal() && next.facing().isVertical() ? new Vec2(next.position().x(), current.position().y()) : new Vec2(current.position().x(), next.position().y());
                    Vec2i cornerVisual = this.level.gridToActual(cornerPos);

                    graphics.fillOval(
                            cornerVisual.getX() - (this.level.getHalfGridScale()) + this.segmentOffset,
                            cornerVisual.getY() - (this.level.getHalfGridScale()) + this.segmentOffset,
                            SEGMENT_VISUAL_SIZE,
                            SEGMENT_VISUAL_SIZE
                    );

                    connectTo = cornerVisual;
                }
            } else {
                connectTo = this.level.gridToActual(current.position());
            }

            if (connectTo != null) {
                Vec2i to = connectTo.subtract(connectFrom);
                Direction dirTo = Direction.getApproximateNearest(to);
                Direction offset = switch (dirTo) {
                    case UP, DOWN ->  Direction.LEFT;
                    case LEFT, RIGHT ->  Direction.DOWN;
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

        // Display hitbox
        if (GameConstants.debugHitboxes) {
            for (Segment segment : this.segments) {
                int grid = this.level().getGridScale();

                Hitbox hitbox = segment.hitbox();
                Vec2i hitboxCenter = segment.level().gridToActual(hitbox.getCenter());
                int width = (int) (segment.getHitboxWidth() * grid);
                int height = (int) (segment.getHitboxHeight() * grid);

                graphics.setColor(Color.white);
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

    public final void move() {
        for (int i = this.segments.size() - 1; i >= 0; --i) {
            Segment current = this.segments.get(i);
            Vec2 delta = this.getMoveDelta(current.facing(), 0.05);

            // Move body: if not head, check gap to predecessor
            if (i > 0) {
                Segment next = this.segments.get(i - 1); // predecessor toward head
                Vec2 nextDiff = next.position().subtract(current.position());
                if ((Mth.round(Math.abs(nextDiff.x), 2) + Mth.round(Math.abs(nextDiff.y), 2) >= 1.0)) {
                    current.setPos(current.position().add(delta));
                }
            } else {
                // head always moves forward
                current.setPos(current.position().add(delta));
            }

            // Update facing and nextPosition when reached nextPosition
            Vec2 diff = current.position().subtract(current.nextPosition());
            if ((Mth.round(Math.abs(diff.x), 2) + Mth.round(Math.abs(diff.y), 2) == 0.0)) {
                if (i > 0) current.setFacing(this.segments.get(i - 1).facing());
                current.setNextPosition(current.position().relative(current.facing()));
            }
        }
    }

    public final boolean isAligned() {
        return Mth.round(this.getX() % 1, 2) == 0.5 && Mth.round(this.getY() % 1, 2) == 0.5;
    }

    public final boolean hitSelf() {
        Player.Head head = this.getHead();
        for (Player.Segment segment : segments) {
            if (segment.equals(head)) continue;
            if (segment.hitbox().intersects(this.getFacePosition(), this.getFacePosition())) return true;
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
            case DOWN -> new Vec2(center.x, headHitbox.minY);
            case UP -> new Vec2(center.x, headHitbox.maxY);
            case LEFT -> new Vec2(headHitbox.minX, center.y);
            case RIGHT -> new Vec2(headHitbox.maxX, center.y);
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

        this.segments.addFirst(new Head(this.level, facing, position, position));
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
                    noGap ? previous.position() : previous.position().relative(prevFacing.getOpposite()),
                    previous.nextPosition()
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
        public Head(Level level, Direction facing, Vec2 position, Vec2 nextPosition) {
            super(level, facing, position, nextPosition);
        }
    }

    public static class Segment extends Entity {
        private Direction facing;
        private Vec2 nextPosition;

        public Segment(Level level, Direction facing, Vec2 position, Vec2 nextPosition) {
            super(level, position, SEGMENT_SIZE);
            this.facing = facing;
            this.nextPosition = nextPosition;
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

        public final void setNextPosition(Vec2 nextPosition) {
            this.nextPosition = nextPosition;
        }
    }
}
