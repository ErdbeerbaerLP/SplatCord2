package de.erdbeerbaerlp.splatcord2.util;

import de.erdbeerbaerlp.splatcord2.Main;
import de.erdbeerbaerlp.splatcord2.storage.Emote;
import de.erdbeerbaerlp.splatcord2.storage.Rotation;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon1.Phase;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon2.translations.Locale;
import de.erdbeerbaerlp.splatcord2.util.wiiu.RankedModeTranslator;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.requests.RestAction;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static de.erdbeerbaerlp.splatcord2.Main.bot;
import static de.erdbeerbaerlp.splatcord2.Main.iface;

public class MessageUtil {
    public static void sendSalmonFeed(Long serverid, Long channel) {
        final TextChannel ch = bot.jda.getTextChannelById(channel);
        if (ch == null) {
            System.out.println(serverid + " : Channel " + channel + " is null, removing...");
            iface.setSalmonChannel(serverid, null);
            return;
        }
        try {
            final long lastRotationMessageID = iface.getLastSalmonMessage(serverid);
            final boolean deleteMessage = iface.getDeleteMessage(serverid);
            if (deleteMessage && lastRotationMessageID != 0) {
                final RestAction<Message> message = ch.retrieveMessageById(lastRotationMessageID);
                message.submit().thenAccept((msg) -> {
                    msg.delete().queue();
                }).whenComplete((v, error) -> {
                    if (error != null) {
                        System.out.println("Failed deleting salmon " + error.getMessage());
                    }
                });
            }
            Map.Entry<Long, Long> msg = bot.sendSalmonMessage(serverid, channel);
            if (msg != null)
                iface.setSalmonMessage(msg.getKey(), msg.getValue());
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        } catch (InsufficientPermissionException e) {
            Guild guildById = bot.jda.getGuildById(serverid);
            System.err.println("Failed to send salmon to Server \"" + (guildById == null ? "null" : guildById.getName()) + "(" + serverid + ")\"");
        }
    }

    public static void sendRotationFeed(long serverid, long channel, Rotation currentRotation) {
        final TextChannel ch = bot.jda.getTextChannelById(channel);
        if (ch == null) {
            System.out.println(serverid + " : Channel " + channel + " is null, removing...");
            iface.setS2StageChannel(serverid, null);
            return;
        }
        try {
            final long lastRotationMessageID = iface.getLastS2RotationMessage(serverid);
            final boolean deleteMessage = iface.getDeleteMessage(serverid);
            if (deleteMessage && lastRotationMessageID != 0) {
                final RestAction<Message> message = ch.retrieveMessageById(lastRotationMessageID);
                message.submit().thenAccept((msg) -> {
                    msg.delete().queue();
                });
            }
            final CompletableFuture<Message> msg = bot.sendMessage(
                    getMapMessage(
                            serverid,
                            currentRotation), channel);
            if (msg != null) msg.thenAccept((a) -> iface.setLastS2RotationMessage(serverid, a.getIdLong()));

        } catch (InsufficientPermissionException e) {
            Guild guildById = bot.jda.getGuildById(serverid);
            System.err.println("Failed to send rotation to Server \"" + (guildById == null ? "null" : guildById.getName()) + "(" + serverid + ")\"");
        }
    }

    public static Message getMapMessage(Long serverid, Rotation r) {
        Locale lang = Main.translations.get(iface.getServerLang(serverid));
        return new MessageBuilder().setEmbeds(new EmbedBuilder().setTitle(lang.botLocale.stagesTitle+ "(Splatoon 2)")
                .addField(Emote.REGULAR +
                                lang.game_modes.get("regular").name,
                        lang.stages.get(r.getRegular().stage_a.id).getName() +
                                ", " + lang.stages.get(r.getRegular().stage_b.id).getName()
                        , false)
                .addField(Emote.RANKED +
                                lang.game_modes.get("gachi").name + " (" + lang.rules.get(r.getRanked().rule.key).name + ")",
                        lang.stages.get(r.getRanked().stage_a.id).getName() +
                                ", " + lang.stages.get(r.getRanked().stage_b.id).getName()
                        , false)
                .addField(Emote.LEAGUE +
                                lang.game_modes.get("league").name + " (" + lang.rules.get(r.getLeague().rule.key).name + ")",
                        lang.stages.get(r.getLeague().stage_a.id).getName() +
                                ", " + lang.stages.get(r.getLeague().stage_b.id).getName()
                        , false)
                .build()).build();
    }
    public static Message getMapMessage(Long serverid, Phase currentRotation) {
        Locale lang = Main.translations.get(iface.getServerLang(serverid));
        return new MessageBuilder().setEmbeds(new EmbedBuilder().setTitle(lang.botLocale.stagesTitle+ "(Splatoon 1)")
                .addField(Emote.REGULAR +
                                lang.game_modes.get("regular").name,
                        lang.botLocale.getS1MapName(currentRotation.RegularStages[0].MapID.value) +
                                ", " + lang.botLocale.getS1MapName(currentRotation.RegularStages[1].MapID.value)
                        , true)
                .addField(Emote.RANKED +
                                lang.game_modes.get("gachi").name + " (" + lang.rules.get(RankedModeTranslator.translateS1(currentRotation.GachiRule.value)).name + ")",
                        lang.botLocale.getS1MapName(currentRotation.GachiStages[0].MapID.value) +
                                ", " + lang.botLocale.getS1MapName(currentRotation.GachiStages[1].MapID.value)
                        , true)
                .build()).build();
    }

    public static void sendRotationFeed(Long serverid, Long channel, Phase currentS1Rotation) {
        final TextChannel ch = bot.jda.getTextChannelById(channel);
        if (ch == null) {
            System.out.println(serverid + " : Channel " + channel + " is null, removing...");
            iface.setS1StageChannel(serverid, null);
            return;
        }
        try {
            final long lastRotationMessageID = iface.getLastS1RotationMessage(serverid);
            final boolean deleteMessage = iface.getDeleteMessage(serverid);
            if (deleteMessage && lastRotationMessageID != 0) {
                final RestAction<Message> message = ch.retrieveMessageById(lastRotationMessageID);
                message.submit().thenAccept((msg) -> {
                    msg.delete().queue();
                });
            }
            final CompletableFuture<Message> msg = bot.sendMessage(
                    getMapMessage(
                            serverid,
                            currentS1Rotation), channel);
            if (msg != null) msg.thenAccept((a) -> iface.setLastS1RotationMessage(serverid, a.getIdLong()));

        } catch (InsufficientPermissionException e) {
            Guild guildById = bot.jda.getGuildById(serverid);
            System.err.println("Failed to send s1 rotation to Server \"" + (guildById == null ? "null" : guildById.getName()) + "(" + serverid + ")\"");
        }
    }
}
