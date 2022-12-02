package de.erdbeerbaerlp.splatcord2.util;

import de.erdbeerbaerlp.splatcord2.Main;
import de.erdbeerbaerlp.splatcord2.storage.Emote;
import de.erdbeerbaerlp.splatcord2.storage.Rotation;
import de.erdbeerbaerlp.splatcord2.storage.S3Rotation;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon1.Phase;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon2.translations.Locale;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon3.splatfest.FestRecord;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
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
            Map.Entry<Long, Long> msg = bot.sendS2SalmonMessage(serverid, channel);
            if (msg != null)
                iface.setSalmonMessage(msg.getKey(), msg.getValue());
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        } catch (InsufficientPermissionException e) {
            Guild guildById = bot.jda.getGuildById(serverid);
            System.err.println("Failed to send salmon to Server \"" + (guildById == null ? "null" : guildById.getName()) + "(" + serverid + ")\"");
        }
    }

    public static void sendS3SalmonFeed(Long serverid, Long channel) {
        final TextChannel ch = bot.jda.getTextChannelById(channel);
        if (ch == null) {
            System.out.println(serverid + " : Channel " + channel + " is null, removing...");
            iface.setSalmonChannel(serverid, null);
            return;
        }
        try {
            final long lastRotationMessageID = iface.getLastS3SalmonMessage(serverid);
            final boolean deleteMessage = iface.getDeleteMessage(serverid);
            if (deleteMessage && lastRotationMessageID != 0) {
                final RestAction<Message> message = ch.retrieveMessageById(lastRotationMessageID);
                message.submit().thenAccept((msg) -> {
                    msg.delete().queue();
                }).whenComplete((v, error) -> {
                    if (error != null) {
                        System.out.println("Failed deleting S3 salmon " + error.getMessage());
                    }
                });
            }
            Map.Entry<Long, Long> msg = bot.sendS3SalmonMessage(serverid, channel);
            if (msg != null)
                iface.setS3SalmonMessage(msg.getKey(), msg.getValue());
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        } catch (InsufficientPermissionException e) {
            Guild guildById = bot.jda.getGuildById(serverid);
            System.err.println("Failed to send salmon to Server \"" + (guildById == null ? "null" : guildById.getName()) + "(" + serverid + ")\"");
        }
    }

    public static void sendS2RotationFeed(long serverid, long channel, Rotation currentRotation) {
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

    public static void sendS3RotationFeed(long serverid, long channel, S3Rotation currentRotation) {
        final TextChannel ch = bot.jda.getTextChannelById(channel);
        if (ch == null) {
            System.out.println(serverid + " : Channel " + channel + " is null, removing...");
            iface.setS3StageChannel(serverid, null);
            return;
        }
        try {
            final long lastRotationMessageID = iface.getLastS3RotationMessage(serverid);
            final boolean deleteMessage = iface.getDeleteMessage(serverid);
            if (deleteMessage && lastRotationMessageID != 0) {
                final RestAction<Message> message = ch.retrieveMessageById(lastRotationMessageID);
                message.submit().thenAccept((msg) -> msg.delete().queue());
            }
            final CompletableFuture<Message> msg = bot.sendMessage(
                    getS3MapMessage(
                            serverid,
                            currentRotation), channel);
            if (msg != null) msg.thenAccept((a) -> iface.setLastS3RotationMessage(serverid, a.getIdLong()));

        } catch (InsufficientPermissionException e) {
            Guild guildById = bot.jda.getGuildById(serverid);
            System.err.println("Failed to send rotation to Server \"" + (guildById == null ? "null" : guildById.getName()) + "(" + serverid + ")\"");
        }
    }

    public static MessageCreateData getMapMessage(final Long serverid, final Rotation r) {
        final Locale lang = Main.translations.get(iface.getServerLang(serverid));
        final EmbedBuilder b = new EmbedBuilder().setTitle(lang.botLocale.stagesTitle + "(Splatoon 2)");
        addS2Embed(lang, r, b);
        return new MessageCreateBuilder().setEmbeds(b.build()).build();
    }

    public static MessageCreateData getS3MapMessage(final Long serverid, final S3Rotation r) {
        final Locale lang = Main.translations.get(iface.getServerLang(serverid));
        final EmbedBuilder emb = new EmbedBuilder().setTitle(lang.botLocale.stagesTitle + "(Splatoon 3)");
        addS3Embed(lang, r, emb);
        return new MessageCreateBuilder().setEmbeds(emb.build()).build();
    }

    public static void addS2Embed(final Locale lang, final Rotation r, final EmbedBuilder b) {
        b.addField(Emote.REGULAR +
                                lang.game_modes.get("regular").name,
                        lang.stages.get(r.getRegular().stage_a.id).getName() +
                                ", " + lang.stages.get(r.getRegular().stage_b.id).getName()
                        , false)
                .addField(Emote.RANKED +
                                lang.game_modes.get("gachi").name + " (" + GameModeUtil.translateS2(lang, r.getRanked().rule.key) + ")",
                        lang.stages.get(r.getRanked().stage_a.id).getName() +
                                ", " + lang.stages.get(r.getRanked().stage_b.id).getName()
                        , false)
                .addField(Emote.LEAGUE +
                                lang.game_modes.get("league").name + " (" + GameModeUtil.translateS2(lang, r.getLeague().rule.key) + ")",
                        lang.stages.get(r.getLeague().stage_a.id).getName() +
                                ", " + lang.stages.get(r.getLeague().stage_b.id).getName()
                        , false);
    }

    public static void addS3Embed(Locale lang, final S3Rotation r, EmbedBuilder b) {
        if (r.getFest().festMatchSetting != null) {
            b.addField(Emote.SPLATFEST +
                            lang.game_modes.get("regular").name,
                    (lang.s3locales.stages.get(r.getFest().festMatchSetting.vsStages[0].id).name +
                            ", " + (lang.s3locales.stages.get(r.getFest().festMatchSetting.vsStages[1].id)).name)
                    , true);
            if (r.getSplatfest() != null && r.getSplatfest().getMidtermTime() <= System.currentTimeMillis() / 1000 && r.getSplatfest().tricolorStage != null) {
                b.addField(Emote.SPLATFEST + lang.botLocale.tricolorBattle, lang.s3locales.stages.get(r.getSplatfest().tricolorStage.id).name, true);
            }
        } else
            b.addField(Emote.REGULAR +
                                    lang.game_modes.get("regular").name,
                            (lang.s3locales.stages.get(r.getRegular().regularMatchSetting.vsStages[0].id).name +
                                    ", " + lang.s3locales.stages.get(r.getRegular().regularMatchSetting.vsStages[1].id).name)
                            , true)
                    .addField(Emote.RANKED +
                                    lang.botLocale.anarchyBattleSeries + " [" + GameModeUtil.translateS3(lang, r.getBankara().bankaraMatchSettings[0].vsRule.id) + "]",
                            lang.s3locales.stages.get(r.getBankara().bankaraMatchSettings[0].vsStages[0].id).name +
                                    ", " + lang.s3locales.stages.get(r.getBankara().bankaraMatchSettings[0].vsStages[1].id).name
                            , true)
                    .addField(Emote.RANKED +
                                    lang.botLocale.anarchyBattleOpen + " [" + GameModeUtil.translateS3(lang, r.getBankara().bankaraMatchSettings[1].vsRule.id) + "]",
                            lang.s3locales.stages.get(r.getBankara().bankaraMatchSettings[1].vsStages[0].id).name +
                                    ", " + lang.s3locales.stages.get(r.getBankara().bankaraMatchSettings[1].vsStages[1].id).name
                            , true)
                    .addField(Emote.X_BATTLE +
                                    lang.botLocale.xBattle + " [" + GameModeUtil.translateS3(lang, r.getxBattle().xMatchSetting.vsRule.id) + "]",
                            lang.s3locales.stages.get(r.getxBattle().xMatchSetting.vsStages[0].id).name +
                                    ", " + lang.s3locales.stages.get(r.getxBattle().xMatchSetting.vsStages[1].id).name
                            , true);

    }

    public static MessageCreateData getMapMessage(Long serverid, Phase currentRotation) {
        final Locale lang = Main.translations.get(iface.getServerLang(serverid));
        return new MessageCreateBuilder().setEmbeds(new EmbedBuilder().setTitle(lang.botLocale.stagesTitle + "(Splatoon 1)")
                .addField(Emote.REGULAR +
                                lang.game_modes.get("regular").name,
                        lang.botLocale.getS1MapName(currentRotation.RegularStages[0].MapID.value) +
                                ", " + lang.botLocale.getS1MapName(currentRotation.RegularStages[1].MapID.value)
                        , true)
                .addField(Emote.RANKED +
                                lang.game_modes.get("gachi").name + " (" + GameModeUtil.translateS1(lang, currentRotation.GachiRule.value) + ")",
                        lang.botLocale.getS1MapName(currentRotation.GachiStages[0].MapID.value) +
                                ", " + lang.botLocale.getS1MapName(currentRotation.GachiStages[1].MapID.value)
                        , true).build()).build();
    }

    public static int convertIdToVsStageId(String id) {
        return Integer.parseInt(new String(Base64.getDecoder().decode(id), StandardCharsets.UTF_8).split("-")[1]);
    }

    public static void sendS1RotationFeed(Long serverid, Long channel, Phase currentS1Rotation) {
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

    public static MessageEmbed generateSplatfestEmbed(FestRecord fest, boolean command, Locale l) {
        final EmbedBuilder b = new EmbedBuilder();
        final int splatfestID = fest.getSplatfestID();
        if (fest.getEndTime() < System.currentTimeMillis() / 1000) {
            b.setTitle(l.botLocale.splatfestEmbedTitle);
            b.setFooter(l.botLocale.footer_ended);
            b.setTimestamp(Instant.ofEpochSecond(fest.getEndTime()));
        } else if (fest.getStartTime() >= System.currentTimeMillis() / 1000) {
            b.setTitle(l.botLocale.runningSplatfestTitle);
            b.setFooter(l.botLocale.footer_ends);
            b.setTimestamp(Instant.ofEpochSecond(fest.getEndTime()));
        } else if (fest.getStartTime() < System.currentTimeMillis() / 1000) {
            b.setTitle(l.botLocale.newSplatfestTitle);
            b.setFooter(l.botLocale.footer_starts);
            b.setTimestamp(Instant.ofEpochSecond(fest.getStartTime()));
        }
        if (command) b.setTitle(l.botLocale.splatfestEmbedTitle);
        b.setImage(fest.image.url);
        b.setDescription(l.botLocale.getSplatfestTitle(splatfestID));
        b.addField(l.botLocale.splatfestTeams, l.botLocale.getSplatfestTeam(1 + 3 * splatfestID) + ", " + l.botLocale.getSplatfestTeam(2 + 3 * splatfestID) + ", " + l.botLocale.getSplatfestTeam(3 + 3 * splatfestID), false);
        if (fest.getWinningTeam() != null) {
            b.setColor(fest.getWinningTeam().color.toColor());
            b.addField(l.botLocale.splatfestTeamWinner, fest.getWinningTeam().teamName, false);
        }

        return b.build();
    }
}
