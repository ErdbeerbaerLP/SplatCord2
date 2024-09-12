package de.erdbeerbaerlp.splatcord2.storage.json.splatoon3.rotation;

import java.time.Instant;

public class CurrentSplatfest {
    public String id, title, startTime, endTime, midtermTime, state;
    public Stage tricolorStage;
    public Schedule3[] timetable;

    public long getStartTime() {
        return Instant.parse(startTime).toEpochMilli() / 1000;
    }

    public long getEndTime() {

        return Instant.parse(endTime).toEpochMilli() / 1000;
    }

    public long getMidtermTime() {
        return Instant.parse(midtermTime).toEpochMilli() / 1000;
    }

}
