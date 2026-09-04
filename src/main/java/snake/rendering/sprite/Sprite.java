package main.java.snake.rendering.sprite;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public class Sprite {
    private final BufferedImage image;
    private double scale = 1.0;
    private double rotationDeg = 0.0;
    private ScaleMethod scaleMethod = ScaleMethod.BILINEAR;

    public Sprite(BufferedImage image) {
        if (image == null) throw new IllegalArgumentException("image is null");
        this.image = image;
    }

    public void setScale(double scale) {
        if (scale <= 0) throw new IllegalArgumentException("scale must be > 0");
        if (Double.compare(this.scale, scale) != 0) {
            this.scale = scale;
        }
    }

    protected BufferedImage getImage() {
        return this.image;
    }

    protected double getScale() {
        return this.scale;
    }

    protected ScaleMethod getScaleMethod() {
        return this.scaleMethod;
    }

    public void setRotationDegrees(double degrees) {
        if (Double.compare(this.rotationDeg, degrees) != 0) {
            this.rotationDeg = degrees;
        }
    }

    public double getRotationDeg() {
        return this.rotationDeg;
    }

    public void setScaleMethod(ScaleMethod method) {
        if (method == null) throw new IllegalArgumentException("method null");
        if (this.scaleMethod != method) {
            this.scaleMethod = method;
        }
    }

    public void draw(Graphics2D graphics, int x, int y) {
        // save state
        AffineTransform oldTx = graphics.getTransform();
        Object oldInterp = graphics.getRenderingHint(RenderingHints.KEY_INTERPOLATION);
        Object oldAA = graphics.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
        Object oldRender = graphics.getRenderingHint(RenderingHints.KEY_RENDERING);
        Object oldAInterp = graphics.getRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION);
        Object oldCRender = graphics.getRenderingHint(RenderingHints.KEY_COLOR_RENDERING);

        // set hints for quality
        graphics.setRenderingHint(
                RenderingHints.KEY_INTERPOLATION,
                this.toInterpolationHint(this.scaleMethod)
        );
        graphics.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );
        graphics.setRenderingHint(
                RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY
        );
        graphics.setRenderingHint(
                RenderingHints.KEY_ALPHA_INTERPOLATION,
                RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY
        );
        graphics.setRenderingHint(
                RenderingHints.KEY_COLOR_RENDERING,
                RenderingHints.VALUE_COLOR_RENDER_QUALITY
        );

        AffineTransform at = getAt(x, y);
        graphics.drawImage(this.getImage(), at, null);

        // restore
        if (oldTx != null) graphics.setTransform(oldTx);
        if (oldInterp != null) graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, oldInterp);
        if (oldAA != null) graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, oldAA);
        if (oldRender != null) graphics.setRenderingHint(RenderingHints.KEY_RENDERING, oldRender);
        if (oldAInterp != null) graphics.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, oldAInterp);
        if (oldCRender != null) graphics.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, oldCRender);
    }

    protected AffineTransform getAt(int x, int y) {
        int w = this.getImage().getWidth();
        int h = this.getImage().getHeight();
        double sx = this.scale;
        double sy = this.scale;
        double theta = Math.toRadians(this.rotationDeg);

        // Build transform: translate to centre, rotate, scale, then draw image centered
        AffineTransform at = new AffineTransform();
        at.translate(x, y);                        // move to centre
        at.rotate(theta);                          // rotate about centre
        at.scale(sx, sy);                          // apply scale
        at.translate(-w / 2.0, -h / 2.0);   // draw so image centre maps to origin
        return at;
    }

    protected Object toInterpolationHint(ScaleMethod method) {
        return switch (method) {
            case NEAREST -> RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR;
            case BILINEAR -> RenderingHints.VALUE_INTERPOLATION_BILINEAR;
            case BICUBIC -> RenderingHints.VALUE_INTERPOLATION_BICUBIC;
        };
    }

    public enum ScaleMethod {
        NEAREST,
        BILINEAR,
        BICUBIC
    }
}
