package de.erdbeerbaerlp.splatcord2;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import de.erdbeerbaerlp.splatcord2.dc.Bot;
import de.erdbeerbaerlp.splatcord2.storage.*;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon1.Byml;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon1.Phase;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon2.coop_schedules.CoOpSchedules;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon2.splatnet.Merchandise;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon2.splatnet.Order;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon2.splatnet.SplatNet;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon2.translations.Locale;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon2.weapons.Weapon;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon3.splatnet.LimitedGear;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon3.splatnet.SplatNet3;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon3.translations.S3Locale;
import de.erdbeerbaerlp.splatcord2.storage.sql.DatabaseInterface;
import de.erdbeerbaerlp.splatcord2.util.MessageUtil;
import de.erdbeerbaerlp.splatcord2.util.ScheduleUtil;
import de.erdbeerbaerlp.splatcord2.util.wiiu.BossFileUtil;
import de.erdbeerbaerlp.splatcord2.util.wiiu.RotationTimingUtil;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;

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
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import static de.erdbeerbaerlp.splatcord2.commands.Splatnet2Command.repeat;

public class Main {

    public static final String USER_AGENT = "SplatCord 2 (https://discord.gg/DBH9FSFCXb)";
    public static DatabaseInterface iface = null;
    public static Bot bot = null;
    public static Instant startTime = null;
    public static SplatNet splatNet2 = null;
    public static SplatNet3 splatNet3 = null;
    public static Byml s1rotations = null;
    public static boolean splatoon2inkStatus = false;
    public static boolean splatoon3inkStatus = false;

    public static final HashMap<BotLanguage, Locale> translations = new HashMap<>();
    public static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    public static CoOpSchedules coop_schedules;

    public static Map<String, Weapon> weaponData = new HashMap<>();

    private static final Map<Long, User> userCache = new HashMap<>();
    private static final HashMap<Long,SplatProfile> userProfileCache = new HashMap<>();

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

        long salmonEndTime = coop_schedules.details[0].end_time;

