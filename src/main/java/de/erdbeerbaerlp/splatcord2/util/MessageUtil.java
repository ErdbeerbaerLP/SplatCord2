package de.erdbeerbaerlp.splatcord2.util;

import de.erdbeerbaerlp.splatcord2.Main;
import de.erdbeerbaerlp.splatcord2.storage.Rotation;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon2.translations.Locale;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.requests.RestAction;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static de.erdbeerbaerlp.splatcord2.Main.bot;
import static de.erdbeerbaerlp.splatcord2.Main.iface;

public class MessageUtil {
    public static void sendSalmonFeed(Long serverid, Long channel){
        final TextChannel ch = bot.jda.getTextChannelById(channel);
        try {
            final long lastRotationMessageID = iface.getLastSalmonMessage(serverid);
            final boolean deleteMessage = iface.getDeleteMessage(serverid);
            if (deleteMessage && lastRotationMessageID != 0) {
                final RestAction<Message> message = ch.retrieveMessageById(lastRotationMessageID);
                message.submit().thenAccept((msg) -> {
                    msg.delete().queue();
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
        try {
            final long lastRotationMessageID = iface.getLastRotationMessage(serverid);
            final boolean deleteMessage = iface.getDeleteMessage(serverid);
            if (deleteMessage && lastRotationMessageID != 0) {
                final RestAction<Message> message = ch.retrieveMessageById(lastRotationMessageID);
                message.submit().thenAccept((msg) -> {
                    msg.delete().queue();
                });
            }
            bot.sendMessage(
                    getMapMessage(
                            serverid,
                            currentRotation),
                    channel)
                    .thenAccept((a) -> {
                iface.setLastRotationMessage(serverid, a.getIdLong());
            });

        } catch (InsufficientPermissionException e) {
            Guild guildById = bot.jda.getGuildById(serverid);
            System.err.println("Failed to send rotation to Server \"" + (guildById == null ? "null" : guildById.getName()) + "(" + serverid + ")\"");
        }
    }

    public static Message getMapMessage(Long serverid, Rotation r) {
        Locale lang = Main.translations.get(iface.getServerLang(serverid));
        return new MessageBuilder().setEmbeds(new EmbedBuilder().setTitle(lang.botLocale.stagesTitle)
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
}
