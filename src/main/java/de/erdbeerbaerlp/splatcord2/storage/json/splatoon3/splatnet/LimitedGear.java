package de.erdbeerbaerlp.splatcord2.storage.json.splatoon3.splatnet;

import java.time.Instant;

public class LimitedGear {
    public String id;
    public String saleEndTime;
    public String price;
    public Gear3 gear;

    public long getEndTime() {
        return Instant.parse(saleEndTime).toEpochMilli() / 1000;
    }
}
