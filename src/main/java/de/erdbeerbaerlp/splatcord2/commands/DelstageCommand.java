package de.erdbeerbaerlp.splatcord2.commands;

import de.erdbeerbaerlp.splatcord2.Main;
import de.erdbeerbaerlp.splatcord2.dc.Bot;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon2.translations.Locale;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

public class DelstageCommand extends BaseCommand {
    public DelstageCommand(Locale l) {
        super("delstage", l.botLocale.cmdDelstageDesc);
        final SubcommandData splat1 = new SubcommandData("splatoon1", l.botLocale.cmdSetstageDesc);
        final SubcommandData splat2 = new SubcommandData("splatoon2", l.botLocale.cmdSetstageDesc);
        final SubcommandData splat3 = new SubcommandData("splatoon3", l.botLocale.cmdSetstageDescTemporary);

        addSubcommands(splat2, splat1, splat3);
    }

    @Override
    public boolean requiresManageServer() {
        return true;
    }

    @Override
    public void execute(SlashCommandInteractionEvent ev) {
        final Locale lang = Main.translations.get(Main.iface.getServerLang(ev.getGuild().getIdLong()));
        if (ev.getSubcommandName() != null)
            switch (ev.getSubcommandName()) {
                case "splatoon1":
                    if (!Bot.isAdmin(ev.getMember())) {
                        ev.reply(lang.botLocale.noAdminPerms).queue();
                        return;
                    }
                    Main.iface.setS1StageChannel(ev.getGuild().getIdLong(), null);
                    ev.reply(lang.botLocale.deleteSuccessful).queue();
                    break;
                case "splatoon2":
                    if (!Bot.isAdmin(ev.getMember())) {
                        ev.reply(lang.botLocale.noAdminPerms).queue();
                        return;
                    }
                    Main.iface.setS2StageChannel(ev.getGuild().getIdLong(), null);
                    ev.reply(lang.botLocale.deleteSuccessful).queue();
                    break;
                case "splatoon3":
                    if (!Bot.isAdmin(ev.getMember())) {
                        ev.reply(lang.botLocale.noAdminPerms).queue();
                        return;
                    }
                    Main.iface.setS3StageChannel(ev.getGuild().getIdLong(), null);
                    ev.reply(lang.botLocale.deleteSuccessful).queue();
                    break;
            }
    }
}
