package de.erdbeerbaerlp.splatcord2.commands;

import de.erdbeerbaerlp.splatcord2.Main;
import de.erdbeerbaerlp.splatcord2.dc.Bot;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon2.translations.Locale;
import de.erdbeerbaerlp.splatcord2.util.MessageUtil;
import de.erdbeerbaerlp.splatcord2.util.ScheduleUtil;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

public class SetsalmonCommand extends BaseCommand{
    public SetsalmonCommand(Locale l) {
        super("setsalmon", l.botLocale.cmdSetsalmonDesc);
    }

    @Override
    public boolean requiresManageServer() {
        return true;
    }

    @Override
    public void execute(SlashCommandEvent ev) {
        final Guild guild = ev.getGuild();
        final MessageChannel channel = ev.getChannel();
        final Locale lang = Main.translations.get(Main.iface.getServerLang(guild.getIdLong()));
        if (!guild.getMember(ev.getJDA().getSelfUser()).hasPermission(guild.getGuildChannelById(channel.getIdLong()), Permission.MESSAGE_WRITE)) {
            final Locale finalLang = lang;
            ev.getUser().openPrivateChannel().queue((c) -> {
                c.sendMessage(finalLang.botLocale.noWritePerms).queue();
            });
            return;
        }
        if (!Bot.isAdmin(ev.getMember())) {
            ev.reply(lang.botLocale.noAdminPerms).queue();
            return;
        }
        Main.iface.setSalmonChannel(guild.getIdLong(), channel.getIdLong());
        ev.reply(lang.botLocale.salmonFeedMsg).queue();
        MessageUtil.sendSalmonFeed(guild.getIdLong(),channel.getIdLong());

    }
}
