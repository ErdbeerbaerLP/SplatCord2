package de.erdbeerbaerlp.splatcord2.storage.json.splatoon3.splatfest;

import java.awt.*;

public class SplatfestColor {
    public float a,b,g,r;
    public Color toColor(){
        return new Color(r,g,b,a);
    }

    @Override
    public String toString() {
        return "SplatfestColor{" +
                "a=" + a +
                ", b=" + b +
                ", g=" + g +
                ", r=" + r +
                '}';
    }
}
