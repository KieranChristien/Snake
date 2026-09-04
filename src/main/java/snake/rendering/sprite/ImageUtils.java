package main.java.snake.rendering.sprite;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class ImageUtils {
    private static final Logger LOGGER = Logger.getLogger(ImageUtils.class.getName());

    private ImageUtils() {}

    public static BufferedImage fromPathScaled(String path, int targetW, int targetH) {
        return createScaled(fromPath(path), targetW, targetH);
    }

    public static BufferedImage fromPath(String path) {
        BufferedImage img;
        String resource = "/main/resources/assets/" + path;
        try (var in = ImageUtils.class.getResourceAsStream(resource)) {
            if (in == null) {
                LOGGER.log(Level.SEVERE, "Resource not found: {0}", resource);
                throw new IllegalStateException("Resource not found: " + resource);
            }
            img = ImageIO.read(in);
            if (img == null) {
                LOGGER.log(Level.SEVERE, "ImageIO.read returned null for: {0}", resource);
                throw new IllegalStateException("Resource not found: " + resource);
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to load image: " + resource, e);
            throw new IllegalStateException("Failed to load image: " + resource, e);
        }
        return img;
    }

    // High quality or fast scaling
    public static BufferedImage createScaled(BufferedImage src, int targetW, int targetH, boolean highQuality) {
        if (src == null) return null;
        if (!highQuality) {
            BufferedImage dst = new BufferedImage(targetW, targetH, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = dst.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2.drawImage(src, 0, 0, targetW, targetH, null);
            g2.dispose();
            return dst;
        }

        // Multistep downscaling for better quality
        int w = src.getWidth();
        int h = src.getHeight();
        BufferedImage current = src;
        do {
            int nextW = Math.max(targetW, w / 2);
            int nextH = Math.max(targetH, h / 2);

            BufferedImage tmp = new BufferedImage(nextW, nextH, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = tmp.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2.drawImage(current, 0, 0, nextW, nextH, null);
            g2.dispose();

            // prepare for next step
            current = tmp;
            w = current.getWidth();
            h = current.getHeight();
        } while (w != targetW || h != targetH);

        return current;
    }

    // Alternative using AffineTransformOp for single-step high-quality scaling
    public static BufferedImage createScaled(BufferedImage src, int targetW, int targetH) {
        BufferedImage dst = new BufferedImage(targetW, targetH, BufferedImage.TYPE_INT_ARGB);
        double scaleX = targetW / (double) src.getWidth();
        double scaleY = targetH / (double) src.getHeight();
        AffineTransform at = AffineTransform.getScaleInstance(scaleX, scaleY);
        AffineTransformOp ato = new AffineTransformOp(at, AffineTransformOp.TYPE_BICUBIC);
        ato.filter(src, dst);
        return dst;
    }
}
