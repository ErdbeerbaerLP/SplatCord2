package de.erdbeerbaerlp.splatcord2.commands;

import de.erdbeerbaerlp.splatcord2.Main;
import de.erdbeerbaerlp.splatcord2.dc.Bot;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon2.translations.Locale;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class DelsalmonCommand extends BaseCommand{
    public DelsalmonCommand(Locale l) {
        super("delsalmon", l.botLocale.cmdDelsalmonDesc);
    }

    @Override
    public boolean requiresManageServer() {
        return true;
    }

    @Override
    public void execute(SlashCommandInteractionEvent ev) {
        final Locale lang = Main.translations.get(Main.iface.getServerLang(ev.getGuild().getIdLong()));
        if (!Bot.isAdmin(ev.getMember())) {
            ev.reply(lang.botLocale.noAdminPerms).queue();
            return;
        }
        Main.iface.setSalmonChannel(ev.getGuild().getIdLong(), null);
        ev.reply(lang.botLocale.deleteSuccessful).queue();
    }
}
