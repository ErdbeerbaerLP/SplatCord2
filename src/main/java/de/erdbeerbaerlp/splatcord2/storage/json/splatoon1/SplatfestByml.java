package de.erdbeerbaerlp.splatcord2.storage.json.splatoon1;

import java.time.Instant;
import java.util.Arrays;
import java.util.HashMap;

public class SplatfestByml {
    public RotationBymlRoot root = new RotationBymlRoot();
    public String image = null;

    @Override
    public String toString() {
        return "Byml{" +
                "root=" + root +
                '}';
    }

    public static class RotationBymlRoot {
        public BymlEntry Rule;
        public MapObj[] Stages = new MapObj[0];
        public TeamObj[] Teams = new TeamObj[0];
        public TimeObj Time = new TimeObj();
        public BymlEntry FestivalId;

        public static class MapObj {
            public BymlEntry MapID;

            @Override
            public String toString() {
                return "MapObj{" +
                        "MapID=" + MapID +
                        '}';
            }
        }

        public static class TeamObj {
            public BymlEntry Color;
            public HashMap<String, BymlEntry> Name, ShortName;

            @Override
            public String toString() {
                return "TeamObj{" +
                        "Color=" + Color +
                        ", Name=" + Name +
                        ", ShortName=" + ShortName +
                        '}';
            }
        }

        public static class TranslatableObj {
            public BymlEntry EUde, EUen, EUes, EUfr, EUit, JPja, USen, USes, USfr;

            @Override
            public String toString() {
                return "TranslatableObj{" +
                        "EUde=" + EUde +
                        ", EUen=" + EUen +
                        ", EUes=" + EUes +
                        ", EUfr=" + EUfr +
                        ", EUit=" + EUit +
                        ", JPja=" + JPja +
                        ", USen=" + USen +
                        ", USes=" + USes +
                        ", USfr=" + USfr +
                        '}';
            }
        }

        public static class TimeObj {
            public BymlEntry Announce, Start, End, Result;

            private long getTime(String stamp){
                return Instant.parse(stamp).toEpochMilli() / 1000;
            }

            public long getStartTime(){
                return getTime(Start.value);
            }
            public long getAnnounceTime(){
                return getTime(Announce.value);
            }public long getResultTime(){
                return getTime(Result.value);
            }
            public long getEndTime(){
                return getTime(End.value);
            }

            @Override
            public String toString() {
                return "TimeObj{" +
                        "Announce=" + Announce +
                        ", Start=" + Start +
                        ", End=" + End +
                        ", Result=" + Result +
                        '}';
            }
        }

        @Override
        public String toString() {
            return "RotationBymlRoot{" +
                    "Rule=" + Rule +
                    ", Stages=" + Arrays.toString(Stages) +
                    ", Teams=" + Arrays.toString(Teams) +
                    ", Time=" + Time +
                    ", FestivalId=" + FestivalId +
                    '}';
        }

        public static class BymlEntry{
            public String value;

            @Override
            public String toString() {
                return "BymlEntry{" +
                        "value='" + value + '\'' +
                        '}';
            }
        }
    }

}
