package de.erdbeerbaerlp.splatcord2.storage.json.splatoon3.rotation;

import de.erdbeerbaerlp.splatcord2.util.ImageUtil;

import java.time.Instant;

public class Coop3 {
    public String startTime;
    public String endTime;

    public CoopSetting setting;
    public byte[] outImage = new byte[0];


    public void genImage() {
        outImage = ImageUtil.generateSRImage(this);
    }

    public long getStartTime() {
        return Instant.parse(startTime).toEpochMilli() / 1000;
    }

    public long getEndTime() {

        return Instant.parse(endTime).toEpochMilli() / 1000;
    }
}
