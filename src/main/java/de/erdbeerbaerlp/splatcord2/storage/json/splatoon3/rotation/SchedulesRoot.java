package de.erdbeerbaerlp.splatcord2.storage.json.splatoon3.rotation;

import java.util.Arrays;

public class SchedulesRoot {
    public Schedule3[] nodes;

    @Override
    public String toString() {
        return "SchedulesRoot{" +
                "nodes=" + Arrays.toString(nodes) +
                '}';
    }
}
