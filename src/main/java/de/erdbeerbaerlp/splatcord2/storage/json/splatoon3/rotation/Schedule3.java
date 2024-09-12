package de.erdbeerbaerlp.splatcord2.storage.json.splatoon3.rotation;

import java.time.Instant;

public class Schedule3 {
    public String startTime;
    public String endTime;
    public MatchSetting regularMatchSetting;
    public MatchSetting xMatchSetting;
    public MatchSetting[] festMatchSettings;
    public MatchSetting[] bankaraMatchSettings;

    public long getStartTime() {
        return Instant.parse(startTime).toEpochMilli() / 1000;
    }

    public long getEndTime() {

        return Instant.parse(endTime).toEpochMilli() / 1000;
    }

    public MatchSetting getRegularSFMatch(){
        if(festMatchSettings == null) return null;
        for (MatchSetting f : festMatchSettings) {
            if(f.festMode.equals("REGULAR")) return f;
        }
        return null;
    }
    public MatchSetting getProSFMatch(){
        if(festMatchSettings == null) return null;
        for (MatchSetting f : festMatchSettings) {
            if(f.festMode.equals("CHALLENGE")) return f;
        }
        return null;
    }
}
