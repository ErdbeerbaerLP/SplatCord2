package de.erdbeerbaerlp.splatcord2.util;

import com.google.gson.JsonParseException;
import de.erdbeerbaerlp.splatcord2.Main;
import de.erdbeerbaerlp.splatcord2.storage.Rotation;
import de.erdbeerbaerlp.splatcord2.storage.S3Rotation;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon2.coop_schedules.CoOpSchedules;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon2.scheduling.Schedule;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon2.scheduling.Schedules;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon3.Schedule3;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon3.Schedules3;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class ScheduleUtil {

    private static Schedules schedules;
    private static Schedules3 schedules3;

    private static Rotation getRotationForTimestamp(long timestamp){
        Schedule regular = null, ranked = null, league = null;
        for (Schedule s : schedules.regular) {
            if (s.start_time<= timestamp && s.end_time > timestamp) {
                regular = s;
                break;
            }
        }
        for (Schedule s : schedules.gachi) {
            if (s.start_time<= timestamp && s.end_time > timestamp) {
                ranked = s;
                break;
            }
        }
        for (Schedule s : schedules.league) {
            if (s.start_time<= timestamp && s.end_time > timestamp) {
                league = s;
                break;
            }
        }

        return new Rotation(regular, ranked, league);
    }
    private static S3Rotation getS3RotationForTimestamp(long timestamp){
        Schedule3 regular = null, bankara = null, xBattle = null;

        for (Schedule3 s : schedules3.data.regularSchedules.nodes) {
            if (s.getStartTime()<= timestamp && s.getEndTime() > timestamp) {
                regular = s;
                break;
            }
        }
        for (Schedule3 s : schedules3.data.bankaraSchedules.nodes) {
            if (s.getStartTime()<= timestamp && s.getEndTime() > timestamp) {
                bankara = s;
                break;
            }
        }
        for (Schedule3 s :schedules3.data.xSchedules.nodes)  {
            if (s.getStartTime()<= timestamp && s.getEndTime() > timestamp) {
                xBattle = s;
                break;
            }
        }

        return new S3Rotation(regular, bankara, xBattle);
    }
    public static Rotation getCurrentRotation() {
        return getRotationForTimestamp(System.currentTimeMillis()/1000);
    }
    public static S3Rotation getCurrentS3Rotation() {
        return getS3RotationForTimestamp(System.currentTimeMillis()/1000);
    }

    public static ArrayList<Rotation> getNext3Rotations() {
        final ArrayList<Rotation> rotations = new ArrayList<>();
        long time = System.currentTimeMillis()/1000;

        for(int i=0;i<3;i++){
            time += TimeUnit.HOURS.toSeconds(2);
            rotations.add(getRotationForTimestamp(time));
        }
        return rotations;
    }
    public static ArrayList<S3Rotation> getS3Next3Rotations() {
        final ArrayList<S3Rotation> rotations = new ArrayList<>();
        long time = System.currentTimeMillis()/1000;

        for(int i=0;i<3;i++){
            time += TimeUnit.HOURS.toSeconds(2);
            rotations.add(getS3RotationForTimestamp(time));
        }
        return rotations;
    }

    public static void updateRotationData() throws IOException, JsonParseException {
        final URL sched = new URL("https://splatoon2.ink/data/schedules.json");
        final HttpsURLConnection deConn = (HttpsURLConnection) sched.openConnection();
        deConn.setRequestProperty("User-Agent", Main.USER_AGENT);
        deConn.connect();
        schedules = Main.gson.fromJson(new InputStreamReader(deConn.getInputStream()), Schedules.class);
        final URL sched3 = new URL("https://splatoon3.ink/data/schedules.json");
        final HttpsURLConnection deConn3 = (HttpsURLConnection) sched3.openConnection();
        deConn3.setRequestProperty("User-Agent", Main.USER_AGENT);
        deConn3.connect();
        schedules3 = Main.gson.fromJson(new InputStreamReader(deConn3.getInputStream()), Schedules3.class);

        System.out.println(schedules3);
        final URL sched2 = new URL("https://splatoon2.ink/data/coop-schedules.json");
        final HttpsURLConnection deConn2 = (HttpsURLConnection) sched2.openConnection();
        deConn2.setRequestProperty("User-Agent", Main.USER_AGENT);
        deConn2.connect();
        Main.coop_schedules = Main.gson.fromJson(new InputStreamReader(deConn2.getInputStream()), CoOpSchedules.class);
    }


    public static String getSchedulesString() {
        return schedules.toString();
    }
}
