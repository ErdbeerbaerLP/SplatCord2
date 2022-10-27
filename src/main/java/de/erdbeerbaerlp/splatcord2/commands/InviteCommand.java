package de.erdbeerbaerlp.splatcord2.commands;

import de.erdbeerbaerlp.splatcord2.storage.json.splatoon2.translations.Locale;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class InviteCommand extends BaseCommand {
    public InviteCommand(Locale l) {
        super("invite", l.botLocale.cmdInviteDesc);
    }

    @Override
    public boolean requiresManageServer() {
        return false;
    }

    @Override
    public void execute(SlashCommandInteractionEvent ev) {
        ev.reply("<https://discord.com/api/oauth2/authorize?client_id=822228767165644872&permissions=379968&scope=applications.commands%20bot>").setEphemeral(true).queue();
    }
}
