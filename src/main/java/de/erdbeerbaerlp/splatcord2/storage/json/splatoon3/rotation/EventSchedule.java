package de.erdbeerbaerlp.splatcord2.storage.json.splatoon3.rotation;

import java.util.Arrays;

public class EventSchedule {

    public EventMatchSetting leagueMatchSetting = new EventMatchSetting();
    public EventTimePeriod[] timePeriods;
    public static final class EventMatchSetting extends MatchSetting{
        public LeagueMatchEvent leagueMatchEvent = new LeagueMatchEvent();
    }

    @Override
    public String toString() {
        return "EventSchedule{" +
                "leagueMatchSetting=" + leagueMatchSetting +
                ", timePeriods=" + Arrays.toString(timePeriods) +
                '}';
    }
}
