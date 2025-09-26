package de.erdbeerbaerlp.splatcord2.commands;

import de.erdbeerbaerlp.splatcord2.Main;
import de.erdbeerbaerlp.splatcord2.storage.BotLanguage;
import de.erdbeerbaerlp.splatcord2.storage.Emote;
import de.erdbeerbaerlp.splatcord2.storage.Rotation;
import de.erdbeerbaerlp.splatcord2.storage.S3Rotation;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon1.Phase;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon2.translations.Locale;
import de.erdbeerbaerlp.splatcord2.util.GameModeUtil;
import de.erdbeerbaerlp.splatcord2.util.MessageUtil;
import de.erdbeerbaerlp.splatcord2.util.ScheduleUtil;
import de.erdbeerbaerlp.splatcord2.util.wiiu.RotationTimingUtil;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.time.Instant;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class RotationCommand extends BaseCommand {
    public RotationCommand(Locale l) {
        super("rotation", l.botLocale.cmdRotationDesc);
        final SubcommandData splat1 = new SubcommandData("splatoon1", l.botLocale.cmdRotationDesc);
        //final SubcommandData splat1pretendo = new SubcommandData("splatoon1pretendo", "(Pretendo Network) " + l.botLocale.cmdRotationDesc);
        final SubcommandData splat2 = new SubcommandData("splatoon2", l.botLocale.cmdRotationDesc);
        final SubcommandData splat3 = new SubcommandData("splatoon3", l.botLocale.cmdRotationDesc);

        addSubcommands(splat3, splat2, splat1/*, splat1pretendo*/);
        splat1.setDescriptionLocalizations(l.discordLocalizationFunc("cmdRotationDesc"));
        splat2.setDescriptionLocalizations(l.discordLocalizationFunc("cmdRotationDesc"));
        splat3.setDescriptionLocalizations(l.discordLocalizationFunc("cmdRotationDesc"));
        setDescriptionLocalizations(l.discordLocalizationFunc("cmdRotationDesc"));
    }

    @Override
    public boolean isServerOnly() {
        return false;
    }

    public static void addS2Rotation(EmbedBuilder future, Rotation currentRotation, Locale lang) {
        addS2Rotation(future, currentRotation, lang, false);
    }

    public static void addS3Rotation(EmbedBuilder future, S3Rotation currentRotation, Locale lang) {
        addS3Rotation(future, currentRotation, lang, false);
    }

    private static void addS1Rotation(EmbedBuilder future, Phase currentRotation, Locale lang, long timestamp) {
        future.addField(":alarm_clock: ", timestamp == -1 ? ("`" + lang.botLocale.now + "`") : ("<t:" + TimeUnit.MILLISECONDS.toSeconds(timestamp) + ":t>"), true)
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
    }

    private static void addS2Rotation(EmbedBuilder future, Rotation currentRotation, Locale lang, boolean now) {
        future.addField(":alarm_clock: ", now ? ("`" + lang.botLocale.now + "`") : ("<t:" + currentRotation.getRegular().start_time + ":t>"), true)
                .addField(Emote.REGULAR +
                                lang.game_modes.get("regular").name,
                        lang.stages.get(currentRotation.getRegular().stage_a.id).getName() +
                                ", " + lang.stages.get(currentRotation.getRegular().stage_b.id).getName()
                        , true)
                .addField(Emote.RANKED +
                                lang.game_modes.get("gachi").name + " (" + GameModeUtil.translateS2(lang, currentRotation.getRanked().rule.key) + ")",
                        lang.stages.get(currentRotation.getRanked().stage_a.id).getName() +
                                ", " + lang.stages.get(currentRotation.getRanked().stage_b.id).getName()
                        , true)
                .addField(Emote.LEAGUE +
                                lang.game_modes.get("league").name + " (" + GameModeUtil.translateS2(lang, currentRotation.getLeague().rule.key) + ")",
                        lang.stages.get(currentRotation.getLeague().stage_a.id).getName() +
                                ", " + lang.stages.get(currentRotation.getLeague().stage_b.id).getName()
                        , true);
    }

    private static void addS3Rotation(EmbedBuilder future, final S3Rotation currentRotation, Locale lang, boolean now) {
        if (currentRotation.getRegular() == null && currentRotation.getFest() == null) {
            future.addField("Error", "There was an error getting this rotation, please contact developer", true);
            future.addField("Description", "currentRotation.getRegular() == null", true);
        } else {
            future.addField(":alarm_clock: ", now ? ("`" + lang.botLocale.now + "`") : ("<t:" + (currentRotation.getFest() != null ? currentRotation.getFest().getStartTime() : currentRotation.getRegular().getStartTime()) + ":t>"), true);
            MessageUtil.addS3Embed(lang, currentRotation, future);
        }
    }

    @Override
    public boolean requiresManageServer() {
        return false;
    }

    @Override
    public void execute(SlashCommandInteractionEvent ev) {
        if(ev.getGuild() != null){
            BotLanguage serverLang = Main.iface.getServerLang(ev.getGuild().getIdLong());
            if(serverLang == null){
                serverLang = BotLanguage.fromDiscordLocale(ev.getGuild().getLocale());
            }
        }else{ 
            serverLang = BotLanguage.ENGLISH; //Fallback to english for now, command ran in DMs for example
        }
        final Locale lang = Main.translations.get(serverLang);
        if (ev.getSubcommandName() != null)
            switch (ev.getSubcommandName()) {
                case "splatoon1":
                    final Phase currentS1RotationP = Main.s1rotationsPretendo.root.Phases[RotationTimingUtil.getRotationForInstant(Instant.now(), Main.s1rotationsPretendo)];
                    final ArrayList<Phase> nextS1RotationsP = new ArrayList<>();
                    nextS1RotationsP.add(Main.s1rotationsPretendo.root.Phases[RotationTimingUtil.getOffsetRotationForInstant(Instant.now(), 1, Main.s1rotationsPretendo)]);
                    nextS1RotationsP.add(Main.s1rotationsPretendo.root.Phases[RotationTimingUtil.getOffsetRotationForInstant(Instant.now(), 2, Main.s1rotationsPretendo)]);
                    nextS1RotationsP.add(Main.s1rotationsPretendo.root.Phases[RotationTimingUtil.getOffsetRotationForInstant(Instant.now(), 3, Main.s1rotationsPretendo)]);
                    final EmbedBuilder futureP = new EmbedBuilder().setTitle(lang.botLocale.futureStagesTitle + " (Splatoon 1 " + Emote.PRETENDO_NETWORK + ")");
                    addS1Rotation(futureP, currentS1RotationP, lang, -1);
                    futureP.addBlankField(false);
                    long timeP = Instant.now().toEpochMilli();
                    for (int i = 0; i < nextS1RotationsP.size(); i++) {
                        timeP = RotationTimingUtil.getNextRotationStart(timeP + 1, Main.s1rotationsPretendo);
                        addS1Rotation(futureP, nextS1RotationsP.get(i), lang, timeP + 1);
                        if (i < nextS1RotationsP.size() - 1)
                            futureP.addBlankField(false);
                    }
                    ev.replyEmbeds(futureP.build()).queue();

                    break;
                case "splatoon2":
                    final Rotation currentS2Rotation = ScheduleUtil.getCurrentRotation();
                    final ArrayList<Rotation> nextS2Rotations = ScheduleUtil.getNext3Rotations();

                    final EmbedBuilder s2EmbedBuilder = new EmbedBuilder().setTitle(lang.botLocale.futureStagesTitle + " (Splatoon 2)");
                    addS2Rotation(s2EmbedBuilder, currentS2Rotation, lang, true);
                    s2EmbedBuilder.addBlankField(false);
                    for (int i = 0; i < nextS2Rotations.size(); i++) {
                        addS2Rotation(s2EmbedBuilder, nextS2Rotations.get(i), lang);
                        if (i < nextS2Rotations.size() - 1)
                            s2EmbedBuilder.addBlankField(false);
                    }
                    ev.replyEmbeds(s2EmbedBuilder.build()).setActionRow(Button.primary("loadmore2", lang.botLocale.cmdRotationLoadAll)).queue();
                    break;
                case "splatoon3":
                    final S3Rotation currentS3Rotation = ScheduleUtil.getCurrentS3Rotation();
                    System.out.println(currentS3Rotation);
                    final ArrayList<S3Rotation> nextS3Rotations = ScheduleUtil.getS3Next3Rotations();

                    final EmbedBuilder s3EmbedBuilder = new EmbedBuilder().setTitle(lang.botLocale.futureStagesTitle + " (Splatoon 3)");
                    addS3Rotation(s3EmbedBuilder, currentS3Rotation, lang, true);
                    s3EmbedBuilder.addBlankField(false);
                    for (int i = 0; i < nextS3Rotations.size(); i++) {
                        addS3Rotation(s3EmbedBuilder, nextS3Rotations.get(i), lang);
                        if (i < nextS3Rotations.size() - 1)
                            s3EmbedBuilder.addBlankField(false);
                    }
                    ev.replyEmbeds(s3EmbedBuilder.build()).setActionRow(Button.primary("loadmore3", lang.botLocale.cmdRotationLoadAll)).queue();
                    break;
            }

    }
}
