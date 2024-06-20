package de.erdbeerbaerlp.splatcord2.commands;

import de.erdbeerbaerlp.splatcord2.storage.json.splatoon2.translations.Locale;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;

import java.util.Random;

public class CodeCommand extends BaseCommand {
    public CodeCommand(Locale l) {
        super("code", l.botLocale.cmdCodeDesc);
        addOption(OptionType.BOOLEAN, "hidden", l.botLocale.cmdCodeArgDesc);
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
        boolean hide = false;
        final OptionMapping hidden = ev.getOption("hidden");
        if (hidden != null && hidden.getAsBoolean()) hide = true;

        final Random r = new Random();
        final int a = r.nextInt(10);
        final int b = r.nextInt(10);
        final int c = r.nextInt(10);
        final int d = r.nextInt(10);

        ev.deferReply(hide).submit().thenAccept((msg) -> msg.editOriginal(a + "" + b + "" + c + "" + d).queue());
    }
}
