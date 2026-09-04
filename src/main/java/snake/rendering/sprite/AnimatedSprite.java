package main.java.snake.rendering.sprite;

import main.java.snake.math.Time;

import java.awt.*;
import java.awt.image.BufferedImage;

public class AnimatedSprite extends Sprite {
    private final BufferedImage[] frames;
    private final int frameCount;
    private final long frameDurationNanos;
    private final LoopType loopType;

    private BufferedImage frame;

    // Caching fields
    private BufferedImage[] cachedFrames;
    private double cachedScale;
    private Sprite.ScaleMethod cachedScaleMethod;

    // Current frame cache to avoid array indexing each draw
    private BufferedImage cachedFrameImage = null;
    private int lastFrameIndex = -1;

    // Timing / playback
    private long startTime = Time.now();
    private long elapsedTime = 0L;
    private boolean playing = false;

    // Manual playback
    private boolean manualPlayback = false;
    private double normalisedTime = 0.0;

    public AnimatedSprite(BufferedImage sheet, double fps, LoopType loopType) {
        int remainder = sheet.getHeight() % sheet.getWidth();
        if (remainder != 0)
            throw new IllegalArgumentException("Sprite sheet height must be a multiple of sprite sheet width");
        this(sheet, sheet.getWidth(), sheet.getWidth(), sheet.getHeight() / sheet.getWidth(), fps, loopType);
    }

    public AnimatedSprite(BufferedImage sheet, int frameCount, double fps, LoopType loopType) {
        int remainder = sheet.getHeight() % frameCount;
        if (remainder != 0)
            throw new IllegalArgumentException("Frame count must divide sprite height with no remainder");
        this(sheet, sheet.getWidth(), sheet.getHeight() / frameCount, frameCount, fps, loopType);
    }

