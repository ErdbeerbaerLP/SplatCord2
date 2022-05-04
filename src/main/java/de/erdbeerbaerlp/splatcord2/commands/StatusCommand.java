package de.erdbeerbaerlp.splatcord2.commands;

import de.erdbeerbaerlp.splatcord2.Main;
import de.erdbeerbaerlp.splatcord2.storage.BotLanguage;
import de.erdbeerbaerlp.splatcord2.storage.Config;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon2.translations.Locale;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

import java.time.Duration;
import java.time.Instant;

public class StatusCommand extends BaseCommand {
    public StatusCommand(Locale l) {
        super("status", l.botLocale.cmdStatusDesc);
    }

    @Override
    public boolean requiresManageServer() {
        return false;
    }

    @Override
    public void execute(SlashCommandEvent ev) {
        Locale lang = Main.translations.get(BotLanguage.ENGLISH);
        final boolean dbAlive = Main.iface.status.isDBAlive();
        if (dbAlive)
            lang = Main.translations.get(Main.iface.getServerLang(ev.getGuild().getIdLong()));

        final EmbedBuilder b = new EmbedBuilder();

        b.addField(lang.botLocale.cmdStatusDB, dbAlive ? ":green_circle: " + lang.botLocale.online : ":red_circle: " + lang.botLocale.offline, true);
        b.addField("splatoon2.ink", Main.splatoon2inkStatus ? ":green_circle: " + lang.botLocale.online : ":red_circle: " + lang.botLocale.offline, true);

        if (dbAlive) {
            b.addBlankField(false);
            Duration duration = Duration.between(Main.startTime, Instant.now());
            String stats =
                    lang.botLocale.cmdStatusStatsServers + Main.bot.jda.getGuilds().size() + "\n" +
                            lang.botLocale.cmdStatusStatsUptime + duration.toDaysPart()+"d "+duration.toHoursPart()+"H "+duration.toMinutesPart()+"M";


            b.addField(lang.botLocale.cmdStatusStats, stats, false);
            final boolean beta = Config.instance().discord.betaServers.contains(ev.getGuild().getId());
            if(beta) b.addField("Beta server?", beta+"", false);
        }
        ev.replyEmbeds(b.build()).queue();
    }
}
