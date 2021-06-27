package de.erdbeerbaerlp.splatcord2.commands;

import de.erdbeerbaerlp.splatcord2.Main;
import de.erdbeerbaerlp.splatcord2.dc.Bot;
import de.erdbeerbaerlp.splatcord2.storage.json.translations.Locale;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

public class DelstageCommand extends BaseCommand{
    public DelstageCommand(Locale l) {
        super("delstage", l.botLocale.cmdDelstageDesc);
    }

    @Override
    public boolean requiresManageServer() {
        return true;
    }

    @Override
    public void execute(SlashCommandEvent ev) {
        final Locale lang = Main.translations.get(Main.iface.getServerLang(ev.getGuild().getIdLong()));
        if (!Bot.isAdmin(ev.getMember())) {
            ev.reply(lang.botLocale.noAdminPerms).queue();
            return;
        }
        Main.iface.setStageChannel(ev.getGuild().getIdLong(), null);
        ev.reply(lang.botLocale.deleteSuccessful).queue();
    }
}
