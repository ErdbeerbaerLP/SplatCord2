package de.erdbeerbaerlp.splatcord2.commands;

import de.erdbeerbaerlp.splatcord2.Main;
import de.erdbeerbaerlp.splatcord2.storage.Emote;
import de.erdbeerbaerlp.splatcord2.storage.Rotation;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon1.Phase;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon2.translations.Locale;
import de.erdbeerbaerlp.splatcord2.util.ScheduleUtil;
import de.erdbeerbaerlp.splatcord2.util.wiiu.RankedModeTranslator;
import de.erdbeerbaerlp.splatcord2.util.wiiu.RotationTimingUtil;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

import java.time.Instant;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class RotationCommand extends BaseCommand {
    public RotationCommand(Locale l) {
        super("rotation", l.botLocale.cmdRotationDesc);
        final SubcommandData splat1 = new SubcommandData("splatoon1", l.botLocale.cmdRotationDesc);
        final SubcommandData splat2 = new SubcommandData("splatoon2", l.botLocale.cmdRotationDesc);
        //final SubcommandData splat3 = new SubcommandData("splatoon3",l.botLocale.cmdSetstageDesc);

        addSubcommands(splat2, splat1);
    }

    @Override
    public boolean requiresManageServer() {
        return false;
    }

    @Override
    public void execute(SlashCommandEvent ev) {

        final Locale lang = Main.translations.get(Main.iface.getServerLang(ev.getGuild().getIdLong()));

        if (ev.getSubcommandName() != null)
            switch (ev.getSubcommandName()) {

                case "splatoon1":
                    final Phase currentS1Rotation = Main.s1rotations.root.Phases[RotationTimingUtil.getRotationForInstant(Instant.now())];
                    final ArrayList<Phase> nextS1Rotations = new ArrayList<>();
                    nextS1Rotations.add(Main.s1rotations.root.Phases[RotationTimingUtil.getOffsetRotationForInstant(Instant.now(),1)]);
                    nextS1Rotations.add(Main.s1rotations.root.Phases[RotationTimingUtil.getOffsetRotationForInstant(Instant.now(),2)]);
                    nextS1Rotations.add(Main.s1rotations.root.Phases[RotationTimingUtil.getOffsetRotationForInstant(Instant.now(),3)]);
                    final EmbedBuilder future = new EmbedBuilder().setTitle(lang.botLocale.futureStagesTitle+ "(Splatoon 1)");
                    addS1Rotation(future, currentS1Rotation, lang, -1);
                    future.addBlankField(false);
                    long time = Instant.now().toEpochMilli();
                    for (int i = 0; i < nextS1Rotations.size(); i++) {
                        time = RotationTimingUtil.getNextRotationStart(time+1);
                        addS1Rotation(future, nextS1Rotations.get(i), lang, time+1);
                        if (i < nextS1Rotations.size() - 1)
                            future.addBlankField(false);
                    }
                    ev.replyEmbeds(future.build()).queue();

                    break;
                case "splatoon2":
                    final Rotation currentS2Rotation = ScheduleUtil.getCurrentRotation();
                    final ArrayList<Rotation> nextS2Rotations = ScheduleUtil.getNext3Rotations();

                    final EmbedBuilder s2EmbedBuilder = new EmbedBuilder().setTitle(lang.botLocale.futureStagesTitle+ "(Splatoon 2)");
                    addS2Rotation(s2EmbedBuilder, currentS2Rotation, lang, true);
                    s2EmbedBuilder.addBlankField(false);
                    for (int i = 0; i < nextS2Rotations.size(); i++) {
                        addS2Rotation(s2EmbedBuilder, nextS2Rotations.get(i), lang);
                        if (i < nextS2Rotations.size() - 1)
                            s2EmbedBuilder.addBlankField(false);
                    }
                    ev.replyEmbeds(s2EmbedBuilder.build()).queue();
                    break;
            }

    }


    private static void addS2Rotation(EmbedBuilder future, Rotation currentRotation, Locale lang) {
        addS2Rotation(future, currentRotation, lang, false);
    }

    private static void addS1Rotation(EmbedBuilder future, Phase currentRotation, Locale lang, long timestamp) {
        future.addField(":alarm_clock: ", timestamp == -1 ? ("`" + lang.botLocale.now + "`") : ("<t:" + TimeUnit.MILLISECONDS.toSeconds(timestamp) + ":t>"), true)
                .addField(Emote.REGULAR +
                                lang.game_modes.get("regular").name,
                        lang.botLocale.getS1MapName(currentRotation.RegularStages[0].MapID.value) +
                                ", " + lang.botLocale.getS1MapName(currentRotation.RegularStages[1].MapID.value)
                        , true)
                .addField(Emote.RANKED +
                                lang.game_modes.get("gachi").name + " (" + lang.rules.get(RankedModeTranslator.translateS1(currentRotation.GachiRule.value)).name + ")",
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
                                lang.game_modes.get("gachi").name + " (" + lang.rules.get(currentRotation.getRanked().rule.key).name + ")",
                        lang.stages.get(currentRotation.getRanked().stage_a.id).getName() +
                                ", " + lang.stages.get(currentRotation.getRanked().stage_b.id).getName()
                        , true)
                .addField(Emote.LEAGUE +
                                lang.game_modes.get("league").name + " (" + lang.rules.get(currentRotation.getLeague().rule.key).name + ")",
                        lang.stages.get(currentRotation.getLeague().stage_a.id).getName() +
                                ", " + lang.stages.get(currentRotation.getLeague().stage_b.id).getName()
                        , true);
    }
}
