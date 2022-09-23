package de.erdbeerbaerlp.splatcord2.storage.json.splatoon3.rotation;

import java.util.Arrays;

public class MatchSetting {
    public Rule vsRule;
    public Stage[] vsStages;

    @Override
    public String toString() {
        return "MatchSetting{" +
                "vsRule=" + vsRule +
                ", vsStages=" + Arrays.toString(vsStages) +
                '}';
    }
}
