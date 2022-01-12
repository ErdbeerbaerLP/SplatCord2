package de.erdbeerbaerlp.splatcord2.dc;

import de.erdbeerbaerlp.splatcord2.Main;
import de.erdbeerbaerlp.splatcord2.commands.BaseCommand;
import de.erdbeerbaerlp.splatcord2.storage.CommandRegistry;
import de.erdbeerbaerlp.splatcord2.storage.BotLanguage;
import de.erdbeerbaerlp.splatcord2.storage.Config;
import de.erdbeerbaerlp.splatcord2.storage.Rotation;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon2.coop_schedules.Weapons;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon2.tentaworld.Gear;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon2.tentaworld.Merchandise;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon2.tentaworld.TentaWorld;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon2.translations.Locale;
import de.erdbeerbaerlp.splatcord2.util.MessageUtil;
import de.erdbeerbaerlp.splatcord2.util.ScheduleUtil;
import net.dv8tion.jda.api.*;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.guild.*;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.role.RoleCreateEvent;
import net.dv8tion.jda.api.events.role.update.RoleUpdatePermissionsEvent;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.hooks.EventListener;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.jetbrains.annotations.NotNull;

import javax.net.ssl.HttpsURLConnection;
import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

public class Bot implements EventListener {
    public final JDA jda;
    @SuppressWarnings("FieldCanBeLocal")
    private final StatusUpdater presence;
    public static final HashMap<Long, Long> splatnetCooldown = new HashMap<>();

    public Bot() throws LoginException, InterruptedException {
        CommandRegistry.registerAllBaseCommands();
        JDABuilder b = JDABuilder.create(Config.instance().discord.token, GatewayIntent.GUILD_MESSAGES).addEventListeners(this);
        b.setMemberCachePolicy(MemberCachePolicy.DEFAULT);
        b.setAutoReconnect(true);
        b.disableCache(CacheFlag.ONLINE_STATUS);
        b.disableCache(CacheFlag.ACTIVITY, CacheFlag.VOICE_STATE, CacheFlag.EMOTE, CacheFlag.CLIENT_STATUS);
        b.setChunkingFilter(ChunkingFilter.ALL);
        jda = b.build().awaitReady();
        presence = new StatusUpdater();
        presence.start();

        final ArrayList<Long> knownIDs = Main.iface.getAllServers();
        jda.getGuilds().forEach((g) -> {
            if (!knownIDs.contains(g.getIdLong()))
                Main.iface.addServer(g.getIdLong());
            CommandRegistry.setCommands(g);
        });

    }

    public void sendMessage(String msg, String channelId) {
        sendMessage(new MessageBuilder().setContent(msg).build(), channelId);
    }

    public CompletableFuture<Message> sendMessage(Message msg, Long channelId) throws InsufficientPermissionException {
        if (msg == null || channelId == null) return null;
        final TextChannel channel = jda.getTextChannelById(channelId);
        if (channel != null) return channel.sendMessage(msg).submit();
        return null;
    }

    private CompletableFuture<Message> submitMessage(Message msg, Long channelId) throws InsufficientPermissionException{
        if (msg == null || channelId == null) return null;
        final TextChannel channel = jda.getTextChannelById(channelId);
        if (channel != null) return channel.sendMessage(msg).submit();
        return null;
    }

    public void sendMessage(Message msg, String channelId) {
        if (msg == null || channelId == null) return;
        final TextChannel channel = jda.getTextChannelById(channelId);
        if (channel != null) channel.sendMessage(msg).queue();
    }

    @Override
    public void onEvent(@NotNull GenericEvent event) {

        //Guild joining
        if (event instanceof GuildJoinEvent) {
            final GuildJoinEvent guildJoinEvent = (GuildJoinEvent) event;
            Main.iface.addServer(((GuildJoinEvent) event).getGuild().getIdLong());
            CommandRegistry.setCommands(guildJoinEvent.getGuild());
        }
        if (event instanceof UnavailableGuildJoinedEvent) {
            Main.iface.addServer(((UnavailableGuildJoinedEvent) event).getGuildIdLong());
        }
        if(event instanceof GuildAvailableEvent){
            CommandRegistry.setCommands(((GuildAvailableEvent) event).getGuild());
        }

        //Guild leaving
        if (event instanceof GuildLeaveEvent) Main.iface.delServer(((GuildLeaveEvent) event).getGuild().getIdLong());
        if (event instanceof UnavailableGuildLeaveEvent)
            Main.iface.delServer(((UnavailableGuildLeaveEvent) event).getGuildIdLong());


        // /slash commands
        if (event instanceof SlashCommandEvent) {
            SlashCommandEvent ev = (SlashCommandEvent) event;
            if (ev.getChannelType() != ChannelType.TEXT) return;
            final Command cmd = CommandRegistry.registeredCommands.get(ev.getCommandIdLong());
            if (cmd != null) {
                if(!Main.iface.status.isDBAlive() && !(cmd.getName().equals("status") || cmd.getName().equals("support"))){
                    ev.reply(Main.translations.get(BotLanguage.ENGLISH).botLocale.databaseError).queue();
                    return;
                }
                final BaseCommand baseCmd = CommandRegistry.getCommandByName(cmd.getName());
                if (baseCmd != null) baseCmd.execute(ev);
            }
        }

        //Update command permissions on role creation / permission change
        if(event instanceof RoleUpdatePermissionsEvent){
            CommandRegistry.setCommands(((RoleUpdatePermissionsEvent) event).getGuild());
        }
        if(event instanceof RoleCreateEvent)
            CommandRegistry.setCommands(((RoleCreateEvent) event).getGuild());
    }


    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean isAdmin(Member m) {
        if (m == null) return false;
        return m.hasPermission(Permission.MANAGE_SERVER);
    }

    private static String getWeaponName(Locale lang, Weapons w) {
        if (w.weapon == null && w.coop_special_weapon != null) {
            return lang.coop_special_weapons.get(w.coop_special_weapon.image).name;
        } else {
            return lang.weapons.get(w.id).name;
        }
    }

    public Map.Entry<Long, Long> sendSalmonMessage(long serverid, long channel) throws InsufficientPermissionException, ExecutionException, InterruptedException {
        Locale lang = Main.translations.get(Main.iface.getServerLang(serverid));
        final CompletableFuture<Message> submitMsg = submitMessage(new MessageBuilder().setEmbeds(new EmbedBuilder().setTitle(lang.botLocale.salmonRunTitle)
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
        if(submitMsg != null)
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
                jda.getPresence().setPresence(Main.iface.status.isDBAlive()?OnlineStatus.ONLINE:OnlineStatus.DO_NOT_DISTURB,Activity.of(s.type, s.message.replace("%servercount%", jda.getGuilds().size() + ""), s.streamingURL), false);
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
