package de.erdbeerbaerlp.splatcord2.storage.json.splatoon3.loadoutink;

import java.awt.*;

public class Splashtag extends ImageNode {
    public SplashtagColor color;

    public static class SplashtagColor {
        public float r,g,b;

        public Color toColor() {
            return new Color(r, g, b);
        }

        @Override
        public String toString() {
            return "SplashtagColor{" +
                    ", b=" + b +
                    ", g=" + g +
                    ", r=" + r +
                    '}';
        }
    }
}
