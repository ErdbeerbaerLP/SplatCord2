package de.erdbeerbaerlp.splatcord2.tasks;

import de.erdbeerbaerlp.splatcord2.Main;

import java.util.TimerTask;

public class StatisticRecordTask  extends TimerTask {
    @Override
    public void run() {
        Main.iface.addServerStatistic(Main.bot.jda.getGuilds().size());
    }
}
