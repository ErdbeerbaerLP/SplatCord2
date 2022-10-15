package de.erdbeerbaerlp.splatcord2.storage.json.splatoon3.splatfest;

import java.util.Arrays;

public class SplatfestRegion {
    public RegionData data;

    @Override
    public String toString() {
        return "SplatfestRegion{" +
                "data=" + data +
                '}';
    }

    public static class RegionData {
        public FestRecordRoot festRecords;

        @Override
        public String toString() {
            return "RegionData{" +
                    "festRecords=" + festRecords +
                    '}';
        }
    }

    public static class FestRecordRoot {
        public FestRecord[] nodes;

        @Override
        public String toString() {
            return "FestRecordRoot{" +
                    "nodes=" + Arrays.toString(nodes) +
                    '}';
        }
    }
}
