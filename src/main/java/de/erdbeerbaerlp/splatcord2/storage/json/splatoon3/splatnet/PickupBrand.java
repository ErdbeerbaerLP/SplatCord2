package de.erdbeerbaerlp.splatcord2.storage.json.splatoon3.splatnet;

import de.erdbeerbaerlp.splatcord2.storage.json.Image;

import java.time.Instant;

public class PickupBrand {
    public Image image;
    public Brand3 brand;
    public String saleEndTime;
    public LimitedGear[] brandGears;
    public Brand3 nextBrand;

    public long getEndTime() {
        return Instant.parse(saleEndTime).toEpochMilli() / 1000;
    }
}
