package main.java.snake.ui;

import main.java.snake.Level;
import main.java.snake.constant.GameConstants;
import main.java.snake.util.ImageUtils;
import main.java.snake.util.Sprite;
import main.java.snake.util.Vec2i;

import java.awt.*;

public class ControlsHint {
    private static final Color BACKGROUND_COLOR = new Color(0, 0, 0, 125);
    private static final int BACKGROUND_SIZE = Level.GRID_SCALE * 3 + 10;
    private static final int BACKGROUND_ARC = (int) (BACKGROUND_SIZE * 0.25);
    private static final int SPRITE_SIZE = (int) (BACKGROUND_SIZE * 0.8);
    private static final Sprite CONTROLS_SPRITE = new Sprite(ImageUtils.fromPathScaled("ui/controls.png", SPRITE_SIZE, SPRITE_SIZE));
    private static final Vec2i CENTER_POS = new Vec2i(GameConstants.WINDOW_WIDTH / 2, 200);

    public static void draw(Graphics2D graphics) {
        graphics.setColor(BACKGROUND_COLOR);
        graphics.fillRoundRect(CENTER_POS.getX() - BACKGROUND_SIZE / 2, CENTER_POS.getY() - BACKGROUND_SIZE / 2, BACKGROUND_SIZE, BACKGROUND_SIZE, BACKGROUND_ARC, BACKGROUND_ARC);

        CONTROLS_SPRITE.draw(graphics, CENTER_POS.getX(), CENTER_POS.getY());
    }
}
