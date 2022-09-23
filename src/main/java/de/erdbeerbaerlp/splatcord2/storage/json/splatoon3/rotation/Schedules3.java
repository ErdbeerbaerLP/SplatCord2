package de.erdbeerbaerlp.splatcord2.storage.json.splatoon3.rotation;

public class Schedules3 {
    public SchDat data;

    @Override
    public String toString() {
        return "Schedules3{" +
                "data=" + data +
                '}';
    }

    public static class SchDat{
        public SchedulesRoot regularSchedules = new SchedulesRoot();
        public SchedulesRoot bankaraSchedules = new SchedulesRoot();
        public SchedulesRoot xSchedules = new SchedulesRoot();
        public SchedulesRoot leagueSchedules = new SchedulesRoot();
        public SchedulesRoot festSchedules = new SchedulesRoot();

        public CoopRoot coopGroupingSchedule  = new CoopRoot();

        @Override
        public String toString() {
            return "SchDat{" +
                    "regularSchedules=" + regularSchedules +
                    '}';
        }
    }
}
