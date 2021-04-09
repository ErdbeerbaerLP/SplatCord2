package de.erdbeerbaerlp.splatcord2.storage.json.scheduling;

import java.util.Arrays;

public class Schedules {

    public Schedule[] regular = new Schedule[0];
    public Schedule[] gachi = new Schedule[0];
    public Schedule[] league = new Schedule[0];

    @Override
    public String toString() {
        return "Schedules{" +
                "regular=" + Arrays.toString(regular) +
                ", gachi=" + Arrays.toString(gachi) +
                ", league=" + Arrays.toString(league) +
                '}';
    }
}
