package main.java.snake.core;

import main.java.snake.constant.ColourConstants;
import main.java.snake.constant.GameConstants;
import main.java.snake.constant.WindowConstants;
import main.java.snake.io.Data;
import main.java.snake.math.Direction;
import main.java.snake.rendering.ui.ControlsHint;
import main.java.snake.rendering.ui.Scoreboard;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayDeque;

public class GameLoop extends JPanel implements KeyListener, ActionListener {
    private static final int MAX_QUEUE = 2;

    private final java.util.Queue<Direction> inputQueue = new ArrayDeque<>();
    private final Level level;

    private Direction input;
    private GameState state;
    private int highScore;

    public GameLoop() {
        setPreferredSize(new Dimension(WindowConstants.WIDTH, WindowConstants.HEIGHT));
        setBackground(ColourConstants.BACKGROUND);

        addKeyListener(this);
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);

        this.input = GameConstants.START_DIR;
        this.level = new Level();
        this.state = GameState.START_MENU;

        new Timer(10, this).start();
    }

    public void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D graphics2D = (Graphics2D) graphics;

        // Save old colour
        Color cOld = graphics.getColor();

        // Draw the level
        this.level.draw(graphics, graphics2D);

        // Draw UI
        graphics.setColor(ColourConstants.TOP);
        graphics.fillRect(0, 0, WindowConstants.WIDTH, WindowConstants.TOP_MARGIN);
        graphics.setColor(ColourConstants.TOP_DIVIDER);
        graphics.drawLine(0, WindowConstants.TOP_MARGIN, WindowConstants.WIDTH, WindowConstants.TOP_MARGIN);

        Scoreboard.draw(graphics2D, this.level.getScore(), this.getHighScore());

        if (this.state == GameState.START_MENU) {
            ControlsHint.draw(graphics2D);
        }

        graphics.setColor(cOld);
    }

    private void queueInput(Direction input) {
        // Don't add if full
        if (this.inputQueue.size() >= MAX_QUEUE) return;

        // Start game on player input
        if (this.state == GameState.START_MENU && input != this.input.getOpposite())
            this.state = GameState.PLAY;

        // Only add input to queue if input valid next move
        Direction last = this.inputQueue.isEmpty()
                ? this.level.getPlayer().getFacing()
                : ((ArrayDeque<Direction>) this.inputQueue).peekLast();

        if (input != last && input != last.getOpposite()) {
            this.inputQueue.offer(input);
        }
    }

    private void reset() {
        if (this.getHighScore() < this.level.getScore()) this.setHighScore(this.level.getScore());

        this.input = GameConstants.START_DIR;
        this.inputQueue.clear();
        this.state = GameState.START_MENU;
        this.level.reset();
    }

    private void pause() {
        this.inputQueue.clear();
        this.state = GameState.START_MENU;
    }

    private void setHighScore(int score) {
        this.highScore = score;

        Data.saveHighScore(score);
    }

    private int getHighScore() {
        int fileHighScore = Data.loadHighScore();
        if (fileHighScore != 0) return fileHighScore;
        return this.highScore;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (this.state == GameState.PLAY) {
            // Get next direction when aligned to grid
            if (this.level.getPlayer().isAligned() && !this.inputQueue.isEmpty()) this.input = this.inputQueue.poll();

            this.level.tick(this.input);

            // End game if players hits wall or self
            if (this.level.didPlayerHitWall() || this.level.didPlayerHitSelf()) {
                this.level.getPlayer().setAlive(false);
                this.state = GameState.GAME_OVER;
            }
        }

        this.repaint();
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP -> this.queueInput(Direction.UP);
            case KeyEvent.VK_DOWN -> this.queueInput(Direction.DOWN);
            case KeyEvent.VK_LEFT -> this.queueInput(Direction.LEFT);
            case KeyEvent.VK_RIGHT -> this.queueInput(Direction.RIGHT);
            case KeyEvent.VK_H -> GameConstants.debugHitboxes = !GameConstants.debugHitboxes;
            case KeyEvent.VK_ENTER -> {
                if (this.state == GameState.GAME_OVER) this.reset();
            }
            case KeyEvent.VK_ESCAPE -> {
                if (this.state == GameState.PLAY) this.pause();
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}