        while (true) {

            final Rotation currentRotation = ScheduleUtil.getCurrentRotation();
            final S3Rotation currentS3Rotation = ScheduleUtil.getCurrentS3Rotation();
            final int currentS1RotationInt = RotationTimingUtil.getRotationForInstant(Instant.now());
            final Phase currentS1Rotation = s1rotations.root.Phases[currentS1RotationInt];
            System.gc();
            //Try to update data
            if (LocalTime.now().getMinute() == 0 && LocalTime.now().getSecond() >= 30) {
                try {
                    ScheduleUtil.updateS2RotationData();
                    splatoon2inkStatus = true;
                } catch (IOException | JsonParseException e) {
                    e.printStackTrace();
                    splatoon2inkStatus = false;
                }
                try {
                    ScheduleUtil.updateS3RotationData();
                    splatoon3inkStatus = true;
                } catch (IOException | JsonParseException e) {
                    e.printStackTrace();
                    splatoon3inkStatus = false;
                }
                try {
                    s1rotations = BossFileUtil.getStageByml();
                } catch (Exception e) {
                    System.err.println("Failed loading splatoon 1 rotations!");
                    e.printStackTrace();
                }
                final URL tworld2 = new URL("https://splatoon2.ink/data/merchandises.json");
                final HttpsURLConnection twcon2 = (HttpsURLConnection) tworld2.openConnection();
                twcon2.setRequestProperty("User-Agent", Main.USER_AGENT);
                twcon2.connect();
                splatNet2 = Main.gson.fromJson(new InputStreamReader(twcon2.getInputStream()), SplatNet.class);
                final URL tworld3 = new URL("https://splatoon3.ink/data/gear.json");
                final HttpsURLConnection conn3 = (HttpsURLConnection) tworld3.openConnection();
                conn3.setRequestProperty("User-Agent", Main.USER_AGENT);
                conn3.connect();
                splatNet3 = Main.gson.fromJson(new InputStreamReader(conn3.getInputStream()), SplatNet3.class);
            }
            try {
                //Splatoon 1 Rotations
                if (iface.status.isDBAlive() && currentS1RotationInt != Config.instance().doNotEdit.lastS1Rotation) {
                    iface.getAllS1MapChannels().forEach((serverid, channel) -> {
                        try {
                            MessageUtil.sendS1RotationFeed(serverid, channel, currentS1Rotation);
                        } catch (
                                Exception e) { //Try to catch everything to prevent messages not sent to other servers on error
                            e.printStackTrace();
                        }
                    });
                    Config.instance().doNotEdit.lastS1Rotation = currentS1RotationInt;
                    Config.instance().saveConfig();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                //Map rotation data
                if (iface.status.isDBAlive() && currentRotation.getRegular().start_time != Config.instance().doNotEdit.lastRotationTimestamp) {
                    iface.getAllS2MapChannels().forEach((serverid, channel) -> {
                        try {
                            MessageUtil.sendS2RotationFeed(serverid, channel, currentRotation);
                        } catch (
                                Exception e) { //Try to catch everything to prevent messages not sent to other servers on error
                            e.printStackTrace();
                        }
                    });
                    Config.instance().doNotEdit.lastRotationTimestamp = currentRotation.getRegular().start_time;
                    Config.instance().saveConfig();
                }

                if (iface.status.isDBAlive() && currentS3Rotation.getRegular().getStartTime() != Config.instance().doNotEdit.lastS3RotationTimestamp) {
                    iface.getAllS3MapChannels().forEach((serverid, channel) -> {
                        try {
                            MessageUtil.sendS3RotationFeed(serverid, channel, currentS3Rotation);
                        } catch (
                                Exception e) { //Try to catch everything to prevent messages not sent to other servers on error
                            e.printStackTrace();
                        }
                    });
                    Config.instance().doNotEdit.lastS3RotationTimestamp = currentS3Rotation.getRegular().getStartTime();
                    Config.instance().saveConfig();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                //Salmon run data
                if (iface.status.isDBAlive() && coop_schedules.details[0].start_time != Config.instance().doNotEdit.lastSalmonTimestamp) {
                    if (salmonEndTime <= (System.currentTimeMillis() / 1000)) {
                        salmonEndTime = -1;
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

            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                if (iface.status.isDBAlive() && currentS3Rotation.getCoop().getStartTime() != Config.instance().doNotEdit.lastS3SalmonTimestamp) {
                    if (salmonEndTime <= (System.currentTimeMillis() / 1000)) {
                        salmonEndTime = -1;
                    }
                    if (iface.status.isDBAlive() && currentS3Rotation.getCoop().getStartTime() <= (System.currentTimeMillis() / 1000)) {
                        iface.getAllS3SalmonChannels().forEach((serverid, channel) -> {
                            try {
                                MessageUtil.sendS3SalmonFeed(serverid, channel);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });
                        Config.instance().doNotEdit.lastS3SalmonTimestamp = currentS3Rotation.getCoop().getStartTime();
                        Config.instance().saveConfig();
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                final HashMap<Long, Order[]> allOrders = iface.getAllS2Orders();
                for (Merchandise m : splatNet2.merchandises) {
                    for (Long usrid : allOrders.keySet()) {

                        final SplatProfile profile = getUserProfile(usrid);
                        final ArrayList<Order> orders = profile.s2orders;
                        if (orders.size() > 0) {
                            final User user = getUserById(usrid);
                            final ArrayList<Order> finishedOrders = new ArrayList<>();
                            for (Order o : orders) {
                                if ((m.gear.kind + "/" + m.gear.id).equals(o.gear)) {
                                    final TextChannel channel = bot.jda.getTextChannelById(o.channel);
                                    final Locale lang = Main.translations.get(Main.iface.getServerLang(channel.getGuild().getIdLong()));
                                    final MessageCreateBuilder b = new MessageCreateBuilder();
                                    b.addContent(lang.botLocale.cmdSplatnetOrderFinished.replace("%ping%", user.getAsMention()));
                                    final EmbedBuilder emb = new EmbedBuilder().setTimestamp(Instant.ofEpochSecond(m.end_time)).setFooter(lang.botLocale.footer_ends).setThumbnail("https://splatoon2.ink/assets/splatnet" + m.gear.image).setAuthor(lang.allGears.get(m.gear.kind + "/" + m.gear.id) + " (" + lang.brands.get(m.gear.brand.id).name + ")", null, "https://splatoon2.ink/assets/splatnet" + m.gear.brand.image).addField(lang.botLocale.skillSlots, Emote.resolveFromS2Ability(m.skill.id) + repeat(1 + m.gear.rarity, Emote.ABILITY_LOCKED.toString()), true).addField(lang.botLocale.price, Emote.SPLATCASH.toString() + m.price, true);
                                    b.addEmbeds(emb.build());
                                    channel.sendMessage(b.build()).queue();
                                    finishedOrders.add(o);
                                }
                            }

                            if (finishedOrders.size() > 0) {
                                profile.s2orders.removeAll(finishedOrders);
                                Main.iface.updateSplatProfile(profile);
                                finishedOrders.clear();
                            }
                        }
                    }
                }
                allOrders.clear();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                final HashMap<Long, Order[]> allOrders = iface.getAllS3Orders();
                for (LimitedGear g : splatNet3.data.gesotown.limitedGears) {
                    for (Long usrid : allOrders.keySet()) {

                        final SplatProfile profile = getUserProfile(usrid);
                        final ArrayList<Order> orders = profile.s3orders;
                        if (orders.size() > 0) {
                            final User user = getUserById(usrid);
                            final ArrayList<Order> finishedOrders = new ArrayList<>();
                            for (Order o : orders) {
                                if ((g.gear.__splatoon3ink_id).equals(o.gear)) {
                                    final TextChannel channel = bot.jda.getTextChannelById(o.channel);
                                    final Locale lang = Main.translations.get(Main.iface.getServerLang(channel.getGuild().getIdLong()));
                                    final MessageCreateBuilder b = new MessageCreateBuilder();
                                    b.addContent(lang.botLocale.cmdSplatnetOrderFinished.replace("%ping%", user.getAsMention()));
                                    final EmbedBuilder emb = new EmbedBuilder().setTimestamp(Instant.ofEpochSecond(g.getEndTime())).setFooter(lang.botLocale.footer_ends).setThumbnail(g.gear.image.url).setAuthor(lang.s3locales.gear.get(g.gear.__splatoon3ink_id).name + " (" + lang.s3locales.brands.get(g.gear.brand.id).name + ")", null, g.gear.brand.image.url).addField(lang.botLocale.skillSlots, lang.s3locales.powers.get(g.gear.primaryGearPower.__splatoon3ink_id).name + repeat(g.gear.additionalGearPowers.length, Emote.ABILITY_LOCKED.toString()), true).addField(lang.botLocale.price, Emote.SPLATCASH + g.price, true);
                                    b.addEmbeds(emb.build());
                                    channel.sendMessage(b.build()).queue();
                                    finishedOrders.add(o);
                                }
                            }

                            if (finishedOrders.size() > 0) {
                                profile.s3orders.removeAll(finishedOrders);
                                Main.iface.updateSplatProfile(profile);
                                finishedOrders.clear();
                            }
                        }
                    }
                }
                allOrders.clear();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                TimeUnit.SECONDS.sleep(15);
                Config.instance().loadConfig();
            } catch (InterruptedException e) {
                e.printStackTrace();
                return;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static User getUserById(Long userid){
        if(userCache.containsKey(userid)) return userCache.get(userid);
        else{
            System.out.println("getting uncached user " + userid);
            final User out = bot.jda.retrieveUserById(userid).complete();
            System.out.println("retrieved user " + userid);
            userCache.put(userid, out);
            return out;
        }
    }
    public static SplatProfile getUserProfile(Long userid){
        if(userProfileCache.containsKey(userid)) return userProfileCache.get(userid);
        else{
            System.out.println("getting uncached user profile " + userid);
            final SplatProfile out = iface.getSplatoonProfiles(userid);
            System.out.println("retrieved user profile for " + userid);
            userProfileCache.put(userid, out);
            return out;
        }
    }
    public static User getUserById(String userid){
        return getUserById(Long.parseLong(userid));
    }
}