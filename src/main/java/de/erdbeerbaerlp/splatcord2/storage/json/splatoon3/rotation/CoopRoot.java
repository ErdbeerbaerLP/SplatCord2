package de.erdbeerbaerlp.splatcord2.storage.json.splatoon3.rotation;

public class CoopRoot {
    public CoopRegular regularSchedules;
    public CoopRegular bigRunSchedules;
    public CoopRegular teamContestSchedules;

    public static class CoopRegular {
        public Coop3[] nodes;
    }
}
