package de.erdbeerbaerlp.splatcord2.commands;

import de.erdbeerbaerlp.splatcord2.Main;
import de.erdbeerbaerlp.splatcord2.storage.json.translations.Locale;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

public class HelpCommand extends BaseCommand{
    public HelpCommand(Locale l) {
        super("help", l.botLocale.cmdHelpDesc);
    }

    @Override
    public boolean requiresManageServer() {
        return false;
    }

    @Override
    public void execute(SlashCommandEvent ev) {
        ev.reply(Main.iface.getServerLang(ev.getGuild().getIdLong()).botLocale.helpMessage).queue();
    }
}
