package main.java.snake;

import main.java.snake.constant.ColourConstants;
import main.java.snake.constant.GameConstants;
import main.java.snake.ui.ControlsHint;
import main.java.snake.util.Direction;
import main.java.snake.util.Now;

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

    public GameLoop() {
        setPreferredSize(new Dimension(GameConstants.WINDOW_WIDTH, GameConstants.WINDOW_HEIGHT));
        setBackground(ColourConstants.BACKGROUND_COLOUR);

        addKeyListener(this);
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);

        this.input = GameConstants.START_DIR;
        this.level = new Level();
        this.state = GameState.START_MENU;

        new Timer(8, this).start();
    }

    public void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D graphics2D = (Graphics2D) graphics;

        // Update now
        Now.set(System.nanoTime());

        // Draw the level
        this.level.draw(graphics, graphics2D);

        // Draw UI
        if (this.state == GameState.START_MENU) {
            ControlsHint.draw(graphics2D);
        }
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
        this.input = GameConstants.START_DIR;
        this.inputQueue.clear();
        this.state = GameState.START_MENU;
        this.level.reset();
    }

    private void pause() {
        this.inputQueue.clear();
        this.state = GameState.START_MENU;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (this.state == GameState.PLAY) {
            // Get next direction when aligned to grid
            if (this.level.getPlayer().isAligned() && !this.inputQueue.isEmpty()) this.input = this.inputQueue.poll();

            // End game if players hits wall
            if (this.level.didPlayerHitWall() || this.level.didPlayerHitSelf()) this.state = GameState.GAME_OVER;

            this.level.tick(this.input);
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
