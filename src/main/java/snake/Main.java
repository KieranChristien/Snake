package main.java.snake;

import main.java.snake.util.ImageUtils;

import javax.swing.*;
import java.awt.image.BufferedImage;

public class Main {
    private static final String TITLE = "Snake";
    private static final BufferedImage ICON = ImageUtils.fromPath("fruit/apple.png");

    static void main(String[] args) {
        // Initialise game
        JFrame window = new JFrame();
        GameLoop gameLoop = new GameLoop();

        window.add(gameLoop);
        window.pack();
        window.setTitle(TITLE);
        window.setIconImage(ICON);
        window.setLocationRelativeTo(null);
        window.setResizable(false);
        window.setVisible(true);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}