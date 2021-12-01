package de.erdbeerbaerlp.splatcord2.storage.json.splatoon2.coop_schedules;

import java.util.Arrays;

public class CoOpSchedules {
    public Detail[] details;

    @Override
    public String toString() {
        return "CoOpSchedules{" +
                "details=" + Arrays.toString(details) +
                '}';
    }
}
