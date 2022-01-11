package de.erdbeerbaerlp.splatcord2;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import de.erdbeerbaerlp.splatcord2.dc.Bot;
import de.erdbeerbaerlp.splatcord2.storage.BotLanguage;
import de.erdbeerbaerlp.splatcord2.storage.Config;
import de.erdbeerbaerlp.splatcord2.storage.Rotation;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon2.coop_schedules.CoOpSchedules;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon2.translations.Locale;
import de.erdbeerbaerlp.splatcord2.storage.sql.DatabaseInterface;
import de.erdbeerbaerlp.splatcord2.util.MessageUtil;
import de.erdbeerbaerlp.splatcord2.util.ScheduleUtil;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;

import javax.net.ssl.HttpsURLConnection;
import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class Main {

    public static final String USER_AGENT = "SplatCord 2 (https://discord.gg/DBH9FSFCXb)";
    public static DatabaseInterface iface = null;
    public static Bot bot = null;
    public static Instant startTime = null;

    public static boolean splatoon2inkStatus = false;

    public static final HashMap<BotLanguage, Locale> translations = new HashMap<>();
    public static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    public static CoOpSchedules coop_schedules;

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

            //Map rotation data
            if (iface.status.isDBAlive() && currentRotation.getRegular().start_time != Config.instance().doNotEdit.lastRotationTimestamp) {
                iface.getAllMapChannels().forEach((serverid, channel) -> {
                    MessageUtil.sendRotationFeed(serverid,channel,currentRotation);
                });
                Config.instance().doNotEdit.lastRotationTimestamp = currentRotation.getRegular().start_time;
                Config.instance().saveConfig();
            }


            //Salmon run data
            if (iface.status.isDBAlive() && coop_schedules.details[0].start_time != Config.instance().doNotEdit.lastSalmonTimestamp) {
                if (salmonEndTime <= (System.currentTimeMillis() / 1000)) {
                    salmonEndTime = -1;
                    iface.getAllSalmonMessages().forEach((chan, msg) -> {
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
                                iface.setSalmonMessage(channel.getGuild().getIdLong(), null);
                            });
                        }
                    });
                }
                if (iface.status.isDBAlive() && coop_schedules.details[0].start_time <= (System.currentTimeMillis() / 1000)) {
                    iface.getAllSalmonChannels().forEach((serverid, channel) -> {
                        MessageUtil.sendSalmonFeed(serverid,channel);


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