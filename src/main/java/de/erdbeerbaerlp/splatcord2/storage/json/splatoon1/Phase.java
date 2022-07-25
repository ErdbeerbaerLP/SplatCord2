package de.erdbeerbaerlp.splatcord2.storage.json.splatoon1;

import java.util.Arrays;

public class Phase {
    public static class GachiRule {
        public String value = "cUnk";

        @Override
        public String toString() {
            return "GachiRule{" +
                    "value='" + value + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "Phase{" +
                "GachiStages=" + Arrays.toString(GachiStages) +
                ", RegularStages=" + Arrays.toString(RegularStages) +
                '}';
    }
    public GachiRule GachiRule = new GachiRule();
    public Stage[] GachiStages = new Stage[0];
    public Stage[] RegularStages = new Stage[0];
    public static class Stage {
        public MapID MapID = new MapID();

        @Override
        public String toString() {
            return "Stage{" +
                    "MapID=" + MapID +
                    '}';
        }

        public static class MapID {
            public int value = -1;

            @Override
            public String toString() {
                return "MapID{" +
                        "value=" + value +
                        '}';
            }
        }
    }
}
