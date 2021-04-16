package de.erdbeerbaerlp.splatcord2;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.erdbeerbaerlp.splatcord2.dc.Bot;
import de.erdbeerbaerlp.splatcord2.storage.BotLanguage;
import de.erdbeerbaerlp.splatcord2.storage.Config;
import de.erdbeerbaerlp.splatcord2.storage.json.coop_schedules.CoOpSchedules;
import de.erdbeerbaerlp.splatcord2.storage.json.scheduling.Schedules;
import de.erdbeerbaerlp.splatcord2.storage.json.translations.Locale;
import de.erdbeerbaerlp.splatcord2.storage.sql.DatabaseInterface;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;

import javax.net.ssl.HttpsURLConnection;
import javax.security.auth.login.LoginException;
import java.io.InputStreamReader;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class Main {


    public static DatabaseInterface iface = null;
    public static Bot bot = null;

    public static final HashMap<BotLanguage, Locale> translations = new HashMap<>();
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    public static Schedules schedules;
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
        try {
            bot = new Bot();
        } catch (LoginException e) {
            System.err.println("Cannot login to discord!");
            e.printStackTrace();
        }
        if (bot == null) return;

        System.out.println("Downloading locales");
        for (BotLanguage l : BotLanguage.values()) {
            final URL lng = new URL("https://splatoon2.ink/data/locale/" + l.key + ".json");
            final HttpsURLConnection deConn = (HttpsURLConnection) lng.openConnection();
            deConn.setRequestProperty("User-Agent", "SplatCord 2");
            deConn.connect();
            final Locale locale = gson.fromJson(new InputStreamReader(deConn.getInputStream()), Locale.class);
            locale.botLocale = l.botLocale;
            translations.put(l, locale);
        }

        System.out.println("Downloading schedules...");
        final URL sched = new URL("https://splatoon2.ink/data/schedules.json");
        final HttpsURLConnection deConn = (HttpsURLConnection) sched.openConnection();
        deConn.setRequestProperty("User-Agent", "SplatCord 2");
        deConn.connect();
        schedules = gson.fromJson(new InputStreamReader(deConn.getInputStream()), Schedules.class);
        final URL sched2 = new URL("https://splatoon2.ink/data/coop-schedules.json");
        final HttpsURLConnection deConn2 = (HttpsURLConnection) sched2.openConnection();
        deConn2.setRequestProperty("User-Agent", "SplatCord 2");
        deConn2.connect();

        coop_schedules = gson.fromJson(new InputStreamReader(deConn2.getInputStream()), CoOpSchedules.class);
        while (true) {
            if (LocalTime.now().getMinute() == 0 && LocalTime.now().getSecond() >= 30) {
                long prevStartTime = schedules.regular[0].start_time;
                System.out.println("Downloading schedules...");
                final HttpsURLConnection updateSched = (HttpsURLConnection) sched.openConnection();
                updateSched.setRequestProperty("User-Agent", "SplatCord 2");
                updateSched.connect();
                schedules = gson.fromJson(new InputStreamReader(updateSched.getInputStream()), Schedules.class);
                final HttpsURLConnection updateCOOP = (HttpsURLConnection) sched2.openConnection();
                updateCOOP.setRequestProperty("User-Agent", "SplatCord 2");
                updateCOOP.connect();
                coop_schedules = gson.fromJson(new InputStreamReader(updateCOOP.getInputStream()), CoOpSchedules.class);
                if (schedules.regular[0].start_time != prevStartTime)
                    iface.getAllMapChannels().forEach((serverid, channel) -> bot.sendMapMessage(serverid, channel));
                TimeUnit.MINUTES.sleep(5);
            }
            long prevSalmonStartTime = coop_schedules.details[0].start_time;
            if (coop_schedules.details[0].start_time <= (System.currentTimeMillis() / 1000) && prevSalmonStartTime != Config.instance().doNotEdit.lastSalmonTimestamp) {
                iface.getAllSalmonChannels().forEach((serverid, channel) -> {
                    try {
                        Map.Entry<Long,Long> msg = bot.sendSalmonMessage(serverid, channel);
                        iface.setSalmonMessage(msg.getKey(),msg.getValue());
                    } catch (ExecutionException | InterruptedException e) {
                        e.printStackTrace();
                    }

                });
                Config.instance().doNotEdit.lastSalmonTimestamp = coop_schedules.details[0].start_time;
                Config.instance().saveConfig();
            }else if(coop_schedules.details[0].end_time <= (System.currentTimeMillis() / 1000) && prevSalmonStartTime != Config.instance().doNotEdit.lastSalmonTimestamp){                iface.getAllSalmonMessages().forEach((chan,msg)->{
                    if(msg != null){
                        final TextChannel channel = bot.jda.getTextChannelById(chan);
                        if(channel == null) return;
                        Locale lang = Main.translations.get(Main.iface.getServerLang(channel.getGuild().getIdLong()));
                        channel.retrieveMessageById(msg).submit().thenAccept((message)->{
                            final List<MessageEmbed> e = message.getEmbeds();
                            final EmbedBuilder embedBuilder = new EmbedBuilder(e.get(0));
                            embedBuilder.setTimestamp(null);
                            embedBuilder.setFooter(lang.botLocale.footer_closed);
                            message.editMessage(embedBuilder.build()).queue();
                            iface.setSalmonMessage(channel.getGuild().getIdLong(),null);
                        });
                    }
                });
                Config.instance().doNotEdit.lastSalmonTimestamp = coop_schedules.details[0].start_time;
                Config.instance().saveConfig();
            }
            try {
                TimeUnit.SECONDS.sleep(30);
            }catch (InterruptedException e){
                return;
            }
        }
    }
}
