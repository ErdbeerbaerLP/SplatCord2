package de.erdbeerbaerlp.splatcord2.storage.json.splatoon3.splatfest;

import de.erdbeerbaerlp.splatcord2.storage.json.Image;

import java.time.Instant;

public class FestRecord {
    public String __splatoon3ink_id;
    public String id;
    public String state;
    public String startTime;
    public String endTime;
    public String title;
    public String lang;
    public Image image;
    public SplatfestTeam[] teams;
    public boolean isVotable;

    public long getStartTime() {
        return Instant.parse(startTime).toEpochMilli() / 1000;
    }

    public long getEndTime() {
        return Instant.parse(endTime).toEpochMilli() / 1000;
    }

    public String getSplatfestID() {
        return __splatoon3ink_id;
    }

    public SplatfestTeam getWinningTeam() {
        for (SplatfestTeam t : teams) {
            if (t.result != null) {
                if (t.result.isWinner) return t;
            }
        }
        return null;
    }
}
