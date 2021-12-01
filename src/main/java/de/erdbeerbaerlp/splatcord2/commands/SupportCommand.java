package de.erdbeerbaerlp.splatcord2.commands;

import de.erdbeerbaerlp.splatcord2.storage.json.splatoon2.translations.Locale;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

public class SupportCommand extends BaseCommand{
    public SupportCommand(Locale l) {
        super("support", l.botLocale.cmdSupportDesc);
    }

    @Override
    public boolean requiresManageServer() {
        return false;
    }

    @Override
    public void execute(SlashCommandEvent ev) {
        ev.reply("https://discord.gg/DBH9FSFCXb").queue();
    }
}