    public AnimatedSprite(BufferedImage sheet, int frameW, int frameH, int frameCount, double fps, LoopType loopType) {
        super(sheet);
        if (frameW <= 0 || frameH <= 0) throw new IllegalArgumentException("invalid frame size");
        if (frameCount <= 0) throw new IllegalArgumentException("frameCount must be > 0");
        if (fps == 0) throw new IllegalArgumentException("fps must != 0");
        if (loopType == null) throw new IllegalArgumentException("loopType must not be null");

        this.frameCount = frameCount;
        this.frameDurationNanos = (long) (1_000_000_000.0 / fps);
        if (this.frameDurationNanos < 0) {
            this.manualPlayback = true;
        }
        this.loopType = loopType;

        int cols = Math.max(1, sheet.getWidth() / frameW);
        this.frames = new BufferedImage[frameCount];
        for (int i = 0; i < frameCount; i++) {
            int cx = i % cols;
            int cy = i / cols;
            int sx = cx * frameW;
            int sy = cy * frameH;

            // copy subimage into a new BufferedImage to avoid referencing parent sheet
            BufferedImage sub = new BufferedImage(frameW, frameH, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = sub.createGraphics();
            g.drawImage(sheet.getSubimage(sx, sy, frameW, frameH), 0, 0, null);
            g.dispose();
            this.frames[i] = sub;
        }

        // build initial scaled frames at default scale
        this.cachedScale = super.getScale();
        this.cachedScaleMethod = super.getScaleMethod();
        this.cacheFrames();
    }

    @Override
    public void setScale(double scale) {
        super.setScale(scale);
        // rebuild scaled frames lazily on next draw
        if (Double.compare(this.cachedScale, scale) != 0) {
            this.cacheFrames();
        }
    }

    @Override
    public void setScaleMethod(ScaleMethod method) {
        super.setScaleMethod(method);
        if (this.cachedScaleMethod != method) {
            this.cacheFrames();
        }
    }

    private void cacheFrames() {
        double scale = super.getScale();
        if (cachedFrames != null && Double.compare(scale, this.cachedScale) == 0 && this.cachedScaleMethod == super.getScaleMethod()) {
            return; // nothing to do
        }

        int n = this.frameCount;
        this.cachedFrames = new BufferedImage[n];
        // choose interpolation hint
        Object hint = toInterpolationHint(super.getScaleMethod());
        for (int i = 0; i < n; ++i) {
            BufferedImage src = this.frames[i];
            int sw = src.getWidth();
            int sh = src.getHeight();
            int tw = Math.max(1, (int) Math.round(sw * scale));
            int th = Math.max(1, (int) Math.round(sh * scale));

            BufferedImage dst = new BufferedImage(tw, th, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = dst.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, hint);
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g.drawImage(src, 0, 0, tw, th, null);
            g.dispose();
            this.cachedFrames[i] = dst;
        }

        // reset cached frame pointer so next draw picks correct image
        this.lastFrameIndex = -1;
        this.cachedScale = scale;
        this.cachedScaleMethod = super.getScaleMethod();
    }

    private int currentFrameIndex() {
        if (this.manualPlayback) {
            // Map normalised time [0, 1] to frame index [0, frameCount - 1]
            return (int) Math.round(this.normalisedTime * (this.frameCount - 1));
        }

        if (this.playing) {
            this.elapsedTime = Time.now() - this.startTime;
        }

        long frameNumber = this.elapsedTime / this.frameDurationNanos;

        if (frameNumber >= this.frameCount) {
            switch (this.loopType) {
                case LOOPING:
                    return (int) (frameNumber % this.frameCount);

                case HOLD_ON_LAST_FRAME:
                    return this.frameCount - 1;

                case PLAY_ONCE:
                    this.playing = false;
                    this.elapsedTime = 0L;
                    return 0;
            }
        }

        return (int) frameNumber;
    }

    @Override
    public void draw(Graphics2D graphics, int x, int y) {
        // ensure scaled frames are up-to-date with current scale/scaleMethod
        this.cacheFrames();

        // if rotation is zero, use the cheap path: draw pre-scaled image directly
        if (Double.compare(super.getRotationDeg(), 0.0) == 0) {
            int idx = this.currentFrameIndex();
            if (idx != lastFrameIndex) {
                // update cached frame image
                this.cachedFrameImage = this.cachedFrames[idx];
                this.lastFrameIndex = idx;
            }
            if (this.cachedFrameImage != null) {
                int w = this.cachedFrameImage.getWidth();
                int h = this.cachedFrameImage.getHeight();
                graphics.drawImage(this.cachedFrameImage, x - w / 2, y - h / 2, null);
                return;
            }
        }

        // fallback: rotation or other transform required — use parent draw (which applies transforms)
        // parent draw will call getImage() which returns the current frame (we set it below)
        this.frame = frames[this.currentFrameIndex()];
        super.draw(graphics, x, y);
    }

    /**
     * Set the animation to a normalised position.
     *
     * @param value normalised position in the range [0, 1]
     */
    public void setProgress(double value) {
        this.normalisedTime = Math.clamp(value, 0.0, 1.0);
        this.manualPlayback = true;
    }

    /**
     * Get the current normalised position.
     */
    public double getProgress() {
        return this.normalisedTime;
    }

    /**
     * Return to time-based playback.
     */
    public void useAutomaticPlayback() {
        this.manualPlayback = false;
        this.startTime = Time.now() - this.elapsedTime;
        this.playing = true;
    }

    /**
     * Start the animation from the beginning
     *
     */
    public void start() {
        this.elapsedTime = 0L;
        this.startTime = Time.now();
        this.playing = true;
    }

    /**
     * Start the animation from the beginning if stopped
     *
     */
    public void startIfStopped() {
        if (!this.playing) {
            this.start();
        }
    }

    /**
     * Resume the animation
     *
     */
    public void resume() {
        if (!this.playing) {
            this.startTime = Time.now();
            this.playing = true;
        }
    }

    /**
     * Pause the animation
     *
     */
    public void pause() {
        this.playing = false;
    }

    /**
     * Stop and reset the animation
     *
     */
    public void stop() {
        this.playing = false;
        this.elapsedTime = 0L;
    }

    public boolean isStarted() {
        return this.playing;
    }

    @Override
    protected BufferedImage getImage() {
        return this.frame;
    }

    public enum LoopType {
        LOOPING,
        HOLD_ON_LAST_FRAME,
        PLAY_ONCE
    }
}
