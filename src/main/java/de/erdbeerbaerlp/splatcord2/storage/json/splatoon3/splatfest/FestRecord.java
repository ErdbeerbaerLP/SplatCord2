package de.erdbeerbaerlp.splatcord2.storage.json.splatoon3.splatfest;

import de.erdbeerbaerlp.splatcord2.storage.json.Image;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;

public class FestRecord {
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

    public int getSplatfestID() {
        return Integer.parseInt(new String(Base64.getDecoder().decode(id), StandardCharsets.UTF_8).split("-")[2]);
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
