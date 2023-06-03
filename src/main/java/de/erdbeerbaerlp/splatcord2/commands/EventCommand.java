package de.erdbeerbaerlp.splatcord2.commands;

import de.erdbeerbaerlp.splatcord2.Main;
import de.erdbeerbaerlp.splatcord2.storage.Emote;
import de.erdbeerbaerlp.splatcord2.storage.S3Rotation;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon2.translations.Locale;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon3.rotation.EventSchedule;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon3.rotation.EventTimePeriod;
import de.erdbeerbaerlp.splatcord2.util.GameModeUtil;
import de.erdbeerbaerlp.splatcord2.util.ScheduleUtil;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

public class EventCommand extends BaseCommand {
    public EventCommand(Locale l) {
        super("challenges", l.botLocale.cmdEventDesc);
    }

    @Override
    public boolean requiresManageServer() {
        return false;
    }

    @Override
    public void execute(SlashCommandInteractionEvent ev) {

        final Locale lang = Main.translations.get(Main.iface.getServerLang(ev.getGuild().getIdLong()));
        final S3Rotation currentS3Rotation = ScheduleUtil.getCurrentS3Rotation();
        final EventSchedule nextRotation = ScheduleUtil.getNextS3Event();

        final EmbedBuilder curEmb = new EmbedBuilder().setTitle(Emote.EVENT+ lang.s3locales.events.get(currentS3Rotation.getEvent().leagueMatchSetting.leagueMatchEvent.id).name)
                .setDescription("**"+lang.s3locales.events.get(currentS3Rotation.getEvent().leagueMatchSetting.leagueMatchEvent.id).desc+"**\n"+lang.s3locales.events.get(currentS3Rotation.getEvent().leagueMatchSetting.leagueMatchEvent.id).regulation.replace("<br />", "\n"))
                .addField(lang.botLocale.mode, GameModeUtil.translateS3(lang,currentS3Rotation.getEvent().leagueMatchSetting.vsRule.id),true ).addField(lang.botLocale.stages, lang.s3locales.stages.get(currentS3Rotation.getEvent().leagueMatchSetting.vsStages[0].id).name +
                        ", " + lang.s3locales.stages.get(currentS3Rotation.getEvent().leagueMatchSetting.vsStages[1].id).name, true);
        final StringBuilder b = new StringBuilder();
        for (EventTimePeriod tp : currentS3Rotation.getEvent().timePeriods) {
            b.append("<t:"+tp.getStartTime()+":f> (<t:"+tp.getStartTime()+":R>) - <t:"+tp.getEndTime()+":f> (<t:"+tp.getEndTime()+":R>)\n");
        }
        curEmb.addField(lang.botLocale.eventTimeTitle, b.toString(), false);
        final EmbedBuilder futureEmb = new EmbedBuilder().setTitle(Emote.EVENT+ lang.s3locales.events.get(nextRotation.leagueMatchSetting.leagueMatchEvent.id).name)
                .setDescription("**"+lang.s3locales.events.get(nextRotation.leagueMatchSetting.leagueMatchEvent.id).desc+"**\n"+lang.s3locales.events.get(nextRotation.leagueMatchSetting.leagueMatchEvent.id).regulation.replace("<br />", "\n"))
                .addField(lang.botLocale.mode, GameModeUtil.translateS3(lang,nextRotation.leagueMatchSetting.vsRule.id),true ).addField(lang.botLocale.stages, lang.s3locales.stages.get(nextRotation.leagueMatchSetting.vsStages[0].id).name +
                        ", " + lang.s3locales.stages.get(nextRotation.leagueMatchSetting.vsStages[1].id).name, true);
        final StringBuilder b2 = new StringBuilder();
        for (EventTimePeriod tp : nextRotation.timePeriods) {
            b2.append("<t:"+tp.getStartTime()+":f> (<t:"+tp.getStartTime()+":R>) - <t:"+tp.getEndTime()+":f> (<t:"+tp.getEndTime()+":R>)\n");
        }
        futureEmb.addField(lang.botLocale.eventTimeTitle, b2.toString(), false);
        ev.replyEmbeds(curEmb.build(),futureEmb.build()).setActionRow(Button.danger("delete", Emoji.fromUnicode("U+1F5D1"))).queue();

    }
}
