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

    public void sendMessage(Message msg, Long channelId) throws InsufficientPermissionException {
        if (msg == null || channelId == null) return;
        final TextChannel channel = jda.getTextChannelById(channelId);
        if (channel != null) channel.sendMessage(msg).queue();
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
                if (baseCmd != null)
                    baseCmd.execute(ev);
            }
        }

        //Update command permissions on role creation / permission change
        if(event instanceof RoleUpdatePermissionsEvent){
            CommandRegistry.setCommands(((RoleUpdatePermissionsEvent) event).getGuild());
        }
        if(event instanceof RoleCreateEvent)
            CommandRegistry.setCommands(((RoleCreateEvent) event).getGuild());



        //Legacy commands
        if (event instanceof MessageReceivedEvent) {
            final MessageReceivedEvent ev = ((MessageReceivedEvent) event);
            if (ev.getAuthor().isBot()) return;
            String msg = ev.getMessage().getContentRaw();
            if (msg.startsWith(Config.instance().discord.prefix)) {
                msg = msg.replaceFirst(Pattern.quote(Config.instance().discord.prefix), "").trim();
                final String[] cmd = msg.split(" ");
                Locale lang = Main.translations.get(Main.iface.getServerLang(ev.getGuild().getIdLong()));
                if (!ev.getGuild().getMember(jda.getSelfUser()).hasPermission(ev.getGuild().getGuildChannelById(ev.getChannel().getIdLong()), Permission.MESSAGE_WRITE)) {
                    final Locale finalLang = lang;
                    ev.getAuthor().openPrivateChannel().queue((channel) -> {
                        channel.sendMessage(finalLang.botLocale.noWritePerms).queue();
                    });
                    return;
                }
                if (cmd.length > 0) {
                    if(cmd[0].equalsIgnoreCase("fixslashcommands")) {
                        if (!isAdmin(ev.getMember())) {
                            sendMessage(lang.botLocale.noAdminPerms, ev.getChannel().getId());
                            return;
                        }
                        CommandRegistry.setCommands(ev.getGuild());
                        sendMessage(lang.botLocale.cmdFixSlashCommands, ev.getChannel().getId());
                    }else{
                        sendMessage(lang.botLocale.legacyCommand, ev.getChannel().getId());
                    }
                }
            }
        }
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
        final CompletableFuture<Message> submitMsg = submitMessage(new MessageBuilder().setEmbed(new EmbedBuilder().setTitle(lang.botLocale.salmonRunTitle)
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

    public void sendSplatNetShopMessage(long serverid, long channel) {
        Locale lang = Main.translations.get(Main.iface.getServerLang(serverid));
        try {
            final URL tworld = new URL("https://splatoon2.ink/data/merchandises.json");
            final HttpsURLConnection con = (HttpsURLConnection) tworld.openConnection();
            con.setRequestProperty("User-Agent", "SplatCord 2");
            con.connect();
            final TentaWorld splatNet = Main.gson.fromJson(new InputStreamReader(con.getInputStream()), TentaWorld.class);
            final ArrayList<MessageEmbed> embeds = new ArrayList<>();
            for (Merchandise m : splatNet.merchandises) {
                final EmbedBuilder b = new EmbedBuilder()
                        .setDescription(lang.botLocale.skillSlots + " " + (1 + m.gear.rarity))
                        .setTimestamp(Instant.ofEpochSecond(m.end_time))
                        .setFooter(lang.botLocale.footer_ends)
                        .setThumbnail("https://splatoon2.ink/assets/splatnet" + m.gear.thumbnail)
                        .setAuthor(getLocalizedGearName(lang, m.gear) + " (" + lang.brands.get(m.gear.brand.id).name + ")", null, "https://splatoon2.ink/assets/splatnet" + m.gear.brand.image)
                        .addField(lang.botLocale.skill, lang.skills.get(m.skill.id).name, true)
                        .addField(lang.botLocale.price, m.price + "", true);
                embeds.add(b.build());
            }
            CompletableFuture<Message> msg = jda.getTextChannelById(channel).sendMessage(lang.botLocale.splatNetShop).embed(embeds.get(0)).submit();
            embeds.remove(0);
            for (MessageEmbed e : embeds) {
                msg = msg.thenCompose((m) -> m.getChannel().sendMessage(e).submit());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getLocalizedGearName(Locale lang, Gear gear) {
        if (lang.gear.get(gear.kind.name()).getAsJsonObject().has(gear.id + ""))
            return lang.gear.get(gear.kind.name()).getAsJsonObject().get(gear.id + "").getAsJsonObject().get("name").getAsString();
        else if (lang.gear.containsKey(gear.id + "")) {
            return lang.gear.get(gear.id + "").getAsJsonObject().get("name").getAsString();
        } else {
            return "Error, contact Developer";
        }
    }

    private void sendMapRotation(Long serverid, long channel) {
        final Locale lang = Main.translations.get(Main.iface.getServerLang(serverid));

        final Rotation currentRotation = ScheduleUtil.getCurrentRotation();
        final ArrayList<Rotation> nextRotations = ScheduleUtil.getNext3Rotations();

        sendMessage(getMapMessage(serverid, currentRotation),channel);
        sendMessage(new MessageBuilder().setEmbed(new EmbedBuilder().setTitle(lang.botLocale.futureStagesTitle)
                .addField(":alarm_clock: ", "<t:" + nextRotations.get(0).getRegular().start_time + ":R>", true)
                .addField("<:regular:822873973225947146>" +
                                lang.game_modes.get("regular").name,
                        lang.stages.get(nextRotations.get(0).getRegular().stage_a.id).getName() +
                                ", " + lang.stages.get(nextRotations.get(0).getRegular().stage_b.id).getName()
                        , true)
                .addField("<:ranked:822873973200388106>" +
                                lang.game_modes.get("gachi").name + " (" + lang.rules.get(nextRotations.get(0).getRanked().rule.key).name + ")",
                        lang.stages.get(nextRotations.get(0).getRanked().stage_a.id).getName() +
                                ", " + lang.stages.get(nextRotations.get(0).getRanked().stage_b.id).getName()
                        , true)
                .addField("<:league:822873973142192148>" +
                                lang.game_modes.get("league").name + " (" + lang.rules.get(nextRotations.get(0).getLeague().rule.key).name + ")",
                        lang.stages.get(nextRotations.get(0).getLeague().stage_a.id).getName() +
                                ", " + lang.stages.get(nextRotations.get(0).getLeague().stage_b.id).getName()
                        , true)
                .addBlankField(false)
                .addField(":alarm_clock: ", "<t:" + nextRotations.get(1).getRegular().start_time + ":R>", true)
                .addField("<:regular:822873973225947146>" +
                                lang.game_modes.get("regular").name,
                        lang.stages.get(nextRotations.get(1).getRegular().stage_a.id).getName() +
                                ", " + lang.stages.get(nextRotations.get(1).getRegular().stage_b.id).getName()
                        , true)
                .addField("<:ranked:822873973200388106>" +
                                lang.game_modes.get("gachi").name + " (" + lang.rules.get(nextRotations.get(1).getRanked().rule.key).name + ")",
                        lang.stages.get(nextRotations.get(1).getRanked().stage_a.id).getName() +
                                ", " + lang.stages.get(nextRotations.get(1).getRanked().stage_b.id).getName()
                        , true)
                .addField("<:league:822873973142192148>" +
                                lang.game_modes.get("league").name + " (" + lang.rules.get(nextRotations.get(1).getLeague().rule.key).name + ")",
                        lang.stages.get(nextRotations.get(1).getLeague().stage_a.id).getName() +
                                ", " + lang.stages.get(nextRotations.get(1).getLeague().stage_b.id).getName()
                        , true)
                .build()).build(), channel);
    }

    public Message getMapMessage(Long serverid, Rotation r) {
        Locale lang = Main.translations.get(Main.iface.getServerLang(serverid));
        return new MessageBuilder().setEmbed(new EmbedBuilder().setTitle(lang.botLocale.stagesTitle)
                .addField("<:regular:822873973225947146>" +
                                lang.game_modes.get("regular").name,
                        lang.stages.get(r.getRegular().stage_a.id).getName() +
                                ", " + lang.stages.get(r.getRegular().stage_b.id).getName()
                        , false)
                .addField("<:ranked:822873973200388106>" +
                                lang.game_modes.get("gachi").name + " (" + lang.rules.get(r.getRanked().rule.key).name + ")",
                        lang.stages.get(r.getRanked().stage_a.id).getName() +
                                ", " + lang.stages.get(r.getRanked().stage_b.id).getName()
                        , false)
                .addField("<:ranked:822873973142192148>" +
                                lang.game_modes.get("league").name + " (" + lang.rules.get(r.getLeague().rule.key).name + ")",
                        lang.stages.get(r.getLeague().stage_a.id).getName() +
                                ", " + lang.stages.get(r.getLeague().stage_b.id).getName()
                        , false)
                .build()).build();
    }

    private class StatusUpdater extends Thread {
        int presence = 0;
        final Random r = new Random();

        @Override
        public void run() {
            while (true) {
                final Config.Discord.Status s = Config.instance().discord.botStatus.get(presence);
                jda.getPresence().setPresence(Main.iface.status.isDBAlive()?OnlineStatus.ONLINE:OnlineStatus.DO_NOT_DISTURB,Activity.of(s.type, s.message.replace("%servercount%", jda.getGuilds().size() + "")), false);
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
