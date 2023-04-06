package de.erdbeerbaerlp.splatcord2.commands;

import de.erdbeerbaerlp.splatcord2.storage.json.splatoon2.translations.Locale;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

public class XRankCommand extends BaseCommand{
    public XRankCommand(Locale l) {
        super("xrank", l.botLocale.cmdXrankDesc1);
        final SubcommandData tentatek = new SubcommandData("tentatek", l.botLocale.cmdXrankDesc1);
        final SubcommandData takoroka = new SubcommandData("takoroka", l.botLocale.cmdXrankDesc2);
        final OptionData mode = new OptionData(OptionType.STRING, "mode", l.botLocale.cmdXrankMode);
        l.s3locales.rules.forEach((s, n)->{
            mode.addChoice(n.name, s);
        });
        tentatek.addOptions(mode);
        takoroka.addOptions(mode);
        addSubcommands(tentatek, takoroka);
    }

    @Override
    public boolean requiresManageServer() {
        return false;
    }

    @Override
    public void execute(SlashCommandInteractionEvent ev) {

    }
}
