package de.erdbeerbaerlp.splatcord2.storage.json.coop_schedules;

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
