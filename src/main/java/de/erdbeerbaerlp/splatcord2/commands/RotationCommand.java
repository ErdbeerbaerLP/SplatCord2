package de.erdbeerbaerlp.splatcord2.commands;

import de.erdbeerbaerlp.splatcord2.Main;
import de.erdbeerbaerlp.splatcord2.storage.BotLanguage;
import de.erdbeerbaerlp.splatcord2.storage.Emote;
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
        addRotation(future, currentRotation, lang, true);
        future.addBlankField(false);
        for (int i = 0; i < nextRotations.size(); i++) {
            addRotation(future, nextRotations.get(i), lang);
            if (i < nextRotations.size() - 1)
                future.addBlankField(false);
        }
        ev.replyEmbeds(future.build()).queue();
    }

    private static void addRotation(EmbedBuilder future, Rotation currentRotation, Locale lang) {
        addRotation(future, currentRotation, lang,false);
    }

    private static void addRotation(EmbedBuilder future, Rotation currentRotation, Locale lang, boolean now) {
        future.addField(":alarm_clock: ", now?("`"+lang.botLocale.now+"`"):("<t:" + currentRotation.getRegular().start_time + ":t>"), true)
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
