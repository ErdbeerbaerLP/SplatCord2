package de.erdbeerbaerlp.splatcord2.storage.json.splatoon3.splatfest;

import java.util.Arrays;

public class SFRoot {
    public SplatfestRoot US;
    public SplatfestRoot EU;
    public SplatfestRoot JP;

    @Override
    public String toString() {
        return "SFRoot{" +
                "US=" + US +
                ", EU=" + EU +
                ", JP=" + JP +
                '}';
    }

    private class SplatfestRoot {
        public SplatfestData data;

        @Override
        public String toString() {
            return "SplatfestRoot{" +
                    "data=" + data +
                    '}';
        }
    }
    private class SplatfestData {
        public SplatfestRecords festRecords;

        @Override
        public String toString() {
            return "SplatfestData{" +
                    "festRecords=" + festRecords +
                    '}';
        }
    }


    private class SplatfestRecords {
        public Splatfest[] nodes;

        @Override
        public String toString() {
            return "SplatfestData{" +
                    "nodes=" + Arrays.toString(nodes) +
                    '}';
        }
    }


}
