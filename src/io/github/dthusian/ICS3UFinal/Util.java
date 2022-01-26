package io.github.dthusian.ICS3UFinal;

import java.awt.*;

public class Util {
    public static boolean between(int min, int v, int max) {
        return min <= v && v <= max;
    }
    public static boolean between(long min, long v, long max) {
        return min <= v && v <= max;
    }
    public static double lerp(double a, double b, double v) {
        return a + (b - a) * v;
    }
    public static Color colLerp(Color a, Color b, double v) {
        return new Color((int) lerp(a.getRed(), b.getRed(), v), (int) lerp(a.getGreen(), b.getGreen(), v), (int) lerp(a.getBlue(), b.getBlue(), v));
    }
}
