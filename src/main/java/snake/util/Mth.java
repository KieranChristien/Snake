package main.java.snake.util;

public class Mth {
    public static final double EPSILON = 1.0E-7;

    public static double lengthSquared(final double x, final double y) {
        return x * x + y * y;
    }

    public static double length(final double x, final double y) {
        return Math.sqrt(lengthSquared(x, y));
    }

    public static double lerp(final double alpha1, final double p0, final double p1) {
        return p0 + alpha1 * (p1 - p0);
    }

    public static double round(double v, int decimals) {
        double scale = Math.pow(10, decimals);
        return Math.round(v * scale) / scale;
    }
}
