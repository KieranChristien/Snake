package main.java.snake.entity;

import main.java.snake.core.Level;
import main.java.snake.math.GridPos;
import main.java.snake.math.Mth;
import main.java.snake.math.Vec2;

public abstract class Entity {
    private final Dimensions dimensions;
    private final Level level;
    private Hitbox hitbox;
    private Vec2 position;
    private GridPos gridPosition;

    public Entity(Level level, Vec2 position, float scale) {
        this(level, position, scale, scale);
    }

    public Entity(Level level, Vec2 position, float width, float height) {
        this.dimensions = new Dimensions(width, height);
        this.level = level;
        this.position = Vec2.ZERO;
        this.gridPosition = GridPos.ZERO;
        this.setPos(position.x(), position.y());
    }

    public final Level level() {
        return this.level;
    }

    public final Vec2 position() {
        return this.position;
    }

    public final GridPos gridPosition() {
        return this.gridPosition;
    }

    public final void setPos(Vec2 pos) {
        this.setPos(pos.x(), pos.y());
    }

    public void setPos(double x, double y) {
        this.setPosRaw(x, y);
        this.setHitbox(this.makeHitbox());
    }

    public void setPosRaw(double x, double y) {
        // Remove floating point issues
        double roundedX = Mth.round(x, 2);
        double roundedY = Mth.round(y, 2);

        if (this.position.x() != x || this.position.y() != y) {
            this.position = new Vec2(roundedX, roundedY);
            int fx = (int) Math.floor(roundedX);
            int fy = (int) Math.floor(roundedY);
            if (fx != this.gridPosition.getX() || fy != this.gridPosition.getY()) {
                this.gridPosition = new GridPos(fx, fy);
            }
        }
    }

    protected final Hitbox makeHitbox() {
        return this.dimensions.makeHitbox(this.position);
    }

    public final void setHitbox(Hitbox hitbox) {
        this.hitbox = hitbox;
    }

    public final Hitbox hitbox() {
        return this.hitbox;
    }

    public final float getHitboxWidth() {
        return this.dimensions.width();
    }

    public final float getHitboxHeight() {
        return this.dimensions.height();
    }

    public final Dimensions getDimensions() {
        return this.dimensions;
    }
}
