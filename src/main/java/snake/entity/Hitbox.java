package main.java.snake.entity;

import main.java.snake.math.Direction;
import main.java.snake.math.GridPos;
import main.java.snake.math.Mth;
import main.java.snake.math.Vec2;

import java.util.List;
import java.util.Optional;

public class Hitbox {
    public final double minX;
    public final double minY;
    public final double maxX;
    public final double maxY;
    
    public Hitbox(final double minX, final double minY, final double maxX, final double maxY) {
        this.minX = Math.min(minX, maxX);
        this.minY = Math.min(minY, maxY);
        this.maxX = Math.max(minX, maxX);
        this.maxY = Math.max(minY, maxY);
    }
    
    public Hitbox(final GridPos pos) {
        this(pos.getX(), pos.getY(), pos.getX() + 1, pos.getY() + 1);
    }
    
    public Hitbox(final Vec2 begin, final Vec2 end) {
        this(begin.x(), begin.y(), end.x(), end.y());
    }
    
    public Hitbox setMinX(final double minX) {
        return new Hitbox(minX, this.minY, this.maxX, this.maxY);
    }

    public Hitbox setMinY(final double minY) {
        return new Hitbox(this.minX, minY, this.maxX, this.maxY);
    }
    
    public Hitbox setMaxX(final double maxX) {
        return new Hitbox(this.maxX, this.minY, maxX, this.maxY);
    }

