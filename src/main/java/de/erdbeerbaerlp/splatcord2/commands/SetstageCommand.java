package de.erdbeerbaerlp.splatcord2.commands;

import de.erdbeerbaerlp.splatcord2.Main;
import de.erdbeerbaerlp.splatcord2.dc.Bot;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon2.translations.Locale;
import de.erdbeerbaerlp.splatcord2.util.MessageUtil;
import de.erdbeerbaerlp.splatcord2.util.ScheduleUtil;
import de.erdbeerbaerlp.splatcord2.util.wiiu.RotationTimingUtil;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

import java.time.Instant;

public class SetstageCommand extends BaseCommand {
    public SetstageCommand(Locale l) {
        super("setstage", l.botLocale.cmdSetstageDesc);
        final SubcommandData splat1 = new SubcommandData("splatoon1", l.botLocale.cmdSetstageDesc);
        final SubcommandData splat2 = new SubcommandData("splatoon2", l.botLocale.cmdSetstageDesc);
        final SubcommandData splat3 = new SubcommandData("splatoon3",l.botLocale.cmdSetstageDesc);

        addSubcommands(splat2, splat1,splat3);
    }

    @Override
    public boolean requiresManageServer() {
        return true;
    }

    @Override
    public void execute(SlashCommandInteractionEvent ev) {
        final Guild guild = ev.getGuild();
        final Locale lang = Main.translations.get(Main.iface.getServerLang(guild.getIdLong()));
        final MessageChannel channel = ev.getChannel();
        if (ev.getSubcommandName() != null)
            switch (ev.getSubcommandName()) {
                case "splatoon1":
                    if (checkPerms(ev, guild, lang, channel)) return;
                    Main.iface.setS1StageChannel(guild.getIdLong(), channel.getIdLong());
                    ev.reply(lang.botLocale.stageFeedMsg).queue();
                    MessageUtil.sendS1RotationFeed(guild.getIdLong(), channel.getIdLong(), Main.s1rotations.root.Phases[RotationTimingUtil.getRotationForInstant(Instant.now())]);
                    break;
                case "splatoon2":
                    if (checkPerms(ev, guild, lang, channel)) return;
                    Main.iface.setS2StageChannel(guild.getIdLong(), channel.getIdLong());
                    ev.reply(lang.botLocale.stageFeedMsg).queue();
                    MessageUtil.sendS2RotationFeed(guild.getIdLong(), channel.getIdLong(), ScheduleUtil.getCurrentRotation());
                    break;
                case "splatoon3":
                    if (checkPerms(ev, guild, lang, channel)) return;
                    Main.iface.setS3StageChannel(guild.getIdLong(), channel.getIdLong());
                    ev.reply(lang.botLocale.stageFeedMsg).queue();
                    MessageUtil.sendS3RotationFeed(guild.getIdLong(), channel.getIdLong(), ScheduleUtil.getCurrentS3Rotation());
                    break;
            }
    }

    private boolean checkPerms(SlashCommandInteractionEvent ev, Guild guild, Locale lang, MessageChannel channel) {
        if (!guild.getMember(ev.getJDA().getSelfUser()).hasPermission(guild.getGuildChannelById(channel.getIdLong()), Permission.MESSAGE_SEND)) {
            final Locale finalLang = lang;
            ev.getUser().openPrivateChannel().queue((c) -> {
                c.sendMessage(finalLang.botLocale.noWritePerms).queue();
            });
            return true;
        }
        if (!Bot.isAdmin(ev.getMember())) {
            ev.reply(lang.botLocale.noAdminPerms).queue();
            return true;
        }
        return false;
    }
}
