package main.java.snake.entity;

import main.java.snake.util.Vec2;

public record Dimensions(float width, float height) {
    public Hitbox makeHitbox(final Vec2 pos) {
        return this.makeHitbox(pos.x, pos.y);
    }

    public Hitbox makeHitbox(final double x, final double y) {
        float w = this.width / 2.0F;
        float h = this.height / 2.0F;
        return new Hitbox(x - (double) w, y - (double) h, x + (double) w, y + (double) h);
    }
}
