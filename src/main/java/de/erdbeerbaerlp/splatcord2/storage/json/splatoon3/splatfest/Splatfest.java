package de.erdbeerbaerlp.splatcord2.storage.json.splatoon3.splatfest;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Arrays;
import java.util.Base64;

public class Splatfest {
    public String id, state, startTime, endTime, title, lang;
    public SplatfestTeam[] teams;

    public long getStartTime() {
        return Instant.parse(startTime).toEpochMilli() / 1000;
    }

    public long getEndTime() {
        return Instant.parse(endTime).toEpochMilli() / 1000;
    }

    public int getSplatfestID() {
        return Integer.parseInt(new String(Base64.getDecoder().decode(id), StandardCharsets.UTF_8).split("-")[1]);
    }

    @Override
    public String toString() {
        return "Splatfest{" +
                "id='" + id + '\'' +
                ", state='" + state + '\'' +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", title='" + title + '\'' +
                ", lang='" + lang + '\'' +
                ", teams=" + Arrays.toString(teams) +
                '}';
    }
}