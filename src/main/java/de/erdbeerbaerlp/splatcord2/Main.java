package de.erdbeerbaerlp.splatcord2;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import de.erdbeerbaerlp.splatcord2.dc.Bot;
import de.erdbeerbaerlp.splatcord2.storage.BotLanguage;
import de.erdbeerbaerlp.splatcord2.storage.Config;
import de.erdbeerbaerlp.splatcord2.storage.SplatProfile;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon1.Byml;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon2.coop_schedules.CoOpSchedules;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon2.splatnet.SplatNet;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon2.translations.Locale;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon2.weapons.Weapon;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon3.splatnet.SplatNet3;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon3.translations.S3Locale;
import de.erdbeerbaerlp.splatcord2.storage.sql.DatabaseInterface;
import de.erdbeerbaerlp.splatcord2.threads.DataUpdateThread;
import de.erdbeerbaerlp.splatcord2.threads.RotationThread;
import de.erdbeerbaerlp.splatcord2.threads.SalmonrunThread;
import de.erdbeerbaerlp.splatcord2.threads.SplatnetOrderThread;
import de.erdbeerbaerlp.splatcord2.util.ScheduleUtil;
import de.erdbeerbaerlp.splatcord2.util.wiiu.BossFileUtil;
import net.dv8tion.jda.api.entities.User;

import javax.net.ssl.HttpsURLConnection;
import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.lang.reflect.Type;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Main {

    public static final String USER_AGENT = "SplatCord 2 (https://discord.gg/DBH9FSFCXb)";
    public static final HashMap<BotLanguage, Locale> translations = new HashMap<>();
    public static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static final Map<Long, User> userCache = new HashMap<>();
    private static final HashMap<Long, SplatProfile> userProfileCache = new HashMap<>();
    public static DatabaseInterface iface = null;
    public static Bot bot = null;
    public static Instant startTime = null;
    public static SplatNet splatNet2 = null;
    public static SplatNet3 splatNet3 = null;
    public static Byml s1rotations = null;
    public static boolean splatoon2inkStatus = false;
    public static boolean splatoon3inkStatus = false;
    public static CoOpSchedules coop_schedules;
    public static Map<String, Weapon> weaponData = new HashMap<>();

    public static void main(String[] args) throws Exception {
        Config.instance().loadConfig();
        try {
            iface = new DatabaseInterface();
        } catch (SQLException e) {
            System.err.println("Database connection Failed!");
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            System.out.println("Cannot load SQL Driver...");
            e.printStackTrace();
        }
        if (iface == null) return;
        System.out.println("Downloading Splat1 rotation data");

        try {
            s1rotations = BossFileUtil.getStageByml();
        } catch (Exception e) {
            System.err.println("Failed loading Splatoon 1 rotations!");
            e.printStackTrace();
        }


        System.out.println("Downloading locales");
        for (BotLanguage l : BotLanguage.values()) {
            final URL lng = new URL("https://splatoon2.ink/data/locale/" + l.key + ".json");
            final HttpsURLConnection deConn = (HttpsURLConnection) lng.openConnection();
            deConn.setRequestProperty("User-Agent", USER_AGENT);
            deConn.connect();
            String out = new Scanner(deConn.getInputStream(), StandardCharsets.UTF_8).useDelimiter("\\A").next();
            final Locale locale = gson.fromJson(new StringReader(out), Locale.class);
            final URL lng3 = new URL("https://splatoon3.ink/data/locale/" + l.s3Key + ".json");
            final HttpsURLConnection s3Conn = (HttpsURLConnection) lng3.openConnection();
            s3Conn.setRequestProperty("User-Agent", USER_AGENT);
            s3Conn.connect();
            String o3 = new Scanner(s3Conn.getInputStream(), StandardCharsets.UTF_8).useDelimiter("\\A").next();
            locale.s3locales = gson.fromJson(new StringReader(o3), S3Locale.class);
            locale.botLocale = l.botLocale;
            locale.allGears = new HashMap<>();
            locale.init();
            //System.out.println(locale);
            translations.put(l, locale);

        }
        System.out.println("Downloading Weapon data");
        final URL wpn = new URL("https://splatoon2.ink/data/weapons.json");
        final HttpsURLConnection wpnConn = (HttpsURLConnection) wpn.openConnection();
        wpnConn.setRequestProperty("User-Agent", USER_AGENT);
        wpnConn.connect();
        final String out = new Scanner(wpnConn.getInputStream(), StandardCharsets.UTF_8).useDelimiter("\\A").next();
        final Type mapType = new TypeToken<Map<String, Weapon>>() {
        }.getType();
        weaponData = gson.fromJson(new StringReader(out), mapType);

        System.out.println("Downloading SplatNet2 data");
        final URL tworld = new URL("https://splatoon2.ink/data/merchandises.json");
        final HttpsURLConnection con = (HttpsURLConnection) tworld.openConnection();
        con.setRequestProperty("User-Agent", Main.USER_AGENT);
        con.connect();
        splatNet2 = Main.gson.fromJson(new InputStreamReader(con.getInputStream()), SplatNet.class);

        System.out.println("Downloading SplatNet3 data");
        final URL tw3 = new URL("https://splatoon3.ink/data/gear.json");
        final HttpsURLConnection con3 = (HttpsURLConnection) tw3.openConnection();
        con3.setRequestProperty("User-Agent", Main.USER_AGENT);
        con3.connect();
        splatNet3 = Main.gson.fromJson(new InputStreamReader(con3.getInputStream()), SplatNet3.class);


        try {
            ScheduleUtil.updateS2RotationData();
            splatoon2inkStatus = true;
        } catch (IOException | JsonParseException e) {
            splatoon2inkStatus = false;
            e.printStackTrace();
        }
        try {
            ScheduleUtil.updateS3RotationData();
            splatoon3inkStatus = true;
        } catch (IOException | JsonParseException e) {
            splatoon3inkStatus = false;
            e.printStackTrace();
        }
        try {
            bot = new Bot();
        } catch (LoginException e) {
            System.err.println("Cannot login to discord!");
            e.printStackTrace();
        }
        if (bot == null) return;
        startTime = Instant.now();

        final DataUpdateThread dataUpdateThread = new DataUpdateThread();
        dataUpdateThread.start();
        final RotationThread rotationThread = new RotationThread();
        rotationThread.start();
        final SalmonrunThread salmonrunThread = new SalmonrunThread();
        salmonrunThread.start();
        final SplatnetOrderThread splatnetOrderThread = new SplatnetOrderThread();
        splatnetOrderThread.start();
    }

    public static User getUserById(Long userid) {
        if (userCache.containsKey(userid)) return userCache.get(userid);
        else {
            System.out.println("getting uncached user " + userid);
            final User out = bot.jda.retrieveUserById(userid).complete();
            System.out.println("retrieved user " + userid);
            userCache.put(userid, out);
            return out;
        }
    }

    public static SplatProfile getUserProfile(Long userid) {
        if (userProfileCache.containsKey(userid)) return userProfileCache.get(userid);
        else {
            System.out.println("getting uncached user profile " + userid);
            final SplatProfile out = iface.getSplatoonProfiles(userid);
            System.out.println("retrieved user profile for " + userid);
            userProfileCache.put(userid, out);
            return out;
        }
    }

    public static User getUserById(String userid) {
        return getUserById(Long.parseLong(userid));
    }
}