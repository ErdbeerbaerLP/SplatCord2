package de.erdbeerbaerlp.splatcord2.dc;

import de.erdbeerbaerlp.splatcord2.Main;
import de.erdbeerbaerlp.splatcord2.commands.*;
import de.erdbeerbaerlp.splatcord2.storage.*;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon2.coop_schedules.Weapons;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon2.translations.Locale;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon3.loadoutink.Gear;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon3.loadoutink.LInk3;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon3.splatfest.FestRecord;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon3.splatfest.SplatfestRegion;
import de.erdbeerbaerlp.splatcord2.util.MessageUtil;
import de.erdbeerbaerlp.splatcord2.util.ScheduleUtil;
import de.erdbeerbaerlp.splatcord2.util.wiiu.RotationTimingUtil;
import net.dv8tion.jda.api.*;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.guild.*;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.EntitySelectInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.events.role.RoleCreateEvent;
import net.dv8tion.jda.api.events.role.update.RoleUpdatePermissionsEvent;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.hooks.EventListener;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import org.jetbrains.annotations.NotNull;

import javax.security.auth.login.LoginException;
import java.time.Instant;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static de.erdbeerbaerlp.splatcord2.Main.bot;
import static net.dv8tion.jda.api.Permission.MANAGE_SERVER;

public class Bot implements EventListener {
    public final JDA jda;

