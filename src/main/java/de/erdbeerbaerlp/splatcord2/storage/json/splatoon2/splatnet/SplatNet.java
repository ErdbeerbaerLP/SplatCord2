package de.erdbeerbaerlp.splatcord2.storage.json.splatoon2.splatnet;

import java.util.Arrays;

public class SplatNet {
    public Merchandise[] merchandises;

    @Override
    public String toString() {
        return "TentaWorld{" +
                "merchandises=" + Arrays.toString(merchandises) +
                '}';
    }
}
