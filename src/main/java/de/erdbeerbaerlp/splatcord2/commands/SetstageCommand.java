package de.erdbeerbaerlp.splatcord2.commands;

import de.erdbeerbaerlp.splatcord2.Main;
import de.erdbeerbaerlp.splatcord2.dc.Bot;
import de.erdbeerbaerlp.splatcord2.storage.json.translations.Locale;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

public class SetstageCommand extends BaseCommand{
    public SetstageCommand(Locale l) {
        super("setstage", l.botLocale.cmdSetstageDesc);
    }

    @Override
    public boolean requiresManageServer() {
        return true;
    }

    @Override
    public void execute(SlashCommandEvent ev) {
        final Locale lang = Main.translations.get(Main.iface.getServerLang(ev.getGuild().getIdLong()));
        if (!ev.getGuild().getMember(ev.getJDA().getSelfUser()).hasPermission(ev.getGuild().getGuildChannelById(ev.getChannel().getIdLong()), Permission.MESSAGE_WRITE)) {
            final Locale finalLang = lang;
            ev.getUser().openPrivateChannel().queue((channel) -> {
                channel.sendMessage(finalLang.botLocale.noWritePerms).queue();
            });
            return;
        }
        if (!Bot.isAdmin(ev.getMember())) {
            ev.reply(lang.botLocale.noAdminPerms).queue();
            return;
        }
        Main.iface.setStageChannel(ev.getGuild().getIdLong(), ev.getChannel().getIdLong());
        ev.reply(lang.botLocale.stageFeedMsg).queue();
    }
}
