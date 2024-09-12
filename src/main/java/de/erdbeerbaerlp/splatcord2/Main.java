package de.erdbeerbaerlp.splatcord2;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import de.erdbeerbaerlp.splatcord2.dc.Bot;
import de.erdbeerbaerlp.splatcord2.storage.BotLanguage;
import de.erdbeerbaerlp.splatcord2.storage.CommandRegistry;
import de.erdbeerbaerlp.splatcord2.storage.Config;
import de.erdbeerbaerlp.splatcord2.storage.SplatProfile;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon1.RotationByml;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon1.SplatfestByml;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon2.coop_schedules.CoOpSchedules;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon2.splatnet.SplatNet;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon2.translations.Locale;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon2.weapons.Weapon;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon3.loadoutink.LInk3;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon3.splatnet.SplatNet3;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon3.translations.S3Locale;
import de.erdbeerbaerlp.splatcord2.storage.sql.DatabaseInterface;
import de.erdbeerbaerlp.splatcord2.tasks.*;
import de.erdbeerbaerlp.splatcord2.util.ScheduleUtil;
import de.erdbeerbaerlp.splatcord2.util.wiiu.BossFileUtil;
import io.javalin.Javalin;
import net.dv8tion.jda.api.entities.User;

import javax.net.ssl.HttpsURLConnection;
import javax.security.auth.login.LoginException;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.lang.reflect.Type;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static io.javalin.apibuilder.ApiBuilder.get;
import static io.javalin.apibuilder.ApiBuilder.path;

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
    public static RotationByml s1rotations = null;
    public static RotationByml s1rotationsPretendo = null;
    public static SplatfestByml s1splatfestPretendo = null;
    public static SplatfestByml s1splatfestSplatfestival = null;
    public static boolean splatoon2inkStatus = false;
    public static boolean splatoon3inkStatus = false;
    public static boolean splatoon1Status = false;
    public static boolean splatoon1PretendoStatus = false;
    public static CoOpSchedules coop_schedules;
    public static Map<String, Weapon> weaponData = new HashMap<>();
    public static Font splatfont2;


    public static void main(String[] args) throws Exception {
        Config.instance().loadConfig();

        try {
            InputStream fontStream = Main.class.getResourceAsStream("/assets/fonts/Splatfont.ttf");
            splatfont2 = Font.createFont(Font.TRUETYPE_FONT, fontStream);

        } catch (Exception e) {
            e.printStackTrace();
        }

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
            Main.s1splatfestPretendo = BossFileUtil.getFestByml("https://npts.app.pretendo.cc/p01/tasksheet/1/zvGSM4kOrXpkKnpT/optdat2?c=EU&l=en");
            Main.s1splatfestSplatfestival = BossFileUtil.getFestBymlDirect("https://github.com/Sheldon10095/Splatfestival_StaffFiles/raw/main/FestFiles/00000544",false);
        } catch (Exception e) {
            System.err.println("Failed loading splatoon 1 rotations from pretendo!");
            e.printStackTrace();
            Main.splatoon1PretendoStatus = false;
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

        System.out.println("Downloading Loadout.ink (slushiegoose.github.io) data");
        LInk3.init();
        try {
            ScheduleUtil.updateSpl3Fests();
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

        CommandRegistry.setCommands();
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

        Javalin api = Javalin.create(config -> {
            config.routing.caseInsensitiveRoutes = true;
        }).routes(() -> {
            path("/", ()->{
                get((ctx)->{
                    ctx.redirect("https://splatcord.ink/info/api");
                });
            });
            path("/stats", () -> {
                get(API::stats);
            });
            path("/status", () -> {
                get(API::status);
            });
            path("/s1rotations", () -> {
                get(API::s1rotation);
            });
            path("/s2rotations", () -> {
                get(API::s2rotation);
            });
            path("/s3rotations", () -> {
                get(API::s3rotation);
            });
        });
        api.start(Config.instance().web.port);

        Timer ti = new Timer();
        final StatusUpdateTask statusTask = new StatusUpdateTask();
        ti.scheduleAtFixedRate(statusTask, 0, TimeUnit.SECONDS.toMillis(7));
        startTime = Instant.now();


        final Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR, c.get(Calendar.HOUR+1));
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 10);
        final RotationTask rotationTask = new RotationTask();
        final SalmonrunTask salmonrunTask = new SalmonrunTask();
        final DataUpdateTask dataUpdateTask = new DataUpdateTask();
        final SplatnetOrderTask splatnetOrderTask = new SplatnetOrderTask();
        ti.scheduleAtFixedRate(rotationTask, c.getTime(), TimeUnit.MINUTES.toMillis(60));
        ti.scheduleAtFixedRate(salmonrunTask, c.getTime(), TimeUnit.MINUTES.toMillis(60));

        c.set(Calendar.SECOND, 30);
        ti.scheduleAtFixedRate(dataUpdateTask, c.getTime(), TimeUnit.MINUTES.toMillis(60));
        ti.scheduleAtFixedRate(splatnetOrderTask, TimeUnit.SECONDS.toMillis(10), TimeUnit.MINUTES.toMillis(5));
        ti.scheduleAtFixedRate(new StatisticRecordTask(),TimeUnit.SECONDS.toMillis(20), TimeUnit.MINUTES.toMillis(15));



        rotationTask.run();
        salmonrunTask.run();
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