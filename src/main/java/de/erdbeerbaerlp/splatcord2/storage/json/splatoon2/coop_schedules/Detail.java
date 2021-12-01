package de.erdbeerbaerlp.splatcord2.storage.json.splatoon2.coop_schedules;

import de.erdbeerbaerlp.splatcord2.storage.json.splatoon2.scheduling.Stage;

import java.util.Arrays;

public class Detail {
    public long end_time, start_time;
    public Weapons[] weapons;
    public Stage stage;

    @Override
    public String toString() {
        return "Details{" +
                "end_time=" + end_time +
                ", start_time=" + start_time +
                ", weapons=" + Arrays.toString(weapons) +
                ", stage=" + stage +
                '}';
    }
}
