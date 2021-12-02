package de.erdbeerbaerlp.splatcord2.commands;

import de.erdbeerbaerlp.splatcord2.Main;
import de.erdbeerbaerlp.splatcord2.storage.Rotation;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon2.translations.Locale;
import de.erdbeerbaerlp.splatcord2.util.ScheduleUtil;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

import java.util.ArrayList;

public class RotationCommand extends BaseCommand {
    public RotationCommand(Locale l) {
        super("rotation", l.botLocale.cmdRotationDesc);
    }

    @Override
    public boolean requiresManageServer() {
        return false;
    }

    @Override
    public void execute(SlashCommandEvent ev) {

        final Locale lang = Main.translations.get(Main.iface.getServerLang(ev.getGuild().getIdLong()));

        final Rotation currentRotation = ScheduleUtil.getCurrentRotation();
        final ArrayList<Rotation> nextRotations = ScheduleUtil.getNext3Rotations();

        final EmbedBuilder future = new EmbedBuilder().setTitle(lang.botLocale.futureStagesTitle);
        for (int i = 0; i < nextRotations.size(); i++) {
            future.addField(":alarm_clock: ", "<t:" + nextRotations.get(i).getRegular().start_time + ":t>", true)
                    .addField("<:regular:822873973225947146>" +
                                    lang.game_modes.get("regular").name,
                            lang.stages.get(nextRotations.get(i).getRegular().stage_a.id).getName() +
                                    ", " + lang.stages.get(nextRotations.get(i).getRegular().stage_b.id).getName()
                            , true)
                    .addField("<:ranked:822873973200388106>" +
                                    lang.game_modes.get("gachi").name + " (" + lang.rules.get(nextRotations.get(i).getRanked().rule.key).name + ")",
                            lang.stages.get(nextRotations.get(i).getRanked().stage_a.id).getName() +
                                    ", " + lang.stages.get(nextRotations.get(i).getRanked().stage_b.id).getName()
                            , true)
                    .addField("<:league:822873973142192148>" +
                                    lang.game_modes.get("league").name + " (" + lang.rules.get(nextRotations.get(i).getLeague().rule.key).name + ")",
                            lang.stages.get(nextRotations.get(i).getLeague().stage_a.id).getName() +
                                    ", " + lang.stages.get(nextRotations.get(i).getLeague().stage_b.id).getName()
                            , true);
            if (i < nextRotations.size() - 1)
                future.addBlankField(false);
        }

        ev.replyEmbeds(new EmbedBuilder().setTitle(lang.botLocale.stagesTitle)
                .addField("<:regular:822873973225947146>" +
                                lang.game_modes.get("regular").name,
                        lang.stages.get(currentRotation.getRegular().stage_a.id).getName() +
                                ", " + lang.stages.get(currentRotation.getRegular().stage_b.id).getName()
                        , false)
                .addField("<:ranked:822873973200388106>" +
                                lang.game_modes.get("gachi").name + " (" + lang.rules.get(currentRotation.getRanked().rule.key).name + ")",
                        lang.stages.get(currentRotation.getRanked().stage_a.id).getName() +
                                ", " + lang.stages.get(currentRotation.getRanked().stage_b.id).getName()
                        , false)
                .addField("<:ranked:822873973142192148>" +
                                lang.game_modes.get("league").name + " (" + lang.rules.get(currentRotation.getLeague().rule.key).name + ")",
                        lang.stages.get(currentRotation.getLeague().stage_a.id).getName() +
                                ", " + lang.stages.get(currentRotation.getLeague().stage_b.id).getName()
                        , false)
                .build(), future.build()).queue();
    }
}
