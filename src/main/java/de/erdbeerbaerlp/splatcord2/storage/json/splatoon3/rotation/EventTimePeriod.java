package de.erdbeerbaerlp.splatcord2.storage.json.splatoon3.rotation;

import java.time.Instant;

public class EventTimePeriod {
    public String startTime;
    public String endTime;
    public long getStartTime() {
        return Instant.parse(startTime).toEpochMilli() / 1000;
    }

    public long getEndTime() {

        return Instant.parse(endTime).toEpochMilli() / 1000;
    }
}
