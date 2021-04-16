package de.erdbeerbaerlp.splatcord2.dc;

import de.erdbeerbaerlp.splatcord2.Main;
import de.erdbeerbaerlp.splatcord2.storage.BotLanguage;
import de.erdbeerbaerlp.splatcord2.storage.Config;
import de.erdbeerbaerlp.splatcord2.storage.json.coop_schedules.Weapons;
import de.erdbeerbaerlp.splatcord2.storage.json.translations.Locale;
import net.dv8tion.jda.api.*;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.events.guild.UnavailableGuildJoinedEvent;
import net.dv8tion.jda.api.events.guild.UnavailableGuildLeaveEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.jetbrains.annotations.NotNull;

import javax.security.auth.login.LoginException;
import java.time.Instant;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

public class Bot implements EventListener {
    public final JDA jda;
    @SuppressWarnings("FieldCanBeLocal")
    private final PresenceUpdater presence;

    public Bot() throws LoginException, InterruptedException {
        JDABuilder b = JDABuilder.create(Config.instance().discord.token, GatewayIntent.GUILD_MESSAGES).addEventListeners(this);
        b.setMemberCachePolicy(MemberCachePolicy.DEFAULT);
        b.setAutoReconnect(true);
        b.disableCache(CacheFlag.ACTIVITY, CacheFlag.VOICE_STATE, CacheFlag.EMOTE, CacheFlag.CLIENT_STATUS);
        b.setChunkingFilter(ChunkingFilter.ALL);
        jda = b.build().awaitReady();
        presence = new PresenceUpdater();
        presence.start();

        final ArrayList<Long> knownIDs = Main.iface.getAllServers();
        jda.getGuilds().forEach((g) -> {
            System.out.println(g.getId());
            if (!knownIDs.contains(g.getIdLong()))
                Main.iface.addServer(g.getIdLong());
        });

    }

    public void sendMessage(String msg, String channelId) {
        sendMessage(new MessageBuilder().setContent(msg).build(), channelId);
    }