    public Hitbox setMaxY(final double maxY) {
        return new Hitbox(this.maxX, this.minY, this.maxX, maxY);
    }

    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        } else if (o instanceof Hitbox hitbox) {
            if (Double.compare(hitbox.minX, this.minX) != 0) {
                return false;
            } else if (Double.compare(hitbox.minY, this.minY) != 0) {
                return false;
            } else if (Double.compare(hitbox.maxX, this.maxX) != 0) {
                return false;
            } else {
                return Double.compare(hitbox.maxY, this.maxY) == 0;
            }
        } else {
            return false;
        }
    }

    public int hashCode() {
        int result = Double.hashCode(this.minX);
        result = 31 * result + Double.hashCode(this.minY);
        result = 31 * result + Double.hashCode(this.maxX);
        result = 31 * result + Double.hashCode(this.maxY);
        return result;
    }

    public Hitbox contract(final double xa, final double ya) {
        double minX = this.minX;
        double minY = this.minY;
        double maxX = this.maxX;
        double maxY = this.maxY;
        if (xa < (double)0.0F) {
            minX -= xa;
        } else if (xa > (double)0.0F) {
            maxX -= xa;
        }

        if (ya < (double)0.0F) {
            minY -= ya;
        } else if (ya > (double)0.0F) {
            maxY -= ya;
        }
        
        return new Hitbox(minX, minY, maxX, maxY);
    }

    public Hitbox expandTowards(final Vec2 delta) {
        return this.expandTowards(delta.x(), delta.y());
    }

    public Hitbox expandTowards(final double xa, final double ya) {
        double minX = this.minX;
        double minY = this.minY;
        double maxX = this.maxX;
        double maxY = this.maxY;
        if (xa < (double)0.0F) {
            minX += xa;
        } else if (xa > (double)0.0F) {
            maxX += xa;
        }

        if (ya < (double)0.0F) {
            minY += ya;
        } else if (ya > (double)0.0F) {
            maxY += ya;
        }

        return new Hitbox(minX, minY, maxX, maxY);
    }

    public Hitbox inflate(final double xAdd, final double yAdd) {
        double minX = this.minX - xAdd;
        double minY = this.minY - yAdd;
        double maxX = this.maxX + xAdd;
        double maxY = this.maxY + yAdd;
        return new Hitbox(minX, minY, maxX, maxY);
    }

    public Hitbox inflate(final double amountToAddInAllDirections) {
        return this.inflate(amountToAddInAllDirections, amountToAddInAllDirections);
    }

    public Hitbox intersect(final Hitbox other) {
        double minX = Math.max(this.minX, other.minX);
        double minY = Math.max(this.minY, other.minY);
        double maxX = Math.min(this.maxX, other.maxX);
        double maxY = Math.min(this.maxY, other.maxY);
        return new Hitbox(minX, minY, maxX, maxY);
    }

    public Hitbox minmax(final Hitbox other) {
        double minX = Math.min(this.minX, other.minX);
        double minY = Math.min(this.minY, other.minY);
        double maxX = Math.max(this.maxX, other.maxX);
        double maxY = Math.max(this.maxY, other.maxY);
        return new Hitbox(minX, minY, maxX, maxY);
    }

    public Hitbox move(final double xa, final double ya) {
        return new Hitbox(this.minX + xa, this.minY + ya, this.maxX + xa, this.maxY + ya);
    }

    public Hitbox move(final GridPos pos) {
        return new Hitbox(this.minX + (double)pos.getX(), this.minY + (double)pos.getY(), this.maxX + (double)pos.getX(), this.maxY + (double)pos.getY());
    }

    public Hitbox move(final Vec2 pos) {
        return this.move(pos.x(), pos.y());
    }

    public boolean intersects(final Hitbox hitbox) {
        return this.intersects(hitbox.minX, hitbox.minY, hitbox.maxX, hitbox.maxY);
    }

    public boolean intersects(final double minX, final double minY, final double maxX, final double maxY) {
        return this.minX < maxX && this.maxX > minX && this.minY < maxY && this.maxY > minY;
    }

    public boolean intersects(final Vec2 min, final Vec2 max) {
        return this.intersects(Math.min(min.x(), max.x()), Math.min(min.y(), max.y()), Math.max(min.x(), max.x()), Math.max(min.y(), max.y()));
    }

    public boolean intersects(final GridPos pos) {
        return this.intersects(pos.getX(), pos.getY(), pos.getX() + 1, pos.getY() + 1);
    }

    public boolean contains(final Vec2 vec) {
        return this.contains(vec.x(), vec.y());
    }

    public boolean contains(final double x, final double y) {
        return x >= this.minX && x < this.maxX && y >= this.minY && y < this.maxY;
    }

    public double getSize() {
        double xs = this.getXsize();
        double ys = this.getYsize();
        return (xs + ys) / (double)3.0F;
    }

    public double getXsize() {
        return this.maxX - this.minX;
    }

    public double getYsize() {
        return this.maxY - this.minY;
    }

    public Hitbox deflate(final double xSubstract, final double ySubtract) {
        return this.inflate(-xSubstract, -ySubtract);
    }

    public Hitbox deflate(final double amount) {
        return this.inflate(-amount);
    }

    public Optional<Vec2> clip(final Vec2 from, final Vec2 to) {
        return clip(this.minX, this.minY, this.maxX, this.maxY, from, to);
    }

    public static Optional<Vec2> clip(final double minX, final double minY, final double maxX, final double maxY, final Vec2 from, final Vec2 to) {
        double[] scaleReference = new double[]{(double)1.0F};
        double dx = to.x() - from.x();
        double dy = to.y() - from.y();
        Direction direction = getDirection(minX, minY, maxX, maxY, from, scaleReference, (Direction)null, dx, dy);
        if (direction == null) {
            return Optional.empty();
        } else {
            double scale = scaleReference[0];
            return Optional.of(from.add(scale * dx, scale * dy));
        }
    }

    private static Direction getDirection(final Hitbox hitbox, final Vec2 from, final double[] scaleReference, final Direction direction, final double dx, final double dy) {
        return getDirection(hitbox.minX, hitbox.minY, hitbox.maxX, hitbox.maxY, from, scaleReference, direction, dx, dy);
    }

    private static Direction getDirection(final double minX, final double minY, final double maxX, final double maxY, final Vec2 from, final double[] scaleReference, Direction direction, final double dx, final double dy) {
        if (dx > Mth.EPSILON) {
            direction = clipPoint(scaleReference, direction, dx, dy, minX, minY, maxY, Direction.LEFT, from.x(), from.y());
        } else if (dx < -Mth.EPSILON) {
            direction = clipPoint(scaleReference, direction, dx, dy, maxX, minY, maxY, Direction.RIGHT, from.x(), from.y());
        }

        if (dy > Mth.EPSILON) {
            direction = clipPoint(scaleReference, direction, dy, dx, minY, minX, maxX, Direction.DOWN, from.y(), from.x());
        } else if (dy < -Mth.EPSILON) {
            direction = clipPoint(scaleReference, direction, dy, dx, maxY, minX, maxX, Direction.UP, from.y(), from.x());
        }

        return direction;
    }

    private static Direction clipPoint(final double[] scaleReference, final Direction direction, final double da, final double db, final double point, final double minB, final double maxB, final Direction newDirection, final double fromA, final double fromB) {
        double s = (point - fromA) / da;
        double pb = fromB + s * db;
        if ((double)0.0F < s && s < scaleReference[0] && minB - Mth.EPSILON < pb && pb < maxB + Mth.EPSILON) {
            scaleReference[0] = s;
            return newDirection;
        } else {
            return direction;
        }
    }

    public boolean collidedAlongVector(final Vec2 vector, final List<Hitbox> hitboxes) {
        Vec2 from = this.getCenter();
        Vec2 to = from.add(vector);

        for(Hitbox shapePart : hitboxes) {
            Hitbox inflated = shapePart.inflate(this.getXsize() * (double)0.5F - Mth.EPSILON, this.getYsize() * (double)0.5F - Mth.EPSILON);
            if (inflated.contains(to) || inflated.contains(from)) {
                return true;
            }

            if (inflated.clip(from, to).isPresent()) {
                return true;
            }
        }

        return false;
    }

    public double distanceToSqr(final Vec2 point) {
        double dx = Math.max(Math.max(this.minX - point.x(), point.x() - this.maxX), 0.0F);
        double dy = Math.max(Math.max(this.minY - point.y(), point.y() - this.maxY), 0.0F);
        return dx * dx + dy * dy;
    }

    public double distanceToSqr(final Hitbox boundingBox) {
        double dx = Math.max(Math.max(this.minX - boundingBox.maxX, boundingBox.minX - this.maxX), (double)0.0F);
        double dy = Math.max(Math.max(this.minY - boundingBox.maxY, boundingBox.minY - this.maxY), (double)0.0F);
        return dx * dx + dy * dy;
    }

    public String toString() {
        return "Hitbox[" + this.minX + ", " + this.minY + ", " + "] -> [" + this.maxX + ", " + this.maxY + ", " + "]";
    }

    public boolean hasNaN() {
        return Double.isNaN(this.minX) || Double.isNaN(this.minY) || Double.isNaN(this.maxX) || Double.isNaN(this.maxY);
    }

    public Vec2 getCenter() {
        return new Vec2(Mth.lerp(0.5F, this.minX, this.maxX), Mth.lerp(0.5F, this.minY, this.maxY));
    }

    public Vec2 getMinPosition() {
        return new Vec2(this.minX, this.minY);
    }

    public Vec2 getMaxPosition() {
        return new Vec2(this.maxX, this.maxY);
    }

    public static Hitbox ofSize(final Vec2 center, final double sizeX, final double sizeY) {
        return new Hitbox(center.x() - sizeX / (double)2.0F, center.y() - sizeY / (double)2.0F, center.x() + sizeX / (double)2.0F, center.y() + sizeY / (double)2.0F);
    }
}
