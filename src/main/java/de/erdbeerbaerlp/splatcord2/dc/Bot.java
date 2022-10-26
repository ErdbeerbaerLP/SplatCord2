package de.erdbeerbaerlp.splatcord2.dc;

import de.erdbeerbaerlp.splatcord2.Main;
import de.erdbeerbaerlp.splatcord2.commands.BaseCommand;
import de.erdbeerbaerlp.splatcord2.commands.PrivateCommand;
import de.erdbeerbaerlp.splatcord2.commands.RotationCommand;
import de.erdbeerbaerlp.splatcord2.storage.BotLanguage;
import de.erdbeerbaerlp.splatcord2.storage.CommandRegistry;
import de.erdbeerbaerlp.splatcord2.storage.Config;
import de.erdbeerbaerlp.splatcord2.storage.S3Rotation;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon2.coop_schedules.Weapons;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon2.translations.Locale;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon2.translations.Weapon;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon3.splatfest.FestRecord;
import de.erdbeerbaerlp.splatcord2.util.ScheduleUtil;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.guild.*;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.role.RoleCreateEvent;
import net.dv8tion.jda.api.events.role.update.RoleUpdatePermissionsEvent;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.hooks.EventListener;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.FileUpload;
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
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static net.dv8tion.jda.api.Permission.MANAGE_SERVER;

public class Bot implements EventListener {
    public final JDA jda;
    @SuppressWarnings("FieldCanBeLocal")
    private final StatusUpdater presence;

    public Bot() throws LoginException, InterruptedException {
        CommandRegistry.registerAllBaseCommands();
        JDABuilder b = JDABuilder.create(Config.instance().discord.token, GatewayIntent.GUILD_MESSAGES).addEventListeners(this);
        b.setMemberCachePolicy(MemberCachePolicy.DEFAULT);
        b.setAutoReconnect(true);
        b.disableCache(CacheFlag.ONLINE_STATUS);
        b.disableCache(CacheFlag.ACTIVITY, CacheFlag.VOICE_STATE, CacheFlag.EMOJI, CacheFlag.CLIENT_STATUS);
        b.setChunkingFilter(ChunkingFilter.ALL);
        jda = b.build().awaitReady();
        presence = new StatusUpdater();
        presence.start();

        final ArrayList<Long> knownIDs = Main.iface.getAllServers();
        jda.getGuilds().forEach((g) -> {
            if (!knownIDs.contains(g.getIdLong()))
                Main.iface.addServer(g.getIdLong());
            CommandRegistry.setCommands(g);
            try {
                Thread.sleep(50);
            } catch (InterruptedException ignored) {
            }
        });

    }

    public void sendMessage(String msg, String channelId) {
        sendMessage(new MessageCreateBuilder().setContent(msg).build(), channelId);
    }

    public CompletableFuture<Message> sendMessage(MessageCreateData msg, Long channelId) throws InsufficientPermissionException {
        if (msg == null || channelId == null) return null;
        final TextChannel channel = jda.getTextChannelById(channelId);
        if (channel != null) return channel.sendMessage(msg).submit();
        return null;
    }

    private CompletableFuture<Message> submitMessage(MessageCreateData msg, Long channelId) throws InsufficientPermissionException {
        if (msg == null || channelId == null) return null;
        final TextChannel channel = jda.getTextChannelById(channelId);
        if (channel != null) return channel.sendMessage(msg).submit();
        return null;
    }

    public void sendMessage(MessageCreateData msg, String channelId) {
        if (msg == null || channelId == null) return;
        final TextChannel channel = jda.getTextChannelById(channelId);
        if (channel != null) channel.sendMessage(msg).queue();
    }

