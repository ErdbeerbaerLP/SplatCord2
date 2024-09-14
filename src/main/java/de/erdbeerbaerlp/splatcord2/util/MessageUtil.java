package de.erdbeerbaerlp.splatcord2.util;

import de.erdbeerbaerlp.splatcord2.Main;
import de.erdbeerbaerlp.splatcord2.storage.Emote;
import de.erdbeerbaerlp.splatcord2.storage.Rotation;
import de.erdbeerbaerlp.splatcord2.storage.S3Rotation;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon1.Phase;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon1.SplatfestByml;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon2.translations.Locale;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon3.rotation.EventTimePeriod;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon3.rotation.Stage;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon3.splatfest.FestRecord;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
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

import static de.erdbeerbaerlp.splatcord2.Main.*;

public class MessageUtil {
    public static void sendSalmonFeed(Long serverid, Long channel) {
        final GuildMessageChannel ch = (GuildMessageChannel) bot.jda.getGuildChannelById(channel);
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
            final Map.Entry<Long, Long> msg = bot.sendS2SalmonMessage(serverid, channel);
            if (msg != null)
                iface.setSalmonMessage(msg.getKey(), msg.getValue());
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        } catch (InsufficientPermissionException e) {
            final Guild guildById = bot.jda.getGuildById(serverid);
            System.err.println("Failed to send salmon to Server \"" + (guildById == null ? "null" : guildById.getName()) + "(" + serverid + ")\"");
        }
    }

    public static void sendS3SalmonFeed(Long serverid, Long channel) {
        final GuildMessageChannel ch = (GuildMessageChannel) bot.jda.getGuildChannelById(channel);
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
            final Map.Entry<Long, Long> msg = bot.sendS3SalmonMessage(serverid, channel);
            if (msg != null)
                iface.setS3SalmonMessage(msg.getKey(), msg.getValue());
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        } catch (InsufficientPermissionException e) {
            final Guild guildById = bot.jda.getGuildById(serverid);
            System.err.println("Failed to send salmon to Server \"" + (guildById == null ? "null" : guildById.getName()) + "(" + serverid + ")\"");
        }
    }

    public static void sendS2RotationFeed(long serverid, long channel, Rotation currentRotation) {
        final GuildMessageChannel ch = (GuildMessageChannel) bot.jda.getGuildChannelById(channel);
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
            final Guild guildById = bot.jda.getGuildById(serverid);
            System.err.println("Failed to send rotation to Server \"" + (guildById == null ? "null" : guildById.getName()) + "(" + serverid + ")\"");
        }
    }

    public static void sendS3RotationFeed(long serverid, long channel, S3Rotation currentRotation) {
        final GuildMessageChannel ch = (GuildMessageChannel) bot.jda.getGuildChannelById(channel);
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
            final Guild guildById = bot.jda.getGuildById(serverid);
            System.err.println("Failed to send rotation to Server \"" + (guildById == null ? "null" : guildById.getName()) + "(" + serverid + ")\"");
        }
    }

    public static void sendS3EventRotationFeed(long serverid, long channel, S3Rotation currentRotation) {
        if (currentRotation.getEvent() == null) return;
        final GuildMessageChannel ch = (GuildMessageChannel) bot.jda.getGuildChannelById(channel);
        if (ch == null) {
            System.out.println(serverid + " : Channel " + channel + " is null, removing...");
            iface.setS3EventChannel(serverid, null);
            return;
        }
        try {
            final long lastRotationMessageID = iface.getLastS3EventMessage(serverid);
            final boolean deleteMessage = iface.getDeleteMessage(serverid);
            if (deleteMessage && lastRotationMessageID != 0) {
                final RestAction<Message> message = ch.retrieveMessageById(lastRotationMessageID);
                message.submit().thenAccept((msg) -> msg.delete().queue());
            }
            final CompletableFuture<Message> msg = bot.sendMessage(
                    getS3EventMessage(
                            serverid,
                            currentRotation), channel);
            if (msg != null) msg.thenAccept((a) -> iface.setLastS3RotationMessage(serverid, a.getIdLong()));

        } catch (InsufficientPermissionException e) {
            final Guild guildById = bot.jda.getGuildById(serverid);
            System.err.println("Failed to send rotation to Server \"" + (guildById == null ? "null" : guildById.getName()) + "(" + serverid + ")\"");
        }
    }

    public static MessageCreateData getMapMessage(final Long serverid, final Rotation r) {
        final Locale lang = Main.translations.get(iface.getServerLang(serverid));
        final EmbedBuilder b = new EmbedBuilder().setTitle(lang.botLocale.stagesTitle + " (Splatoon 2)");
        addS2Embed(lang, r, b);
        if (r.image != null) b.setImage(r.image);
        return new MessageCreateBuilder().setEmbeds(b.build()).build();
    }

    public static MessageCreateData getS3MapMessage(final Long serverid, final S3Rotation r) {
        final Locale lang = Main.translations.get(iface.getServerLang(serverid));
        final EmbedBuilder emb = new EmbedBuilder().setTitle(lang.botLocale.stagesTitle + " (Splatoon 3)");
        addS3Embed(lang, r, emb);
        if (r.image != null) emb.setImage(r.image);
        return new MessageCreateBuilder().setEmbeds(emb.build()).build();
    }

    public static MessageCreateData getS3EventMessage(final Long serverid, final S3Rotation r) {
        final Locale lang = Main.translations.get(iface.getServerLang(serverid));
        final EmbedBuilder emb = new EmbedBuilder().setTitle(Emote.EVENT + lang.s3locales.events.get(r.getEvent().leagueMatchSetting.leagueMatchEvent.id).name)
                .setDescription("**" + lang.s3locales.events.get(r.getEvent().leagueMatchSetting.leagueMatchEvent.id).desc + "**\n" + lang.s3locales.events.get(r.getEvent().leagueMatchSetting.leagueMatchEvent.id).regulation.replace("<br />", "\n"))
                .addField(lang.botLocale.mode, GameModeUtil.translateS3(lang, r.getEvent().leagueMatchSetting.vsRule.id), true)
                .addField(lang.botLocale.stages, lang.s3locales.stages.get(r.getEvent().leagueMatchSetting.vsStages[0].id).name +
                        ", " + lang.s3locales.stages.get(r.getEvent().leagueMatchSetting.vsStages[1].id).name, true);
        final StringBuilder b = new StringBuilder();
        for (EventTimePeriod tp : r.getEvent().timePeriods) {
            b.append("<t:" + tp.getStartTime() + ":f> (<t:" + tp.getStartTime() + ":R>) - <t:" + tp.getEndTime() + ":f> (<t:" + tp.getEndTime() + ":R>)\n");
        }
        emb.addField(lang.botLocale.eventTimeTitle, b.toString(), false);
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

    private static String translateStage(Locale l, Stage s){
        return l.s3locales.stages.containsKey(s.id)?l.s3locales.stages.get(s.id).name:s.name;
    }

    public static void addS3Embed(Locale lang, final S3Rotation r, EmbedBuilder b) {
        if (r.getFest().festMatchSettings != null && r.getFest().festMatchSettings.length > 0) {
            b.addField(Emote.SPLATFEST +
                            lang.game_modes.get("regular").name,
                    (translateStage(lang,r.getFest().getRegularSFMatch().vsStages[0]) +
                            ", " + (translateStage(lang,r.getFest().getRegularSFMatch().vsStages[1])))
                    , true);
            b.addField(Emote.SPLATFEST +
                            lang.game_modes.get("regular").name + " (Pro)",
                    (translateStage(lang,r.getFest().getProSFMatch().vsStages[0]) +
                            ", " + (translateStage(lang,r.getFest().getProSFMatch().vsStages[1])))
                    , true);
            if (r.getTricolorStage() != null) {
                b.addField(Emote.SPLATFEST + lang.botLocale.tricolorBattle, translateStage(lang,r.getTricolorStage()), true);
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


    public static MessageCreateData getS1Message(Long serverid, Phase currentRotation) {
        final Locale lang = Main.translations.get(iface.getServerLang(serverid));
        final MessageCreateBuilder builder = new MessageCreateBuilder();

        if ((s1splatfestPretendo.root.Time.getStartTime() <= System.currentTimeMillis() / 1000) && (s1splatfestPretendo.root.Time.getEndTime() > System.currentTimeMillis() / 1000)) {
            builder.setEmbeds(generateSplatfestEmbedPretendo(s1splatfestPretendo, false, lang));
        } else {
            final EmbedBuilder b = new EmbedBuilder().setTitle(lang.botLocale.stagesTitle + " (Splatoon 1 " + Emote.PRETENDO_NETWORK + ")")
                    .addField(Emote.REGULAR +
                                    lang.game_modes.get("regular").name,
                            lang.botLocale.getS1MapName(currentRotation.RegularStages[0].MapID.value) +
                                    ", " + lang.botLocale.getS1MapName(currentRotation.RegularStages[1].MapID.value)
                            , true)
                    .addField(Emote.RANKED +
                                    lang.game_modes.get("gachi").name + " (" + GameModeUtil.translateS1(lang, currentRotation.GachiRule.value) + ")",
                            lang.botLocale.getS1MapName(currentRotation.GachiStages[0].MapID.value) +
                                    ", " + lang.botLocale.getS1MapName(currentRotation.GachiStages[1].MapID.value)
                            , true);
            if (currentRotation.image != null) b.setImage(currentRotation.image);
            builder.setEmbeds(b.build());
        }
        if (iface.getCustomSplatfests(serverid)) {
            if (s1splatfestSplatfestival.root.FestivalId.value.equals("4100")) return builder.build();
            if ((s1splatfestSplatfestival.root.Time.getStartTime() <= System.currentTimeMillis() / 1000) && ((s1splatfestSplatfestival.root.Time.getEndTime() > System.currentTimeMillis() / 1000))) {
                builder.addEmbeds(generateSplatfestEmbedSplatfestival(s1splatfestSplatfestival, false, lang));
            }
        }

        return builder.build();
    }

    public static MessageCreateData getS1PMessage(Long serverid, Phase currentRotation) {
        final Locale lang = Main.translations.get(iface.getServerLang(serverid));
        final MessageCreateBuilder builder = new MessageCreateBuilder();
        if ((s1splatfestPretendo.root.Time.getStartTime() <= System.currentTimeMillis() / 1000) && (s1splatfestPretendo.root.Time.getEndTime() > System.currentTimeMillis() / 1000)) {
            builder.setEmbeds(generateSplatfestEmbedPretendo(s1splatfestPretendo, false, lang));
        } else {
            final EmbedBuilder b = new EmbedBuilder().setTitle(lang.botLocale.stagesTitle + " (Splatoon 1 " + Emote.PRETENDO_NETWORK + ")")
                    .addField(Emote.REGULAR +
                                    lang.game_modes.get("regular").name,
                            lang.botLocale.getS1MapName(currentRotation.RegularStages[0].MapID.value) +
                                    ", " + lang.botLocale.getS1MapName(currentRotation.RegularStages[1].MapID.value)
                            , true)
                    .addField(Emote.RANKED +
                                    lang.game_modes.get("gachi").name + " (" + GameModeUtil.translateS1(lang, currentRotation.GachiRule.value) + ")",
                            lang.botLocale.getS1MapName(currentRotation.GachiStages[0].MapID.value) +
                                    ", " + lang.botLocale.getS1MapName(currentRotation.GachiStages[1].MapID.value)
                            , true);

            if (currentRotation.image != null) b.setImage(currentRotation.image);
            builder.setEmbeds(b.build());
            if (iface.getCustomSplatfests(serverid)) {
                if (s1splatfestSplatfestival.root.FestivalId.value.equals("4100")) return builder.build();
                if ((s1splatfestSplatfestival.root.Time.getStartTime() <= System.currentTimeMillis() / 1000) && ((s1splatfestSplatfestival.root.Time.getEndTime() > System.currentTimeMillis() / 1000))) {
                    builder.addEmbeds(generateSplatfestEmbedSplatfestival(s1splatfestSplatfestival, false, lang));
                }
            }
        }
        return builder.build();
    }

    public static int convertIdToTeamNum(String id) {
        final String s = new String(Base64.getDecoder().decode(id), StandardCharsets.UTF_8).split(":")[2];
        return switch (s.toLowerCase()) {
            case "alpha" -> 0;
            case "bravo" -> 1;
            default -> 2;
        };
    }

    public static void sendS1RotationFeed(Long serverid, Long channel, Phase currentS1Rotation) {
        final GuildMessageChannel ch = (GuildMessageChannel) bot.jda.getGuildChannelById(channel);
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
                    getS1Message(
                            serverid,
                            currentS1Rotation), channel);
            if (msg != null) msg.thenAccept((a) -> iface.setLastS1RotationMessage(serverid, a.getIdLong()));

        } catch (InsufficientPermissionException e) {
            final Guild guildById = bot.jda.getGuildById(serverid);
            System.err.println("Failed to send s1 rotation to Server \"" + (guildById == null ? "null" : guildById.getName()) + "(" + serverid + ")\"");
        }
    }

    public static void sendS1PRotationFeed(Long serverid, Long channel, Phase currentS1Rotation) {
        final GuildMessageChannel ch = (GuildMessageChannel) bot.jda.getGuildChannelById(channel);
        if (ch == null) {
            System.out.println(serverid + " : Channel " + channel + " is null, removing...");
            iface.setS1PStageChannel(serverid, null);
            return;
        }
        try {
            final long lastRotationMessageID = iface.getLastS1PRotationMessage(serverid);
            final boolean deleteMessage = iface.getDeleteMessage(serverid);
            if (deleteMessage && lastRotationMessageID != 0) {
                final RestAction<Message> message = ch.retrieveMessageById(lastRotationMessageID);
                message.submit().thenAccept((msg) -> {
                    msg.delete().queue();
                });
            }
            final CompletableFuture<Message> msg = bot.sendMessage(
                    getS1PMessage(
                            serverid,
                            currentS1Rotation), channel);
            if (msg != null) msg.thenAccept((a) -> iface.setLastS1PRotationMessage(serverid, a.getIdLong()));

        } catch (InsufficientPermissionException e) {
            final Guild guildById = bot.jda.getGuildById(serverid);
            System.err.println("Failed to send s1 rotation to Server \"" + (guildById == null ? "null" : guildById.getName()) + "(" + serverid + ")\"");
        }
    }

    public static MessageEmbed generateSplatfestEmbed(FestRecord fest, boolean command, Locale l) {
        Locale dispLang = l;
        if (!l.s3locales.festivals.containsKey(fest.getSplatfestID())) {
            for (Locale lan : Main.translations.values()) {
                if (lan.s3locales.festivals.containsKey(fest.getSplatfestID())) {
                    dispLang = lan;
                    break;
                }
            }
            if (!dispLang.s3locales.festivals.containsKey(fest.getSplatfestID())) return null;
        }
        final EmbedBuilder b = new EmbedBuilder();
        final String splatfestID = fest.getSplatfestID();
        if (fest.getEndTime() < System.currentTimeMillis() / 1000) {
            b.setTitle(l.botLocale.splatfestEmbedTitle);
            b.setFooter(l.botLocale.footer_ended);
            b.setTimestamp(Instant.ofEpochSecond(fest.getEndTime()));
        } else if (fest.getStartTime() <= System.currentTimeMillis() / 1000) {
            b.setTitle(l.botLocale.runningSplatfestTitle);
            b.setFooter(l.botLocale.footer_ends);
            b.setTimestamp(Instant.ofEpochSecond(fest.getEndTime()));
        } else if (fest.getStartTime() > System.currentTimeMillis() / 1000) {
            b.setTitle(l.botLocale.newSplatfestTitle);
            b.setFooter(l.botLocale.footer_starts);
            b.setTimestamp(Instant.ofEpochSecond(fest.getStartTime()));
        }
        if (command) b.setTitle(l.botLocale.splatfestEmbedTitle);
        b.setImage(fest.image.url);
        b.setDescription(dispLang.s3locales.festivals.get(splatfestID).title);
        b.addField(l.botLocale.splatfestTeams, dispLang.s3locales.festivals.get(splatfestID).teams[0].teamName + ", " + dispLang.s3locales.festivals.get(splatfestID).teams[1].teamName + ", " + dispLang.s3locales.festivals.get(splatfestID).teams[2].teamName, false);
        if (fest.getWinningTeam() != null) {
            b.setColor(fest.getWinningTeam().color.toColor());
            b.addField(l.botLocale.splatfestTeamWinner, dispLang.s3locales.festivals.get(splatfestID).teams[convertIdToTeamNum(fest.getWinningTeam().id)].teamName, false);
        }

        return b.build();
    }

    public static MessageEmbed generateSplatfestEmbedPretendo(SplatfestByml fest, boolean command, Locale l) {
        final EmbedBuilder b = new EmbedBuilder();
        if (fest.root.Time.getEndTime() < System.currentTimeMillis() / 1000) {
            b.setTitle(Emote.SPLATFEST_SPL1 + " " + l.botLocale.splatfestEmbedTitle + " " + Emote.PRETENDO_NETWORK);
            b.setFooter(l.botLocale.footer_ended);
            b.setTimestamp(Instant.ofEpochSecond(fest.root.Time.getEndTime()));
        } else if (fest.root.Time.getStartTime() <= System.currentTimeMillis() / 1000) {
            b.setTitle(Emote.SPLATFEST_SPL1 + " " + l.botLocale.runningSplatfestTitle + " " + Emote.PRETENDO_NETWORK);
            b.setFooter(l.botLocale.footer_ends);
            b.setTimestamp(Instant.ofEpochSecond(fest.root.Time.getEndTime()));
        } else if (fest.root.Time.getStartTime() > System.currentTimeMillis() / 1000) {
            b.setTitle(Emote.SPLATFEST_SPL1 + " " + l.botLocale.newSplatfestTitle + " " + Emote.PRETENDO_NETWORK);
            b.setFooter(l.botLocale.footer_starts);
            b.setTimestamp(Instant.ofEpochSecond(fest.root.Time.getStartTime()));
        }

        if (command) b.setTitle(Emote.SPLATFEST_SPL1 + " " + l.botLocale.splatfestEmbedTitle);
        b.addField(l.botLocale.mode, GameModeUtil.translateS1(l, fest.root.Rule.value), true);
        b.addField(l.botLocale.splatfestTeams, fest.root.Teams[0].ShortName.get(l.botLocale.s1FestStr).value + ", " + fest.root.Teams[1].ShortName.get(l.botLocale.s1FestStr).value, true);
        b.addField(l.botLocale.stages, l.botLocale.getS1MapName(Integer.parseInt(fest.root.Stages[0].MapID.value)) + ", " + l.botLocale.getS1MapName(Integer.parseInt(fest.root.Stages[1].MapID.value)) + ", " + l.botLocale.getS1MapName(Integer.parseInt(fest.root.Stages[2].MapID.value)), false);

        if (fest.image != null) b.setImage(fest.image);
        return b.build();
    }

    public static MessageEmbed generateSplatfestEmbedSplatfestival(SplatfestByml fest, boolean command, Locale l) {
        final EmbedBuilder b = new EmbedBuilder();
        b.setDescription("Powered by [Splatfestival](https://discord.gg/grMSxZf)");
        if (fest.root.Time.getEndTime() < System.currentTimeMillis() / 1000) {
            b.setTitle(Emote.SPLATFEST_SPL1 + " " + l.botLocale.splatfestEmbedTitle);
            b.setFooter(l.botLocale.footer_ended);
            b.setTimestamp(Instant.ofEpochSecond(fest.root.Time.getEndTime()));
        } else if (fest.root.Time.getStartTime() <= System.currentTimeMillis() / 1000) {
            b.setTitle(Emote.SPLATFEST_SPL1 + " " + l.botLocale.runningSplatfestTitle);
            b.setFooter(l.botLocale.footer_ends);
            b.setTimestamp(Instant.ofEpochSecond(fest.root.Time.getEndTime()));
        } else if (fest.root.Time.getStartTime() > System.currentTimeMillis() / 1000) {
            b.setTitle(Emote.SPLATFEST_SPL1 + " " + l.botLocale.newSplatfestTitle);
            b.setFooter(l.botLocale.footer_starts);
            b.setTimestamp(Instant.ofEpochSecond(fest.root.Time.getStartTime()));
        }

        if (command) b.setTitle(Emote.SPLATFEST_SPL1 + " " + l.botLocale.splatfestEmbedTitle);
        b.addField(l.botLocale.mode, GameModeUtil.translateS1(l, fest.root.Rule.value), true);
        b.addField(l.botLocale.splatfestTeams, fest.root.Teams[0].ShortName.get(l.botLocale.s1FestStr).value + ", " + fest.root.Teams[1].ShortName.get(l.botLocale.s1FestStr).value, true);
        b.addField(l.botLocale.stages, l.botLocale.getS1MapName(Integer.parseInt(fest.root.Stages[0].MapID.value)) + ", " + l.botLocale.getS1MapName(Integer.parseInt(fest.root.Stages[1].MapID.value)) + ", " + l.botLocale.getS1MapName(Integer.parseInt(fest.root.Stages[2].MapID.value)), false);
        if (s1splatfestSplatfestival.root.FestivalId.value.equals("4100"))
            b.setFooter("This is a test splatfest, not an actual one");
        if (fest.image != null) b.setImage(fest.image);
        return b.build();
    }
}
