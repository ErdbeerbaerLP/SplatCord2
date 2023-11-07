package de.erdbeerbaerlp.splatcord2.storage.json.splatoon1;

import java.util.Arrays;

public class RotationByml {
    public RotationBymlRoot root = new RotationBymlRoot();

    @Override
    public String toString() {
        return "Byml{" +
                "root=" + root +
                '}';
    }

    public static class RotationBymlRoot {
        public Phase[] Phases = new Phase[0];

        public DateTimeObj DateTime = new DateTimeObj();

        @Override
        public String toString() {
            return "BymlRoot{" +
                    "Phases=" + Arrays.toString(Phases) +
                    '}';
        }

        public static class DateTimeObj {
            public String value = "";
        }
    }

}