    @Override
    public void onEvent(@NotNull GenericEvent event) {

        if (event instanceof final CommandAutoCompleteInteractionEvent ev) {
            final Locale lang = Main.translations.get(Main.iface.getServerLang(ev.getGuild().getIdLong()));
            switch (CommandRegistry.registeredCommands.get(ev.getCommandIdLong()).getName()) {
                case "splatnet2":
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
                    break;
                case "splatnet3":
                    final ArrayList<Command.Choice> choices3 = new ArrayList<>();
                    int count3 = 0;
                    for (String key : lang.s3locales.gear.keySet()) {
                        final String name = lang.s3locales.gear.get(key).name;
                        if (name.toLowerCase().contains(ev.getFocusedOption().getValue().toLowerCase())) {
                            choices3.add(new Command.Choice(name, key));
                            count3++;
                            if (count3 >= 20) break;
                        }
                    }
                    ev.replyChoices(choices3).queue();
                    break;
                case "editprofile":
                    switch (ev.getFocusedOption().getName()) {
                        case "main1":
                        case "main2":
                            final ArrayList<Command.Choice> weapons = new ArrayList<>();
                            int count2 = 0;
                            for (Integer key : lang.weapons.keySet()) {
                                final Weapon wp = lang.weapons.get(key);
                                if (wp.name.toLowerCase().contains(ev.getFocusedOption().getValue().toLowerCase())) {
                                    weapons.add(new Command.Choice(wp.name, key));
                                    count2++;
                                    if (count2 >= 20) break;
                                }
                            }
                            ev.replyChoices(weapons).queue();
                            break;
                    }
                    break;
                case "splatfest":
                    final ArrayList<Command.Choice> splatfests = new ArrayList<>();
                    int countsplatfest = 0;
                    for (FestRecord f : ScheduleUtil.getSplatfestData().US.data.festRecords.nodes) {
                        final String title = lang.botLocale.getSplatfestTitle(f.getSplatfestID());
                        if (title.toLowerCase().contains(ev.getFocusedOption().getValue().toLowerCase())) {
                            splatfests.add(new Command.Choice(title, f.getSplatfestID()));
                            countsplatfest++;
                            if (countsplatfest >= 20) break;
                        }
                    }
                    ev.replyChoices(splatfests).queue();
                    break;
            }
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
            if (ev.getChannelType() != ChannelType.TEXT) return;
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

            if (ev.getComponentId().equals("delete")) {
                if (ev.getMessage().getInteraction().getUser().getIdLong() == ev.getUser().getIdLong())
                    ev.getMessage().delete().queue();
            } else if (ev.getComponentId().equals("regenprivate")) {
                PrivateCommand.generatePrivate(ev);
            } else if (ev.getComponentId().startsWith("loadmore")) {
                final EmbedBuilder b = new EmbedBuilder();
                long time = System.currentTimeMillis() / 1000;
                time += (TimeUnit.HOURS.toSeconds(2) + 1) * 3;
                final CompletableFuture<InteractionHook> submit = ev.deferReply(true).submit();

                if (ev.getComponentId().equals("loadmore3")) {
                    for (int i = 0; i < 6; i++) {
                        time += TimeUnit.HOURS.toSeconds(2) + 1;
                        RotationCommand.addS3Rotation(b, ScheduleUtil.getS3RotationForTimestamp(time), lang);
                        if (i < 5)
                            b.addBlankField(false);
                    }
                }
                if (ev.getComponentId().equals("loadmore2")) {
                    for (int i = 0; i < 6; i++) {
                        time += TimeUnit.HOURS.toSeconds(2) + 1;
                        RotationCommand.addS2Rotation(b, ScheduleUtil.getS2RotationForTimestamp(time), lang);
                        if (i < 6 - 1)
                            b.addBlankField(false);
                    }
                }

                submit.thenAccept((m) -> {
                    m.editOriginalEmbeds(b.build()).queue();
                });
            }
            //Update command permissions on role creation / permission change
        } else if (event instanceof RoleUpdatePermissionsEvent) {
            CommandRegistry.setCommands(((RoleUpdatePermissionsEvent) event).getGuild());
        } else if (event instanceof RoleCreateEvent)
            CommandRegistry.setCommands(((RoleCreateEvent) event).getGuild());
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

    public Map.Entry<Long, Long> sendS2SalmonMessage(long serverid, long channel) throws InsufficientPermissionException, ExecutionException, InterruptedException {
        Locale lang = Main.translations.get(Main.iface.getServerLang(serverid));
        final CompletableFuture<Message> submitMsg = submitMessage(new MessageCreateBuilder().setEmbeds(new EmbedBuilder().setTitle(lang.botLocale.salmonRunTitle + " (Splatoon 2)")
                        .addField(lang.botLocale.salmonStage, lang.coop_stages.get(Main.coop_schedules.details[0].stage.image).getName(), true)
                        .addField(lang.botLocale.weapons,
                                getWeaponName(lang, Main.coop_schedules.details[0].weapons[0]) + ", " +
                                        getWeaponName(lang, Main.coop_schedules.details[0].weapons[1]) + ", " +
                                        getWeaponName(lang, Main.coop_schedules.details[0].weapons[2]) + ", " +
                                        getWeaponName(lang, Main.coop_schedules.details[0].weapons[3])
                                , true)
                        .setImage("https://splatoon2.ink/assets/splatnet/" + Main.coop_schedules.details[0].stage.image)
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
        final TextChannel ch = jda.getTextChannelById(channel);
        CompletableFuture<Message> submitMsg = null;
        if (ch != null)
            submitMsg = ch.sendMessage(new MessageCreateBuilder().setEmbeds(new EmbedBuilder().setTitle(lang.botLocale.salmonRunTitle + " (Splatoon 3)")
                    .addField(lang.botLocale.salmonStage, lang.s3locales.stages.get(currentS3Rotation.getCoop().setting.coopStage.id).name, true)
                    .addField(lang.botLocale.weapons,
                            lang.s3locales.weapons.get(currentS3Rotation.getCoop().setting.weapons[0].__splatoon3ink_id).name + ", " +
                                    lang.s3locales.weapons.get(currentS3Rotation.getCoop().setting.weapons[1].__splatoon3ink_id).name + ", " +
                                    lang.s3locales.weapons.get(currentS3Rotation.getCoop().setting.weapons[2].__splatoon3ink_id).name + ", " +
                                    lang.s3locales.weapons.get(currentS3Rotation.getCoop().setting.weapons[3].__splatoon3ink_id).name
                            , true)
                    .setImage("attachment://current.png")
                    .setFooter(lang.botLocale.footer_ends)
                    .setTimestamp(Instant.ofEpochSecond(currentS3Rotation.getCoop().getEndTime()))
                    .build()
            ).build()).addFiles(FileUpload.fromData(currentS3Rotation.getCoop().outImage, "current.png")).submit();
        if (submitMsg != null)
            return new AbstractMap.SimpleEntry<>(serverid, submitMsg.get().getIdLong());
        else return null;
    }


    private class StatusUpdater extends Thread {
        int presence = 0;
        final Random r = new Random();

        @Override
        public void run() {
            while (true) {
                final Config.Discord.Status s = Config.instance().discord.botStatus.get(presence);
                jda.getPresence().setPresence(Main.iface.status.isDBAlive() ? OnlineStatus.ONLINE : OnlineStatus.DO_NOT_DISTURB, Activity.of(s.type, s.message.replace("%servercount%", jda.getGuilds().size() + ""), s.streamingURL), false);
                try {
                    //noinspection BusyWait
                    sleep(1000 * (r.nextInt(29) + 2));
                } catch (InterruptedException e) {
                    return;
                }
                presence++;
                if (presence >
                        Config.instance().discord.botStatus.size() - 1) presence = 0;
            }
        }
    }

}
