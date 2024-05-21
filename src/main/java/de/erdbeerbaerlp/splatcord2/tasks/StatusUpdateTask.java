package de.erdbeerbaerlp.splatcord2.tasks;

import de.erdbeerbaerlp.splatcord2.Main;
import de.erdbeerbaerlp.splatcord2.storage.Config;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;

import java.util.Random;
import java.util.TimerTask;

public class StatusUpdateTask extends TimerTask {
    final Random r = new Random();
    int presence = 0;

    @Override
    public void run() {
        try {
            Config.instance().loadConfig();
            if (!Main.iface.status.isDBAlive()) {
                Main.bot.jda.getPresence().setPresence(OnlineStatus.DO_NOT_DISTURB, Activity.of(Activity.ActivityType.CUSTOM_STATUS, "⚠ Database is down!"));
                return;
            }
            boolean partialOutage = false;
            if (!Main.splatoon2inkStatus && !Main.splatoon3inkStatus && !Main.splatoon1PretendoStatus) {
                Main.bot.jda.getPresence().setPresence(OnlineStatus.DO_NOT_DISTURB, Activity.of(Activity.ActivityType.CUSTOM_STATUS, "Total Service outage! - /status"));
                return;
            }
            if (!Main.splatoon2inkStatus || !Main.splatoon3inkStatus || !Main.splatoon1PretendoStatus) {
                partialOutage = true;
                if (r.nextBoolean()) {
                    Main.bot.jda.getPresence().setPresence(OnlineStatus.IDLE, Activity.of(Activity.ActivityType.CUSTOM_STATUS, "Partial Service outage! - /status"));
                    return;
                }
            }
            final Config.Discord.Status s = Config.instance().discord.botStatus.get(presence);
            Main.bot.jda.getPresence().setPresence(partialOutage ? OnlineStatus.IDLE : OnlineStatus.ONLINE, Activity.of(s.type, s.message.replace("%servercount%", Main.bot.jda.getGuilds().size() + ""), s.streamingURL));
            presence++;
            if (presence >
                    Config.instance().discord.botStatus.size() - 1) presence = 0;
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
