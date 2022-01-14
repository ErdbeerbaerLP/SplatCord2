package de.erdbeerbaerlp.splatcord2.commands;

import de.erdbeerbaerlp.splatcord2.Main;
import de.erdbeerbaerlp.splatcord2.dc.Bot;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon2.translations.Locale;
import de.erdbeerbaerlp.splatcord2.util.MessageUtil;
import de.erdbeerbaerlp.splatcord2.util.ScheduleUtil;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

public class SetstageCommand extends BaseCommand{
    public SetstageCommand(Locale l) {
        super("setstage", l.botLocale.cmdSetstageDesc);
        //final SubcommandData splat1 = new SubcommandData("splatoon1",l.botLocale.cmdSetstageDesc);
        final SubcommandData splat2 = new SubcommandData("splatoon2",l.botLocale.cmdSetstageDesc);
        //final SubcommandData splat3 = new SubcommandData("splatoon3",l.botLocale.cmdSetstageDesc);

        addSubcommands(splat2);
    }

    @Override
    public boolean requiresManageServer() {
        return true;
    }

    @Override
    public void execute(SlashCommandEvent ev) {
        final Guild guild = ev.getGuild();
        final MessageChannel channel = ev.getChannel();
        if(ev.getSubcommandName() != null)
            switch (ev.getSubcommandName()) {
                case "splatoon2":
                    final Locale lang = Main.translations.get(Main.iface.getServerLang(guild.getIdLong()));
                    if (!guild.getMember(ev.getJDA().getSelfUser()).hasPermission(guild.getGuildChannelById(channel.getIdLong()), Permission.MESSAGE_SEND)) {
                        final Locale finalLang = lang;
                        ev.getUser().openPrivateChannel().queue((c) -> {
                            c.sendMessage(finalLang.botLocale.noWritePerms).queue();
                        });
                        return;
                    }
                    if (!Bot.isAdmin(ev.getMember())) {
                        ev.reply(lang.botLocale.noAdminPerms).queue();
                        return;
                    }
                    Main.iface.setS2StageChannel(guild.getIdLong(), channel.getIdLong());
                    ev.reply(lang.botLocale.stageFeedMsg).queue();
                    MessageUtil.sendRotationFeed(guild.getIdLong(),channel.getIdLong(), ScheduleUtil.getCurrentRotation());
                    break;
            }
    }
}
