package de.erdbeerbaerlp.splatcord2.tasks;

import com.google.gson.JsonParseException;
import de.erdbeerbaerlp.splatcord2.Main;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon2.splatnet.SplatNet;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon3.splatnet.SplatNet3;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon3.translations.S3Locale;
import de.erdbeerbaerlp.splatcord2.util.ScheduleUtil;
import de.erdbeerbaerlp.splatcord2.util.wiiu.BossFileUtil;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.TimerTask;

public class DataUpdateTask extends TimerTask {
    private boolean currentlyRunning = false;

    @Override
    public void run() {
        System.out.println("Starting data update...");
        if(currentlyRunning){
            System.out.println("Skipping due to already running...");
            return;
        }
        currentlyRunning = true;
        try {
            ScheduleUtil.updateS2RotationData();
            Main.splatoon2inkStatus = true;
        } catch (IOException | JsonParseException e) {
            e.printStackTrace();
            Main.splatoon2inkStatus = false;
        }
        try {
            ScheduleUtil.updateS3RotationData();
            ScheduleUtil.updateSpl3Fests();
            Main.splatoon3inkStatus = true;
        } catch (IOException | JsonParseException e) {
            e.printStackTrace();
            Main.splatoon3inkStatus = false;
        }

        try {
            Main.s1rotations = BossFileUtil.getStageByml("https://npts.app.nintendo.net/p01/tasksheet/1/zvGSM4kOrXpkKnpT/schdat2?c=EU&l=en");
            Main.splatoon1Status = true;
        } catch (Exception e) {
            System.err.println("Failed loading splatoon 1 rotations!");
            Main.splatoon1Status = false;
            e.printStackTrace();
        }
        try {
            Main.s1rotationsPretendo = BossFileUtil.getStageByml("https://npts.app.pretendo.cc/p01/tasksheet/1/zvGSM4kOrXpkKnpT/schdat2?c=EU&l=en");
            Main.splatoon1PretendoStatus = true;
            Main.s1splatfestPretendo = BossFileUtil.getFestBymlDirect("https://cdn.discordapp.com/attachments/911878047895023637/1320359116123934720/00000544?ex=67694fc0&is=6767fe40&hm=8670a1c8ea6c87be64f674b014bfa3fc34fc2b696b5bb3884c2c435d0e4583e7&", false);
            //Main.s1splatfestPretendo = BossFileUtil.getFestByml("https://npts.app.pretendo.cc/p01/tasksheet/1/zvGSM4kOrXpkKnpT/optdat2?c=EU&l=en");
            Main.s1splatfestSplatfestival = BossFileUtil.getFestBymlDirect("https://github.com/Sheldon10095/Splatfestival_StaffFiles/raw/main/FestFiles/00000544",false);
        } catch (Exception e) {
            System.err.println("Failed loading splatoon 1 rotations from pretendo!");
            e.printStackTrace();

            Main.splatoon1PretendoStatus = false;
        }
        try {
            final URL tworld2 = new URL("https://splatoon2.ink/data/merchandises.json");
            final HttpsURLConnection twcon2 = (HttpsURLConnection) tworld2.openConnection();
            twcon2.setRequestProperty("User-Agent", Main.USER_AGENT);
            twcon2.connect();
            Main.splatNet2 = Main.gson.fromJson(new InputStreamReader(twcon2.getInputStream()), SplatNet.class);
            final URL tworld3 = new URL("https://splatoon3.ink/data/gear.json");
            final HttpsURLConnection conn3 = (HttpsURLConnection) tworld3.openConnection();
            conn3.setRequestProperty("User-Agent", Main.USER_AGENT);
            conn3.connect();
            Main.splatNet3 = Main.gson.fromJson(new InputStreamReader(conn3.getInputStream()), SplatNet3.class);
            System.out.println("Refreshing S3 translations...");
            Main.translations.forEach((l, locale) -> {
                try {
                    final URL lng3 = new URL("https://splatoon3.ink/data/locale/" + l.s3Key + ".json");
                    final HttpsURLConnection s3Conn = (HttpsURLConnection) lng3.openConnection();
                    s3Conn.setRequestProperty("User-Agent", Main.USER_AGENT);
                    s3Conn.connect();
                    String o3 = new Scanner(s3Conn.getInputStream(), StandardCharsets.UTF_8).useDelimiter("\\A").next();
                    locale.s3locales = Main.gson.fromJson(new StringReader(o3), S3Locale.class);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        currentlyRunning = false;
    }

}
