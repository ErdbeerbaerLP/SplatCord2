package de.erdbeerbaerlp.splatcord2.util;

import com.google.gson.JsonParseException;
import de.erdbeerbaerlp.splatcord2.Main;
import de.erdbeerbaerlp.splatcord2.storage.Rotation;
import de.erdbeerbaerlp.splatcord2.storage.S3Rotation;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon2.coop_schedules.CoOpSchedules;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon2.scheduling.Schedule;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon2.scheduling.Schedules;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon3.rotation.Coop3;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon3.rotation.Schedule3;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon3.rotation.Schedules3;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon3.splatfest.FestRecord;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon3.splatfest.SplatfestData;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class ScheduleUtil {

    private static Schedules schedules;
    private static Schedules3 schedules3;
    private static SplatfestData sf3;

    public static Rotation getS2RotationForTimestamp(long timestamp) {
        Schedule regular = null, ranked = null, league = null;
        for (Schedule s : schedules.regular) {
            if (s.start_time <= timestamp && s.end_time > timestamp) {
                regular = s;
                break;
            }
        }
        for (Schedule s : schedules.gachi) {
            if (s.start_time <= timestamp && s.end_time > timestamp) {
                ranked = s;
                break;
            }
        }
        for (Schedule s : schedules.league) {
            if (s.start_time <= timestamp && s.end_time > timestamp) {
                league = s;
                break;
            }
        }

        return new Rotation(regular, ranked, league);
    }

    public static S3Rotation getS3RotationForTimestamp(long timestamp) {
        Schedule3 regular = null, bankara = null, xBattle = null, fest = null;
        Coop3 coop = null;

        for (Schedule3 s : schedules3.data.regularSchedules.nodes) {
            if (s.getStartTime() <= timestamp && s.getEndTime() > timestamp) {
                regular = s;
                break;
            }
        }
        for (Schedule3 s : schedules3.data.bankaraSchedules.nodes) {
            if (s.getStartTime() <= timestamp && s.getEndTime() > timestamp) {
                bankara = s;
                break;
            }
        }
        for (Schedule3 s : schedules3.data.xSchedules.nodes) {
            if (s.getStartTime() <= timestamp && s.getEndTime() > timestamp) {
                xBattle = s;
                break;
            }
        }

        for (Coop3 s : schedules3.data.coopGroupingSchedule.regularSchedules.nodes) {
            if (s.getStartTime() <= timestamp && s.getEndTime() > timestamp) {
                coop = s;
                break;
            }
        }
        for (Schedule3 s : schedules3.data.festSchedules.nodes) {
            if (s.getStartTime() <= timestamp && s.getEndTime() > timestamp) {
                fest = s;
                break;
            }
        }

        return new S3Rotation(regular, bankara, xBattle, coop, fest, schedules3.data.currentFest);
    }

    public static Rotation getCurrentRotation() {
        return getS2RotationForTimestamp(System.currentTimeMillis() / 1000);
    }

    public static S3Rotation getCurrentS3Rotation() {
        return getS3RotationForTimestamp(System.currentTimeMillis() / 1000);
    }

    public static Coop3 getNextS3Coop() {
        long time = System.currentTimeMillis() / 1000;
        boolean next = false;
        for (Coop3 s : schedules3.data.coopGroupingSchedule.regularSchedules.nodes) {
            if (s.getStartTime() <= time && s.getEndTime() > time) {
                next = true;
                continue;
            }
            if (next)
                return s;
        }
        return null;
    }

    public static ArrayList<Rotation> getNext3Rotations() {
        final ArrayList<Rotation> rotations = new ArrayList<>();
        long time = System.currentTimeMillis() / 1000;

        for (int i = 0; i < 3; i++) {
            time += TimeUnit.HOURS.toSeconds(2);
            rotations.add(getS2RotationForTimestamp(time));
        }
        return rotations;
    }

    public static ArrayList<S3Rotation> getS3Next3Rotations() {
        final ArrayList<S3Rotation> rotations = new ArrayList<>();
        long time = System.currentTimeMillis() / 1000;

        for (int i = 0; i < 3; i++) {
            time += TimeUnit.HOURS.toSeconds(2) + 1;
            rotations.add(getS3RotationForTimestamp(time));
        }
        return rotations;
    }

    public static void updateS2RotationData() throws IOException, JsonParseException {
        System.out.println("Downloading S2 Rotations...");
        final URL sched = new URL("https://splatoon2.ink/data/schedules.json");
        final HttpsURLConnection deConn = (HttpsURLConnection) sched.openConnection();
        deConn.setRequestProperty("User-Agent", Main.USER_AGENT);
        deConn.connect();
        final URL sched2 = new URL("https://splatoon2.ink/data/coop-schedules.json");
        final HttpsURLConnection deConn2 = (HttpsURLConnection) sched2.openConnection();
        deConn2.setRequestProperty("User-Agent", Main.USER_AGENT);
        deConn2.connect();
        schedules = Main.gson.fromJson(new InputStreamReader(deConn.getInputStream()), Schedules.class);
        Main.coop_schedules = Main.gson.fromJson(new InputStreamReader(deConn2.getInputStream()), CoOpSchedules.class);


    }

    public static void updateS3RotationData() throws IOException, JsonParseException {
        System.out.println("Downloading S3 Rotations...");
        final URL sched3 = new URL("https://splatoon3.ink/data/schedules.json");
        final HttpsURLConnection deConn3 = (HttpsURLConnection) sched3.openConnection();
        deConn3.setRequestProperty("User-Agent", Main.USER_AGENT);
        deConn3.connect();
        schedules3 = Main.gson.fromJson(new InputStreamReader(deConn3.getInputStream()), Schedules3.class);
        final URL sfSched = new URL("https://splatoon3.ink/data/festivals.json");
        final HttpsURLConnection deConnSF = (HttpsURLConnection) sfSched.openConnection();
        deConnSF.setRequestProperty("User-Agent", Main.USER_AGENT);
        deConnSF.connect();
        sf3 = Main.gson.fromJson(new InputStreamReader(deConnSF.getInputStream()), SplatfestData.class);

        System.out.println("S3 Rotations updated successfully");
    }


    public static SplatfestData getSplatfestData() {
        return sf3;
    }

    public static FestRecord getSplatfestByID(int id) {
        for (FestRecord f : sf3.US.data.festRecords.nodes) {
            if (id == f.getSplatfestID()) return f;
        }
        for (FestRecord f : sf3.EU.data.festRecords.nodes) {
            if (id == f.getSplatfestID()) return f;
        }
        for (FestRecord f : sf3.AP.data.festRecords.nodes) {
            if (id == f.getSplatfestID()) return f;
        }
        for (FestRecord f : sf3.JP.data.festRecords.nodes) {
            if (id == f.getSplatfestID()) return f;
        }
        return null;
    }
}
