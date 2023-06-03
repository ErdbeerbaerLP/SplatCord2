package de.erdbeerbaerlp.splatcord2.storage.json.splatoon3.rotation;

public class EventSchedule {

    public EventMatchSetting leagueMatchSetting = new EventMatchSetting();
    public EventTimePeriod[] timePeriods;
    public static final class EventMatchSetting extends MatchSetting{
        public LeagueMatchEvent leagueMatchEvent = new LeagueMatchEvent();
    }
}