    public Bot() throws LoginException, InterruptedException {
        CommandRegistry.registerAllBaseCommands();
        JDABuilder b = JDABuilder.create(Config.instance().discord.token, GatewayIntent.GUILD_MESSAGES).addEventListeners(this);
        b.setMemberCachePolicy(MemberCachePolicy.DEFAULT);
        b.setAutoReconnect(true);
        b.disableCache(CacheFlag.ONLINE_STATUS);
        b.disableCache(CacheFlag.ACTIVITY, CacheFlag.VOICE_STATE, CacheFlag.EMOJI, CacheFlag.CLIENT_STATUS);
        b.setChunkingFilter(ChunkingFilter.ALL);
        b.setActivity(Activity.of(Activity.ActivityType.CUSTOM_STATUS, "Bot is starting..."));
        b.setStatus(OnlineStatus.DO_NOT_DISTURB);
        jda = b.build().awaitReady();
        final ArrayList<Long> knownIDs = Main.iface.getAllServers();
        jda.retrieveCommands().submit().thenAccept((t)->{
            for (Command c : t) {
                jda.deleteCommandById(c.getIdLong()).queue();
            }
        });
        jda.getGuilds().forEach((g) -> {
            if (!knownIDs.contains(g.getIdLong()))
                Main.iface.addServer(g.getIdLong());
            CommandRegistry.setCommands(g);
            try {
                Thread.sleep(100);
            } catch (InterruptedException ignored) {
            }
        });





    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean isAdmin(Member m) {
        if (m == null) return false;
        return m.hasPermission(MANAGE_SERVER);
    }

    private static String getWeaponName(Locale lang, Weapons w) {
        if (w.weapon == null && w.coop_special_weapon != null) {
            return lang.coop_special_weapons.get(w.coop_special_weapon.image).name;
        } else {
            return lang.weapons.get(w.id).name;
        }
    }

    public void sendMessage(String msg, String channelId) {
        sendMessage(new MessageCreateBuilder().setContent(msg).build(), channelId);
    }

    public CompletableFuture<Message> sendMessage(MessageCreateData msg, Long channelId) throws InsufficientPermissionException {
        if (msg == null || channelId == null) return null;
        final GuildMessageChannel channel = (GuildMessageChannel) bot.jda.getGuildChannelById(channelId);
        if (channel != null) return channel.sendMessage(msg).submit();
        return null;
    }

    private CompletableFuture<Message> submitMessage(MessageCreateData msg, Long channelId) throws InsufficientPermissionException {
        if (msg == null || channelId == null) return null;
        final GuildMessageChannel channel = (GuildMessageChannel) bot.jda.getGuildChannelById(channelId);
        if (channel != null) return channel.sendMessage(msg).submit();
        return null;
    }

    public void sendMessage(MessageCreateData msg, String channelId) {
        if (msg == null || channelId == null) return;
        final GuildMessageChannel channel = (GuildMessageChannel) bot.jda.getGuildChannelById(channelId);
        if (channel != null) channel.sendMessage(msg).queue();
    }

    @Override
    public void onEvent(@NotNull GenericEvent event) {

        if (event instanceof final CommandAutoCompleteInteractionEvent ev) {
            final Locale lang = Main.translations.get(Main.iface.getServerLang(ev.getGuild().getIdLong()));
            switch (CommandRegistry.registeredCommands.get(ev.getCommandIdLong()).getName()) {
                case "splatnet2" -> {
                    final ArrayList<Command.Choice> choices = new ArrayList<>();
                    int count = 0;
                    for (String key : lang.allGears.keySet()) {
                        final String name = lang.allGears.get(key);
                        if (name.toLowerCase().contains(ev.getFocusedOption().getValue().toLowerCase())) {
                            choices.add(new Command.Choice(name, key));
                            count++;
                            if (count >= 20) break;
                        }
                    }
                    ev.replyChoices(choices).queue();
                }
                case "splatnet3" -> {
                    final ArrayList<Command.Choice> choices3 = new ArrayList<>();
                    int count3 = 0;
                    for (Gear g : LInk3.getAllGear()) {
                        final String key = g.name;
                        final String name = g.localizedName.get(lang.botLocale.locale.replace("-", "_"));
                        if (name.toLowerCase().contains(ev.getFocusedOption().getValue().toLowerCase())) {
                            choices3.add(new Command.Choice(name, key));
                            count3++;
                            if (count3 >= 20) break;
                        }
                    }
                    ev.replyChoices(choices3).queue();
                }
                case "editprofile" -> {
                    switch (ev.getFocusedOption().getName()) {
                        case "splatfest-team":
                            final ArrayList<Command.Choice> choices = new ArrayList<>();
                            final SplatfestRegion[] regions = new SplatfestRegion[]{ScheduleUtil.getSplatfestData().US, ScheduleUtil.getSplatfestData().EU, ScheduleUtil.getSplatfestData().JP, ScheduleUtil.getSplatfestData().AP};
                            int count = 0;
                            for (final SplatfestRegion r : regions) {
                                for (final FestRecord fest : r.data.festRecords.nodes) {
                                    Locale dispLang = lang;
                                    if (!lang.s3locales.festivals.containsKey(fest.getSplatfestID())) {
                                        for (Locale lan : Main.translations.values()) {
                                            if (lan.s3locales.festivals.containsKey(fest.getSplatfestID()))
                                                dispLang = lan;
                                            break;
                                        }
                                        if (!dispLang.s3locales.festivals.containsKey(fest.getSplatfestID())) continue;
                                    }
                                    for (int i = 0; i < fest.teams.length; i++) {
                                        final String dispName = dispLang.s3locales.festivals.get(fest.getSplatfestID()).title + " - " + dispLang.s3locales.festivals.get(fest.getSplatfestID()).teams[i].teamName;
                                        if (dispName.toLowerCase().contains(ev.getFocusedOption().getValue().toLowerCase())) {
                                            choices.add(new Command.Choice(dispName, fest.getSplatfestID() + ";" + i));
                                            count++;
                                            if (count >= 23) {
                                                ev.replyChoices(choices).queue();
                                                return;
                                            }
                                        }
                                    }
                                }
                            }
                            ev.replyChoices(choices).queue();
                    }
                }
                case "splatfest", "splatfestdebug" -> {
                    final ArrayList<Command.Choice> choices = new ArrayList<>();
                    final SplatfestRegion[] regions = new SplatfestRegion[]{ScheduleUtil.getSplatfestData().US, ScheduleUtil.getSplatfestData().EU, ScheduleUtil.getSplatfestData().JP, ScheduleUtil.getSplatfestData().AP};
                    int count = 0;
                    for (int region = 0; region < regions.length; region++) {
                        final SplatfestRegion r = regions[region];
                        String regionString = switch (region) {
                            case 0 -> "US";
                            case 1 -> "Europe";
                            case 2 -> "Japan";
                            case 3 -> "Hong Kong, S. Korea";
                            default -> "Unknown Region";
                        };
                        for (final FestRecord fest : r.data.festRecords.nodes) {
                            Locale dispLang = lang;
                            if (!lang.s3locales.festivals.containsKey(fest.getSplatfestID())) {
                                for (Locale lan : Main.translations.values()) {
                                    if (lan.s3locales.festivals.containsKey(fest.getSplatfestID())) {
                                        dispLang = lan;
                                        break;
                                    }
                                }
                                if (!dispLang.s3locales.festivals.containsKey(fest.getSplatfestID())) continue;
                            }
                            final String dispName = dispLang.s3locales.festivals.get(fest.getSplatfestID()).title + " (" + regionString + ")";
                            if (dispName.toLowerCase().contains(ev.getFocusedOption().getValue().toLowerCase())) {
                                choices.add(new Command.Choice(dispName, fest.getSplatfestID()));
                                count++;
                                if (count >= 23) {
                                    ev.replyChoices(choices).queue();
                                    return;
                                }
                            }
                        }
                    }
                    ev.replyChoices(choices).queue();
                }
            }
        } else if (event instanceof EntitySelectInteractionEvent ev) {
            switch (ev.getComponentId()) {
                case "s1channel" -> {
                    if (checkPerms(ev)) return;
                    Main.iface.setS1PStageChannel(ev.getGuild().getIdLong(), ev.getValues().get(0).getIdLong());
                    MessageUtil.sendS1RotationFeed(ev.getGuild().getIdLong(), ev.getValues().get(0).getIdLong(), Main.s1rotations.root.Phases[RotationTimingUtil.getRotationForInstant(Instant.now(), Main.s1rotations)]);
                    ev.getInteraction().deferEdit().queue();
                }
                /*case "s1channelPretendo" -> {
                    if (checkPerms(ev)) return;
                    Main.iface.setS1PStageChannel(ev.getGuild().getIdLong(), ev.getValues().get(0).getIdLong());
                    MessageUtil.sendS1PRotationFeed(ev.getGuild().getIdLong(), ev.getValues().get(0).getIdLong(), Main.s1rotations.root.Phases[RotationTimingUtil.getRotationForInstant(Instant.now(), Main.s1rotationsPretendo)]);
                    ev.getInteraction().deferEdit().queue();
                }*/
                case "s2channel" -> {
                    if (checkPerms(ev)) return;
                    Main.iface.setS2StageChannel(ev.getGuild().getIdLong(), ev.getValues().get(0).getIdLong());
                    MessageUtil.sendS2RotationFeed(ev.getGuild().getIdLong(), ev.getValues().get(0).getIdLong(), ScheduleUtil.getCurrentRotation());
                    ev.getInteraction().deferEdit().queue();
                }
                case "s3channel" -> {
                    if (checkPerms(ev)) return;
                    Main.iface.setS3StageChannel(ev.getGuild().getIdLong(), ev.getValues().get(0).getIdLong());
                    MessageUtil.sendS3RotationFeed(ev.getGuild().getIdLong(), ev.getValues().get(0).getIdLong(), ScheduleUtil.getCurrentS3Rotation());
                    ev.getInteraction().deferEdit().queue();
                }
                case "s2salmon" -> {
                    if (checkPerms(ev)) return;
                    Main.iface.setSalmonChannel(ev.getGuild().getIdLong(), ev.getValues().get(0).getIdLong());
                    MessageUtil.sendSalmonFeed(ev.getGuild().getIdLong(), ev.getValues().get(0).getIdLong());
                    ev.getInteraction().deferEdit().queue();
                }
                case "s3salmon" -> {
                    if (checkPerms(ev)) return;
                    Main.iface.setS3SalmonChannel(ev.getGuild().getIdLong(), ev.getValues().get(0).getIdLong());
                    MessageUtil.sendS3SalmonFeed(ev.getGuild().getIdLong(), ev.getValues().get(0).getIdLong());
                    ev.getInteraction().deferEdit().queue();

                }
                case "s3event" -> {
                    if (checkPerms(ev)) return;
                    Main.iface.setS3EventChannel(ev.getGuild().getIdLong(), ev.getValues().get(0).getIdLong());
                    MessageUtil.sendS3EventRotationFeed(ev.getGuild().getIdLong(), ev.getValues().get(0).getIdLong(), ScheduleUtil.getCurrentS3Rotation());
                    ev.getInteraction().deferEdit().queue();
                }
                default -> ev.reply("Selected " + ev.getValues().get(0).getAsMention()).setEphemeral(true).queue();
            }
        } else if (event instanceof StringSelectInteractionEvent ev) {
            switch (ev.getComponentId()) {
                case "language" -> {
                    System.out.println(ev.getInteraction().getValues().get(0) + " language");
                    Main.iface.setServerLang(ev.getGuild().getIdLong(), BotLanguage.fromInt(Integer.parseInt(ev.getInteraction().getValues().get(0))));
                    ev.getInteraction().editMessage(SettingsCommand.getMenu("generic", ev.getGuild().getIdLong())).queue();
                    CommandRegistry.setCommands(ev.getGuild());
                    return;
                }
                case "msgdelete" -> {
                    final boolean deleteMsgs = ev.getValues().get(0).equals("yes");
                    Main.iface.setDeleteMessage(ev.getGuild().getIdLong(), deleteMsgs);
                    ev.getInteraction().deferEdit().queue();
                    return;
                }
                case "s1customSplatfest" -> {
                    final boolean enabled = ev.getValues().get(0).equals("yes");
                    Main.iface.setCustomSplatfests(ev.getGuild().getIdLong(), enabled);
                    ev.getInteraction().deferEdit().queue();
                    return;
                }
            }
            if (ev.getComponentId().equals("settingSel")) {
                ev.getInteraction().deferEdit().submit().thenAccept((a) -> a.editOriginal((SettingsCommand.getMenu(ev.getInteraction().getValues().get(0), ev.getGuild().getIdLong()))).queue());
            } else
                ev.reply("Selected " + ev.getValues().get(0)).setEphemeral(true).queue();
        } else if (event instanceof final GuildJoinEvent ev) {
            Main.iface.addServer(ev.getGuild().getIdLong());
            CommandRegistry.setCommands(ev.getGuild());
        } else if (event instanceof UnavailableGuildJoinedEvent) {
            Main.iface.addServer(((UnavailableGuildJoinedEvent) event).getGuildIdLong());
        } else if (event instanceof GuildAvailableEvent) {
            CommandRegistry.setCommands(((GuildAvailableEvent) event).getGuild());
        } else if (event instanceof GuildLeaveEvent)
            Main.iface.delServer(((GuildLeaveEvent) event).getGuild().getIdLong());
        else if (event instanceof UnavailableGuildLeaveEvent)
            Main.iface.delServer(((UnavailableGuildLeaveEvent) event).getGuildIdLong());
        else if (event instanceof SlashCommandInteractionEvent ev) {
            final Command cmd = CommandRegistry.registeredCommands.get(ev.getCommandIdLong());
            if (cmd != null) {
                if (!Main.iface.status.isDBAlive() && !(cmd.getName().equals("status") || cmd.getName().equals("support"))) {
                    ev.reply(Main.translations.get(BotLanguage.ENGLISH).botLocale.databaseError).queue();
                    return;
                }
                final BaseCommand baseCmd = CommandRegistry.getCommandByName(cmd.getName());
                if (baseCmd != null)
                    if ((baseCmd.requiresManageServer() && ev.getMember().hasPermission(MANAGE_SERVER)) || !baseCmd.requiresManageServer())
                        baseCmd.execute(ev);
                    else {
                        final Locale lang = Main.translations.get(Main.iface.getServerLang(ev.getGuild().getIdLong()));
                        ev.deferReply(true).setContent(lang.botLocale.noAdminPerms).queue();
                    }
            }

        } else if (event instanceof final ButtonInteractionEvent ev) {
            final Locale lang = Main.translations.get(Main.iface.getServerLang(ev.getGuild().getIdLong()));
            switch (ev.getComponentId()) {
                case "s1clear" -> {
                    Main.iface.setS1StageChannel(ev.getGuild().getIdLong(), null);
                    Main.iface.setS1PStageChannel(ev.getGuild().getIdLong(), null);
                    ev.getInteraction().deferEdit().queue();
                }
                case "s2clear" -> {
                    Main.iface.setS2StageChannel(ev.getGuild().getIdLong(), null);
                    ev.getInteraction().deferEdit().queue();
                }
                case "s3clear" -> {
                    Main.iface.setS3StageChannel(ev.getGuild().getIdLong(), null);
                    ev.getInteraction().deferEdit().queue();
                }
                case "s2clears" -> {
                    Main.iface.setSalmonChannel(ev.getGuild().getIdLong(), null);
                    ev.getInteraction().deferEdit().queue();
                }
                case "s3clears" -> {
                    Main.iface.setS3SalmonChannel(ev.getGuild().getIdLong(), null);
                    ev.getInteraction().deferEdit().queue();
                }
                case "s3cleare" -> {
                    Main.iface.setS3EventChannel(ev.getGuild().getIdLong(), null);
                    ev.getInteraction().deferEdit().queue();
                }
                case "delete" -> {
                    if (ev.getMessage().getInteraction().getUser().getIdLong() == ev.getUser().getIdLong()) {
                        ev.getInteraction().deferEdit().submit().thenAccept((m) -> m.deleteOriginal().queue());
                    }
                }
                case "regenprivate" -> {
                    PrivateCommand.generatePrivate(ev);
                }
            }
            if (ev.getComponentId().startsWith("loadmore")) {
                final EmbedBuilder b = new EmbedBuilder();
                final EmbedBuilder b2 = new EmbedBuilder();
                long time = System.currentTimeMillis() / 1000;
                time += (TimeUnit.HOURS.toSeconds(2) + 1) * 3;
                final CompletableFuture<InteractionHook> submit = ev.deferReply(true).submit();
                if (ev.getComponentId().equals("loadmore3")) {
                    for (int i = 0; i < 4; i++) {
                        time += TimeUnit.HOURS.toSeconds(2) + 1;
                        RotationCommand.addS3Rotation(b, ScheduleUtil.getS3RotationForTimestamp(time), lang);
                        if (i < 3)
                            b.addBlankField(false);
                    }
                    for (int i = 4; i < 8; i++) {
                        time += TimeUnit.HOURS.toSeconds(2) + 1;
                        RotationCommand.addS3Rotation(b2, ScheduleUtil.getS3RotationForTimestamp(time), lang);
                        if (i < 7)
                            b2.addBlankField(false);
                    }

                }
                if (ev.getComponentId().equals("loadmore2")) {
                    for (int i = 0; i < 4; i++) {
                        time += TimeUnit.HOURS.toSeconds(2) + 1;
                        RotationCommand.addS2Rotation(b, ScheduleUtil.getS2RotationForTimestamp(time), lang);
                        if (i < 4 - 1)
                            b.addBlankField(false);
                    }
                    for (int i = 4; i < 8; i++) {
                        time += TimeUnit.HOURS.toSeconds(2) + 1;
                        RotationCommand.addS2Rotation(b2, ScheduleUtil.getS2RotationForTimestamp(time), lang);
                        if (i < 8 - 1)
                            b2.addBlankField(false);
                    }
                }
                submit.thenAccept((m) -> {
                    m.editOriginalEmbeds(b.build(), b2.build()).queue();
                });
            } else if (ev.getComponentId().startsWith("snet3next")) {
                int targetPage = Integer.parseInt(ev.getComponentId().replace("snet3next", ""));

                Button b = Button.secondary("snet3next" + (targetPage + 1), Emoji.fromUnicode("U+25B6"));
                if (Main.splatNet3.data.gesotown.limitedGears.length < (targetPage + 1) * 3) b = b.asDisabled();

                ev.editMessageEmbeds(Splatnet3Command.saleEmbeds(lang, targetPage - 1))
                        .setActionRow(
                                Button.danger("delete", Emoji.fromUnicode("U+1F5D1")),
                                Button.secondary("snet3prev" + (targetPage - 1), Emoji.fromUnicode("U+25C0")),
                                b).queue();
            } else if (ev.getComponentId().startsWith("snet3prev")) {
                int targetPage = Integer.parseInt(ev.getComponentId().replace("snet3prev", ""));

                Button b = Button.secondary("snet3prev" + (targetPage - 1), Emoji.fromUnicode("U+25C0"));
                if (targetPage == 0) b = b.asDisabled();
                ev.editMessageEmbeds(targetPage == 0 ? Splatnet3Command.dailyEmbeds(lang) : Splatnet3Command.saleEmbeds(lang, targetPage - 1))
                        .setActionRow(
                                Button.danger("delete", Emoji.fromUnicode("U+1F5D1")),
                                b,
                                Button.secondary("snet3next" + (targetPage + 1), Emoji.fromUnicode("U+25B6"))).queue();
            }
        } else if (event instanceof RoleUpdatePermissionsEvent) {
            //Update command permissions on role creation / permission change
            CommandRegistry.setCommands(((RoleUpdatePermissionsEvent) event).getGuild());
        } else if (event instanceof RoleCreateEvent)
            CommandRegistry.setCommands(((RoleCreateEvent) event).getGuild());
    }

    private boolean checkPerms(final EntitySelectInteractionEvent ev) {
        final Locale lang = Main.translations.get(Main.iface.getServerLang(ev.getGuild().getIdLong()));
        if (!ev.getGuild().getMember(ev.getJDA().getSelfUser()).hasPermission(ev.getGuild().getChannelById(GuildMessageChannel.class, ev.getValues().get(0).getIdLong()), Permission.MESSAGE_SEND)) {
            ev.reply(lang.botLocale.noWritePerms).queue();
            return true;
        }
        return false;
    }

    public Map.Entry<Long, Long> sendS2SalmonMessage(long serverid, long channel) throws InsufficientPermissionException, ExecutionException, InterruptedException {
        final Locale lang = Main.translations.get(Main.iface.getServerLang(serverid));
        final CompletableFuture<Message> submitMsg = submitMessage(new MessageCreateBuilder().setEmbeds(new EmbedBuilder().setTitle(lang.botLocale.salmonRunTitle + " (Splatoon 2)")
                        .addField(lang.botLocale.salmonStage, lang.coop_stages.get(Main.coop_schedules.details[0].stage.image).getName(), true)
                        .addField(lang.botLocale.weapons,
                                getWeaponName(lang, Main.coop_schedules.details[0].weapons[0]) + ", " +
                                        getWeaponName(lang, Main.coop_schedules.details[0].weapons[1]) + ", " +
                                        getWeaponName(lang, Main.coop_schedules.details[0].weapons[2]) + ", " +
                                        getWeaponName(lang, Main.coop_schedules.details[0].weapons[3])
                                , true)
                        .setImage(Main.coop_schedules.details[0].outImageURL)
                        .setFooter(lang.botLocale.footer_ends)
                        .setTimestamp(Instant.ofEpochSecond(Main.coop_schedules.details[0].end_time))
                        .build()
                ).build(),
                channel);
        if (submitMsg != null)
            return new AbstractMap.SimpleEntry<>(serverid, submitMsg.get().getIdLong());
        else return null;
    }

    public Map.Entry<Long, Long> sendS3SalmonMessage(long serverid, long channel) throws InsufficientPermissionException, ExecutionException, InterruptedException {
        final S3Rotation currentS3Rotation = ScheduleUtil.getCurrentS3Rotation();
        Locale lang = Main.translations.get(Main.iface.getServerLang(serverid));
        final GuildMessageChannel ch = jda.getChannelById(GuildMessageChannel.class, channel);
        CompletableFuture<Message> submitMsg = null;
        if (ch != null) {
            final String prediction = switch (currentS3Rotation.getCoop().__splatoon3ink_king_salmonid_guess) {
                case "Cohozuna" -> String.valueOf(Emote.COHOZUNA);
                case "Horrorboros" -> String.valueOf(Emote.HORRORBOROS);
                case "Megalodontia" -> String.valueOf(Emote.MEGALODONTIA);
                case "Triumvirate" -> String.valueOf(Emote.TRIUMVIRATE);
                default -> String.valueOf(Emote.ERROR_CONTACT_DEVELOPER);
            };
            final ArrayList<MessageEmbed> embeds = new ArrayList<>();
            embeds.add(new EmbedBuilder().setTitle(lang.botLocale.salmonRunTitle + " (Splatoon 3)")
                    .addField(lang.botLocale.salmonStage, lang.s3locales.stages.get(currentS3Rotation.getCoop().setting.coopStage.id).name, true)
                    .addField(lang.botLocale.weapons,
                            lang.s3locales.weapons.get(currentS3Rotation.getCoop().setting.weapons[0].__splatoon3ink_id).name + ", " +
                                    lang.s3locales.weapons.get(currentS3Rotation.getCoop().setting.weapons[1].__splatoon3ink_id).name + ", " +
                                    lang.s3locales.weapons.get(currentS3Rotation.getCoop().setting.weapons[2].__splatoon3ink_id).name + ", " +
                                    lang.s3locales.weapons.get(currentS3Rotation.getCoop().setting.weapons[3].__splatoon3ink_id).name
                            , true)
                    .setImage(currentS3Rotation.getCoop().outImageURL)
                    .setDescription(lang.botLocale.salmonPrediction + prediction)
                    .setFooter(lang.botLocale.footer_ends)
                    .setTimestamp(Instant.ofEpochSecond(currentS3Rotation.getCoop().getEndTime()))
                    .build());
            if (currentS3Rotation.getEggstraCoop() != null)
                embeds.add(new EmbedBuilder().setTitle(lang.botLocale.eggstraTitle + " (Splatoon 3)")
                        .addField(lang.botLocale.salmonStage, lang.s3locales.stages.get(currentS3Rotation.getEggstraCoop().setting.coopStage.id).name, true)
                        .addField(lang.botLocale.weapons,
                                lang.s3locales.weapons.get(currentS3Rotation.getEggstraCoop().setting.weapons[0].__splatoon3ink_id).name + ", " +
                                        lang.s3locales.weapons.get(currentS3Rotation.getEggstraCoop().setting.weapons[1].__splatoon3ink_id).name + ", " +
                                        lang.s3locales.weapons.get(currentS3Rotation.getEggstraCoop().setting.weapons[2].__splatoon3ink_id).name + ", " +
                                        lang.s3locales.weapons.get(currentS3Rotation.getEggstraCoop().setting.weapons[3].__splatoon3ink_id).name
                                , true)
                        .setImage(currentS3Rotation.getEggstraCoop().outImageURL)
                        .setFooter(lang.botLocale.footer_ends)
                        .setTimestamp(Instant.ofEpochSecond(currentS3Rotation.getEggstraCoop().getEndTime()))
                        .build());
            submitMsg = ch.sendMessage(new MessageCreateBuilder().setEmbeds(embeds).build()).submit();
        }
        if (submitMsg != null)
            return new AbstractMap.SimpleEntry<>(serverid, submitMsg.get().getIdLong());
        else return null;
    }

}
