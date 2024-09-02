package de.erdbeerbaerlp.splatcord2.commands;

import de.erdbeerbaerlp.splatcord2.Main;
import de.erdbeerbaerlp.splatcord2.storage.BotLanguage;
import de.erdbeerbaerlp.splatcord2.storage.Config;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon2.translations.Locale;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

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
    public boolean isServerOnly() {
        return false;
    }

    @Override
    public void execute(SlashCommandInteractionEvent ev) {
        final BotLanguage userLang = BotLanguage.fromDiscordLocale(ev.getUserLocale());
        final Locale lang = Main.translations.get(userLang);
        final boolean dbAlive = Main.iface.status.isDBAlive();
        final EmbedBuilder b = new EmbedBuilder();

        b.addField(lang.botLocale.cmdStatusDB, dbAlive ? ":green_circle: " + lang.botLocale.online : ":red_circle: " + lang.botLocale.offline, true);
        b.addField("Splatoon 1 - Nintendo Network", Main.splatoon1Status ? ":blue_circle: " + lang.botLocale.online+":headstone:" : ":red_circle: " + lang.botLocale.offline+ ":headstone:", false);
        b.addField("Splatoon 1 - Pretendo Network", Main.splatoon1PretendoStatus ? ":green_circle: " + lang.botLocale.online : ":red_circle: " + lang.botLocale.offline, false);
        b.addField("splatoon2.ink", Main.splatoon2inkStatus ? ":green_circle: " + lang.botLocale.online : ":red_circle: " + lang.botLocale.offline, true);
        b.addField("splatoon3.ink", Main.splatoon3inkStatus ? ":green_circle: " + lang.botLocale.online : ":red_circle: " + lang.botLocale.offline, true);

        if (dbAlive) {
            b.addBlankField(false);
            Duration duration = Duration.between(Main.startTime, Instant.now());
            String stats =
                    lang.botLocale.cmdStatusStatsServers + Main.bot.jda.getGuilds().size() + "\n" +
                            lang.botLocale.cmdStatusStatsUptime + duration.toDaysPart() + "d " + duration.toHoursPart() + "H " + duration.toMinutesPart() + "M";


            b.addField(lang.botLocale.cmdStatusStats, stats, false);
            final boolean beta = Config.instance().discord.betaServers.contains(ev.getGuild().getId());
            if (beta) b.addField("Beta server?", beta + "", false);
        }
        ev.replyEmbeds(b.build()).setEphemeral(true).queue();
    }
}
