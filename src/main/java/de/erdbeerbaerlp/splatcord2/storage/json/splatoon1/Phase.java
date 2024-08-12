package de.erdbeerbaerlp.splatcord2.storage.json.splatoon1;

import java.util.Arrays;

public class Phase {
    public GachiRule GachiRule = new GachiRule();
    public Stage[] GachiStages = new Stage[0];
    public Stage[] RegularStages = new Stage[0];
    public SplatfestByml.RotationBymlRoot.BymlEntry Time;
    public String image = null;

    public int getTime() {
        return Integer.parseInt(Time.value);
    }

    @Override
    public String toString() {
        return "Phase{" +
                "GachiStages=" + Arrays.toString(GachiStages) +
                ", RegularStages=" + Arrays.toString(RegularStages) +
                '}';
    }

    public static class GachiRule {
        public String value = "cUnk";

        @Override
        public String toString() {
            return "GachiRule{" +
                    "value='" + value + '\'' +
                    '}';
        }
    }

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
