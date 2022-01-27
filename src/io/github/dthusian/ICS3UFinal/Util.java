package io.github.dthusian.ICS3UFinal;

import java.awt.*;

public class Util {
    // Returns true if v is between min and max
    public static boolean between(int min, int v, int max) {
        return min <= v && v <= max;
    }
    public static boolean between(long min, long v, long max) {
        return min <= v && v <= max;
    }

    // lerp == Linear Interpolation (look it up)
    public static double lerp(double a, double b, double v) {
        return a + (b - a) * v;
    }
    // lerp but for colors
    public static Color colLerp(Color a, Color b, double v) {
        return new Color((int) lerp(a.getRed(), b.getRed(), v), (int) lerp(a.getGreen(), b.getGreen(), v), (int) lerp(a.getBlue(), b.getBlue(), v));
    }
    // Constants related to the game
    public static class Timing {
        public static final long msMiss = 150;
        public static final long msBad = 100;
        public static final long msGood = 50;
        public static final long msPerfect = 30;
    }
    public static final Color colorMiss = new Color(120, 0, 0);
    public static final Color colorBad = new Color(120, 0, 0);
    public static final Color colorGood = new Color(30, 150, 200);
    public static final Color colorPerfect = new Color(200, 150, 30);
}
