package de.erdbeerbaerlp.splatcord2.storage.json.tentaworld;

import java.util.Arrays;

public class TentaWorld {
    public Merchandise[] merchandises;

    @Override
    public String toString() {
        return "TentaWorld{" +
                "merchandises=" + Arrays.toString(merchandises) +
                '}';
    }
}