    private void sendMessage(Message msg, Long channelId) {
        if (msg == null || channelId == null) return;
        final TextChannel channel = jda.getTextChannelById(channelId);
        if (channel != null) channel.sendMessage(msg).queue();
    }
    private CompletableFuture<Message> submitMessage(Message msg, Long channelId) {
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
        if (event instanceof GuildJoinEvent) Main.iface.addServer(((GuildJoinEvent) event).getGuild().getIdLong());
        if (event instanceof UnavailableGuildJoinedEvent)
            Main.iface.addServer(((UnavailableGuildJoinedEvent) event).getGuildIdLong());
        if (event instanceof GuildLeaveEvent) Main.iface.delServer(((GuildLeaveEvent) event).getGuild().getIdLong());
        if (event instanceof UnavailableGuildLeaveEvent)
            Main.iface.delServer(((UnavailableGuildLeaveEvent) event).getGuildIdLong());

        if (event instanceof MessageReceivedEvent) {
            final MessageReceivedEvent ev = ((MessageReceivedEvent) event);
            if (ev.getAuthor().isBot()) return;
            String msg = ev.getMessage().getContentRaw();
            if (msg.startsWith(Config.instance().discord.prefix)) {
                msg = msg.replaceFirst(Pattern.quote(Config.instance().discord.prefix), "").trim();
                final String[] cmd = msg.split(" ");
                Locale lang = Main.translations.get(Main.iface.getServerLang(ev.getGuild().getIdLong()));
                if (cmd.length > 0) {
                    switch (cmd[0].toLowerCase()) {
                        case "setlang":
                            if (!isAdmin(ev.getMember())) {
                                sendMessage(lang.botLocale.noAdminPerms, ev.getChannel().getId());
                                break;
                            }
                            if (cmd.length > 1)
                                switch (cmd[1].toLowerCase()) {
                                    case "deutsch":
                                    case "german":
                                    case "de":
                                        Main.iface.setServerLang(ev.getGuild().getIdLong(), BotLanguage.GERMAN);
                                        lang = Main.translations.get(Main.iface.getServerLang(ev.getGuild().getIdLong()));
                                        sendMessage(lang.botLocale.languageSetMessage, ev.getChannel().getId());
                                        break;
                                    case "english":
                                    case "englisch":
                                    case "en":
                                        Main.iface.setServerLang(ev.getGuild().getIdLong(), BotLanguage.ENGLISH);
                                        lang = Main.translations.get(Main.iface.getServerLang(ev.getGuild().getIdLong()));
                                        sendMessage(lang.botLocale.languageSetMessage, ev.getChannel().getId());
                                        break;
                                    default:
                                        sendMessage(lang.botLocale.unknownLanguage, ev.getChannel().getId());
                                }
                            else
                                sendMessage(lang.botLocale.unknownLanguage, ev.getChannel().getId());
                            break;
                        case "setstage":
                            if (!isAdmin(ev.getMember())) {
                                sendMessage(lang.botLocale.noAdminPerms, ev.getChannel().getId());
                                break;
                            }
                            Main.iface.setStageChannel(ev.getGuild().getIdLong(), ev.getChannel().getIdLong());
                            sendMessage(lang.botLocale.stageFeedMsg, ev.getChannel().getId());
                            break;
                        case "setsalmon":
                            if (!isAdmin(ev.getMember())) {
                                sendMessage(lang.botLocale.noAdminPerms, ev.getChannel().getId());
                                break;
                            }
                            Main.iface.setSalmonChannel(ev.getGuild().getIdLong(), ev.getChannel().getIdLong());
                            sendMessage(lang.botLocale.salmonFeedMsg, ev.getChannel().getId());
                            break;
                        case "delsalmon":
                            if (!isAdmin(ev.getMember())) {
                                sendMessage(lang.botLocale.noAdminPerms, ev.getChannel().getId());
                                break;
                            }
                            Main.iface.setSalmonChannel(ev.getGuild().getIdLong(), null);
                            sendMessage(lang.botLocale.deleteSuccessful, ev.getChannel().getId());
                            break;
                        case "delstage":
                            if (!isAdmin(ev.getMember())) {
                                sendMessage(lang.botLocale.noAdminPerms, ev.getChannel().getId());
                                break;
                            }
                            Main.iface.setStageChannel(ev.getGuild().getIdLong(), null);
                            sendMessage(lang.botLocale.deleteSuccessful, ev.getChannel().getId());
                            break;
                        case "stage":
                        case "stages":
                            sendMapMessage(ev.getGuild().getIdLong(), ev.getChannel().getIdLong());
                            break;
                        case "salmon":
                            try {
                                sendSalmonMessage(ev.getGuild().getIdLong(), ev.getChannel().getIdLong());
                            } catch (ExecutionException | InterruptedException e) {
                                e.printStackTrace();
                            }
                            break;
                        case "invite":
                            sendMessage("<" + jda.getInviteUrl(Permission.MESSAGE_READ, Permission.MESSAGE_WRITE, Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_ATTACH_FILES) + ">", ev.getChannel().getId());
                            break;
                        case "support":
                            sendMessage("https://discord.gg/DBH9FSFCXb", ev.getChannel().getId());
                            break;
                        case "help":
                            sendMessage(lang.botLocale.helpMessage, ev.getChannel().getId());
                            break;
                        case "code":
                            final Random r = new Random();
                            final int a = r.nextInt(10);
                            final int b = r.nextInt(10);
                            final int c = r.nextInt(10);
                            final int d = r.nextInt(10);
                            sendMessage(a + "" + b + "" + c + "" + d, ev.getChannel().getId());
                            break;
                        default:
                            sendMessage(lang.botLocale.unknownCommand, ev.getChannel().getId());
                            break;
                    }
                }
            }
        }
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private static boolean isAdmin(Member m) {
        if(m == null) return false;
        return m.hasPermission(Permission.MANAGE_SERVER);
    }

    private static String getWeaponName(Locale lang, Weapons w) {
        if (w.weapon == null && w.coop_special_weapon != null) {
            return lang.coop_special_weapons.get(w.coop_special_weapon.image).name;
        } else {
            return lang.weapons.get(w.id).name;
        }
    }

    public Map.Entry<Long, Long> sendSalmonMessage(long serverid, long channel) throws ExecutionException, InterruptedException {
        Locale lang = Main.translations.get(Main.iface.getServerLang(serverid));
        return new AbstractMap.SimpleEntry<>(serverid, submitMessage(new MessageBuilder().setEmbed(new EmbedBuilder().setTitle(lang.botLocale.salmonRunTitle)
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
                .build()).build(), channel).get().getIdLong());
    }

    public void sendMapMessage(Long serverid, long channel) {
        Locale lang = Main.translations.get(Main.iface.getServerLang(serverid));
        sendMessage(new MessageBuilder().setEmbed(new EmbedBuilder().setTitle(lang.botLocale.stagesTitle)
                .addField("<:regular:822873973225947146>" +
                                lang.game_modes.get("regular").name,
                        lang.stages.get(Main.schedules.regular[0].stage_a.id).getName() +
                                ", " + lang.stages.get(Main.schedules.regular[0].stage_b.id).getName()
                        , false)
                .addField("<:ranked:822873973200388106>" +
                                lang.game_modes.get("gachi").name + " (" + lang.rules.get(Main.schedules.gachi[0].rule.key).name + ")",
                        lang.stages.get(Main.schedules.gachi[0].stage_a.id).getName() +
                                ", " + lang.stages.get(Main.schedules.gachi[0].stage_b.id).getName()
                        , false)
                .addField("<:ranked:822873973142192148>" +
                                lang.game_modes.get("league").name + " (" + lang.rules.get(Main.schedules.league[0].rule.key).name + ")",
                        lang.stages.get(Main.schedules.league[0].stage_a.id).getName() +
                                ", " + lang.stages.get(Main.schedules.league[0].stage_b.id).getName()
                        , false)
                .build()).build(), channel);
    }

    private class PresenceUpdater extends Thread {
        final Activity[] presences = new Activity[]{Activity.playing("Spoon 2"), Activity.watching(Config.instance().discord.prefix + "help")};
        int presence = 0;
        final Random r = new Random();

        @Override
        public void run() {
            while (true) {
                jda.getPresence().setPresence(presences[presence], false);
                try {
                    //noinspection BusyWait
                    sleep(1000 * (r.nextInt(19) + 1));
                } catch (InterruptedException e) {
                    return;
                }
                presence++;
                if (presence > presences.length-1) presence = 0;

            }
        }
    }

}
