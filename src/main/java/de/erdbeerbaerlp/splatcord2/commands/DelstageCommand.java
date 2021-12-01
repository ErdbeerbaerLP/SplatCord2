package de.erdbeerbaerlp.splatcord2.commands;

import de.erdbeerbaerlp.splatcord2.Main;
import de.erdbeerbaerlp.splatcord2.dc.Bot;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon2.translations.Locale;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

public class DelstageCommand extends BaseCommand {
    public DelstageCommand(Locale l) {
        super("delstage", l.botLocale.cmdDelstageDesc);
        //final SubcommandData splat1 = new SubcommandData("splatoon1",l.botLocale.cmdSetstageDesc);
        final SubcommandData splat2 = new SubcommandData("splatoon2", l.botLocale.cmdSetstageDesc);
        //final SubcommandData splat3 = new SubcommandData("splatoon3",l.botLocale.cmdSetstageDesc);

        addSubcommands(splat2);
    }

    @Override
    public boolean requiresManageServer() {
        return true;
    }

    @Override
    public void execute(SlashCommandEvent ev) {
        if(ev.getSubcommandName() != null)
        switch (ev.getSubcommandName()) {
            case "splatoon2":
                final Locale lang = Main.translations.get(Main.iface.getServerLang(ev.getGuild().getIdLong()));
                if (!Bot.isAdmin(ev.getMember())) {
                    ev.reply(lang.botLocale.noAdminPerms).queue();
                    return;
                }
                Main.iface.setStageChannel(ev.getGuild().getIdLong(), null);
                ev.reply(lang.botLocale.deleteSuccessful).queue();
                break;
        }
    }
}
