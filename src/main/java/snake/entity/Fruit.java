package main.java.snake.entity;

import main.java.snake.constant.GameConstants;
import main.java.snake.core.Level;
import main.java.snake.math.Time;
import main.java.snake.rendering.sprite.ImageUtils;
import main.java.snake.math.Vec2;
import main.java.snake.math.Vec2i;
import main.java.snake.rendering.sprite.Sprite;

import java.awt.*;

public class Fruit extends Entity {
    private final int spriteScale = (int) (this.level().getGridScale() * 1.25);
    private final Sprite appleSprite;

    public Fruit(Level level, Vec2 position, float scale) {
        super(level, position, scale);

        this.appleSprite = new Sprite(ImageUtils.createScaled(ImageUtils.fromPath("fruit/apple.png"), spriteScale, spriteScale));
    }

    public void draw(Graphics2D graphics) {

        // Animate scale
        double t = Time.nanosToSeconds((Time.now() - GameConstants.START_NANO));
        double scale = 1 + 0.2 * Math.sin(Math.PI * t);

        Vec2i visualCenter = this.level().gridToScreen(Vec2.atCenterOf(this.gridPosition()));

        this.appleSprite.setScale(scale);
        this.appleSprite.draw(
                graphics,
                visualCenter.getX(),
                visualCenter.getY()
        );

        // Display hitbox
        if (GameConstants.debugHitboxes) {
            int grid = this.level().getGridScale();

            Hitbox hitbox = this.hitbox();
            Vec2i hitboxCenter = this.level().gridToScreen(hitbox.getCenter());
            int width = (int) (this.getHitboxWidth() * grid);
            int height = (int) (this.getHitboxHeight() * grid);

            graphics.setColor(Color.white);
            graphics.drawRect(hitboxCenter.getX() - (width / 2), hitboxCenter.getY() - (height / 2), width, height);
        }
    }
}
