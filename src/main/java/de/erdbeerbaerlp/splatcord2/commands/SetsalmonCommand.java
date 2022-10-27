package de.erdbeerbaerlp.splatcord2.commands;

import de.erdbeerbaerlp.splatcord2.Main;
import de.erdbeerbaerlp.splatcord2.dc.Bot;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon2.translations.Locale;
import de.erdbeerbaerlp.splatcord2.util.MessageUtil;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

public class SetsalmonCommand extends BaseCommand {
    public SetsalmonCommand(Locale l) {
        super("setsalmon", l.botLocale.cmdSetsalmonDesc);
        final SubcommandData splat2 = new SubcommandData("splatoon2", l.botLocale.cmdSetsalmonDesc);
        final SubcommandData splat3 = new SubcommandData("splatoon3", l.botLocale.cmdSetsalmonDesc);

        addSubcommands(splat2, splat3);
    }

    @Override
    public boolean requiresManageServer() {
        return true;
    }

    @Override
    public void execute(SlashCommandInteractionEvent ev) {
        final Guild guild = ev.getGuild();
        final MessageChannel channel = ev.getChannel();

        final Locale lang = Main.translations.get(Main.iface.getServerLang(guild.getIdLong()));
        if (ev.getSubcommandName() != null)
            switch (ev.getSubcommandName()) {
                case "splatoon2":
                    if (!guild.getMember(ev.getJDA().getSelfUser()).hasPermission(guild.getGuildChannelById(channel.getIdLong()), Permission.MESSAGE_SEND)) {
                        ev.getUser().openPrivateChannel().queue((c) -> {
                            c.sendMessage(lang.botLocale.noWritePerms).queue();
                        });
                        return;
                    }
                    if (!Bot.isAdmin(ev.getMember())) {
                        ev.reply(lang.botLocale.noAdminPerms).queue();
                        return;
                    }
                    Main.iface.setSalmonChannel(guild.getIdLong(), channel.getIdLong());
                    ev.reply(lang.botLocale.salmonFeedMsg).queue();
                    MessageUtil.sendSalmonFeed(guild.getIdLong(), channel.getIdLong());
                    break;
                case "splatoon3":
                    if (!guild.getMember(ev.getJDA().getSelfUser()).hasPermission(guild.getGuildChannelById(channel.getIdLong()), Permission.MESSAGE_SEND)) {
                        ev.getUser().openPrivateChannel().queue((c) -> {
                            c.sendMessage(lang.botLocale.noWritePerms).queue();
                        });
                        return;
                    }
                    if (!Bot.isAdmin(ev.getMember())) {
                        ev.reply(lang.botLocale.noAdminPerms).queue();
                        return;
                    }
                    Main.iface.setS3SalmonChannel(guild.getIdLong(), channel.getIdLong());
                    ev.reply(lang.botLocale.salmonFeedMsg).queue();
                    MessageUtil.sendS3SalmonFeed(guild.getIdLong(), channel.getIdLong());
                    break;
            }
    }
}
