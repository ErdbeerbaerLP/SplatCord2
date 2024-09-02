package de.erdbeerbaerlp.splatcord2.commands;

import de.erdbeerbaerlp.splatcord2.storage.json.splatoon2.translations.Locale;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class SupportCommand extends BaseCommand {
    public SupportCommand(Locale l) {
        super("support", l.botLocale.cmdSupportDesc);
        setDescriptionLocalizations(l.discordLocalizationFunc("cmdSupportDesc"));
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
        ev.reply("https://discord.gg/DBH9FSFCXb").setEphemeral(true).queue();
    }
}
