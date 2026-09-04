package main.java.snake.rendering.ui;

import main.java.snake.constant.LevelConstants;
import main.java.snake.constant.WindowConstants;
import main.java.snake.rendering.sprite.ImageUtils;
import main.java.snake.rendering.sprite.Sprite;
import main.java.snake.math.Vec2i;

import java.awt.*;

public class Scoreboard {
    private static final Sprite FRUIT_SPRITE = new Sprite(ImageUtils.fromPathScaled(
            "fruit/apple.png",
            LevelConstants.GRID_SCALE,
            LevelConstants.GRID_SCALE
    ));
    private static final int FONT_SIZE = 24;
    private static final Font FONT = new Font(
            "Serif",
            Font.BOLD,
            FONT_SIZE
    );
    private static final Vec2i SPRITE_CENTRE = new Vec2i(
            WindowConstants.TOP_MARGIN / 2,
            WindowConstants.TOP_MARGIN / 2
    );

    public static void draw(Graphics2D graphics, String score) {
        FRUIT_SPRITE.draw(graphics, SPRITE_CENTRE.getX(), SPRITE_CENTRE.getY());

        graphics.setColor(Color.WHITE);
        graphics.setFont(FONT);
        graphics.drawString(score, SPRITE_CENTRE.getX() + LevelConstants.HALF_GRID_SCALE,  SPRITE_CENTRE.getY() + FONT_SIZE / 2);
    }
}
