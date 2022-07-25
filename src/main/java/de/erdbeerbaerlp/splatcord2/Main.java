package de.erdbeerbaerlp.splatcord2;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import de.erdbeerbaerlp.splatcord2.dc.Bot;
import de.erdbeerbaerlp.splatcord2.storage.BotLanguage;
import de.erdbeerbaerlp.splatcord2.storage.Config;
import de.erdbeerbaerlp.splatcord2.storage.Rotation;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon1.Byml;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon1.Phase;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon2.coop_schedules.CoOpSchedules;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon2.translations.Locale;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon2.weapons.Weapon;
import de.erdbeerbaerlp.splatcord2.storage.sql.DatabaseInterface;
import de.erdbeerbaerlp.splatcord2.util.MessageUtil;
import de.erdbeerbaerlp.splatcord2.util.ScheduleUtil;
import de.erdbeerbaerlp.splatcord2.util.wiiu.BossFileUtil;
import de.erdbeerbaerlp.splatcord2.util.wiiu.RotationTimingUtil;

import javax.net.ssl.HttpsURLConnection;
import javax.security.auth.login.LoginException;
import java.io.*;
import java.lang.reflect.Type;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class Main {

    public static final String USER_AGENT = "SplatCord 2 (https://discord.gg/DBH9FSFCXb)";
    public static DatabaseInterface iface = null;
    public static Bot bot = null;
    public static Instant startTime = null;

    public static Byml s1rotations = null;

    public static boolean splatoon2inkStatus = false;

    public static final HashMap<BotLanguage, Locale> translations = new HashMap<>();
    public static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
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
            System.err.println("Failed loading splatoon 1 rotations!");
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
            locale.botLocale = l.botLocale;
            translations.put(l, locale);
        }
        System.out.println("Downloading Weapon Data");
        final URL wpn = new URL("https://splatoon2.ink/data/weapons.json");
        final HttpsURLConnection wpnConn = (HttpsURLConnection) wpn.openConnection();
        wpnConn.setRequestProperty("User-Agent", USER_AGENT);
        wpnConn.connect();
        String out = new Scanner(wpnConn.getInputStream(), StandardCharsets.UTF_8).useDelimiter("\\A").next();
        Type mapType = new TypeToken<Map<String, Weapon>>() {
        }.getType();
        weaponData = gson.fromJson(new StringReader(out), mapType);
        try {
            bot = new Bot();
        } catch (LoginException e) {
            System.err.println("Cannot login to discord!");
            e.printStackTrace();
        }
        if (bot == null) return;
        startTime = Instant.now();
        try {
            ScheduleUtil.updateRotationData();
            splatoon2inkStatus = true;
        } catch (IOException | JsonParseException e) {
            splatoon2inkStatus = false;
            e.printStackTrace();
        }

        long salmonEndTime = coop_schedules.details[0].end_time;

        while (true) {
            final Rotation currentRotation = ScheduleUtil.getCurrentRotation();
            final int currentS1RotationInt = RotationTimingUtil.getRotationForInstant(Instant.now());
            final Phase currentS1Rotation = s1rotations.root.Phases[currentS1RotationInt];

            //Splatoon 1 Rotations
            if (iface.status.isDBAlive() && currentS1RotationInt != Config.instance().doNotEdit.lastS1Rotation) {
                iface.getAllS1MapChannels().forEach((serverid, channel) -> {
                    try {
                        MessageUtil.sendRotationFeed(serverid, channel, currentS1Rotation);
                    } catch (Exception e) { //Try to catch everything to prevent messages not sent to other servers on error
                        e.printStackTrace();
                    }
                });
                Config.instance().doNotEdit.lastS1Rotation = currentS1RotationInt;
                Config.instance().saveConfig();
            }


            //Map rotation data
            if (iface.status.isDBAlive() && currentRotation.getRegular().start_time != Config.instance().doNotEdit.lastRotationTimestamp) {
                iface.getAllS2MapChannels().forEach((serverid, channel) -> {
                    try {
                        MessageUtil.sendRotationFeed(serverid, channel, currentRotation);
                    } catch (Exception e) { //Try to catch everything to prevent messages not sent to other servers on error
                        e.printStackTrace();
                    }
                });
                Config.instance().doNotEdit.lastRotationTimestamp = currentRotation.getRegular().start_time;
                Config.instance().saveConfig();
            }


            //Salmon run data
            if (iface.status.isDBAlive() && coop_schedules.details[0].start_time != Config.instance().doNotEdit.lastSalmonTimestamp) {
                if (salmonEndTime <= (System.currentTimeMillis() / 1000)) {
                    salmonEndTime = -1;
                    /*iface.getAllSalmonMessages().forEach((chan, msg) -> {
                        if (msg != null) {
                            final TextChannel channel = bot.jda.getTextChannelById(chan);
                            if (channel == null) return;
                            Locale lang = Main.translations.get(Main.iface.getServerLang(channel.getGuild().getIdLong()));
                            channel.retrieveMessageById(msg).submit().thenAccept((message) -> {
                                final List<MessageEmbed> e = message.getEmbeds();
                                final EmbedBuilder embedBuilder = new EmbedBuilder(e.get(0));
                                embedBuilder.setTimestamp(null);
                                embedBuilder.setFooter(lang.botLocale.footer_closed);
                                message.editMessageEmbeds(embedBuilder.build()).queue();
                            });
                        }
                    });*/
                }
                if (iface.status.isDBAlive() && coop_schedules.details[0].start_time <= (System.currentTimeMillis() / 1000)) {
                    iface.getAllSalmonChannels().forEach((serverid, channel) -> {
                        try {
                            MessageUtil.sendSalmonFeed(serverid, channel);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                    Config.instance().doNotEdit.lastSalmonTimestamp = coop_schedules.details[0].start_time;
                    Config.instance().saveConfig();
                }
            }

            //Try to update data
            if (LocalTime.now().getMinute() == 0 && LocalTime.now().getSecond() >= 30) {
                try {
                    ScheduleUtil.updateRotationData();
                    splatoon2inkStatus = true;
                } catch (IOException | JsonParseException e) {
                    e.printStackTrace();
                    splatoon2inkStatus = false;
                }
                try {
                    s1rotations = BossFileUtil.getStageByml();
                } catch (Exception e) {
                    System.err.println("Failed loading splatoon 1 rotations!");
                    e.printStackTrace();
                }
                TimeUnit.MINUTES.sleep(2);
            }
            try {
                TimeUnit.SECONDS.sleep(15);
                Config.instance().loadConfig();
            } catch (InterruptedException e) {
                return;
            }
        }
    }
}