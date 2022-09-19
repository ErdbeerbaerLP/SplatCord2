package de.erdbeerbaerlp.splatcord2.commands;

import de.erdbeerbaerlp.splatcord2.Main;
import de.erdbeerbaerlp.splatcord2.storage.S3Rotation;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon2.coop_schedules.Weapons;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon2.translations.Locale;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon3.Coop3;
import de.erdbeerbaerlp.splatcord2.util.ScheduleUtil;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

import java.time.Instant;

public class SalmonCommand extends BaseCommand {
    public SalmonCommand(Locale l) {

        super("salmon", l.botLocale.cmdSalmonDesc);

        final SubcommandData splat2 = new SubcommandData("splatoon2", l.botLocale.cmdSetsalmonDesc);
        final SubcommandData splat3 = new SubcommandData("splatoon3",l.botLocale.cmdSetsalmonDesc);
        addSubcommands(splat2,splat3);
    }

    @Override
    public boolean requiresManageServer() {
        return false;
    }

    @Override
    public void execute(SlashCommandInteractionEvent ev) {

        final Locale lang = Main.translations.get(Main.iface.getServerLang(ev.getGuild().getIdLong()));
        if (ev.getSubcommandName() != null)
            switch (ev.getSubcommandName()) {
                case "splatoon2":
                    ev.replyEmbeds(new EmbedBuilder().setTitle(lang.botLocale.salmonRunTitle + " (Splatoon 2)")
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
                            .build(), new EmbedBuilder().setTitle(lang.botLocale.salmonRunTitle + " (Splatoon 2)")
                            .addField(lang.botLocale.salmonStage, lang.coop_stages.get(Main.coop_schedules.details[1].stage.image).getName(), true)
                            .addField(lang.botLocale.weapons,
                                    getWeaponName(lang, Main.coop_schedules.details[1].weapons[0]) + ", " +
                                            getWeaponName(lang, Main.coop_schedules.details[1].weapons[1]) + ", " +
                                            getWeaponName(lang, Main.coop_schedules.details[1].weapons[2]) + ", " +
                                            getWeaponName(lang, Main.coop_schedules.details[1].weapons[3])
                                    , true)
                            .setImage("https://splatoon2.ink/assets/splatnet/" + Main.coop_schedules.details[1].stage.image)
                            .setFooter(lang.botLocale.footer_starts)
                            .setTimestamp(Instant.ofEpochSecond(Main.coop_schedules.details[1].start_time))
                            .build()).queue();
                    break;
                case "splatoon3":
                    final S3Rotation currentS3Rotation = ScheduleUtil.getCurrentS3Rotation();
                    final Coop3 nextRotation = ScheduleUtil.getNextS3Coop();
                    ev.replyEmbeds(new EmbedBuilder().setTitle(lang.botLocale.salmonRunTitle + " (Splatoon 3)")
                            .addField(lang.botLocale.salmonStage, lang.botLocale.getS3SalmonMap(currentS3Rotation.getCoop().setting.coopStage.coopStageId), true)
                            .addField(lang.botLocale.weapons,
                                    currentS3Rotation.getCoop().setting.weapons[0].name + ", " +
                                            currentS3Rotation.getCoop().setting.weapons[1].name + ", " +
                                            currentS3Rotation.getCoop().setting.weapons[2].name + ", " +
                                            currentS3Rotation.getCoop().setting.weapons[3].name
                                    , true)
                            .setImage(currentS3Rotation.getCoop().setting.coopStage.image.url)
                            .setFooter(lang.botLocale.footer_ends)
                            .setTimestamp(Instant.ofEpochSecond(currentS3Rotation.getCoop().getEndTime()))
                            .setDescription(lang.botLocale.noTranslations)
                            .build(), new EmbedBuilder().setTitle(lang.botLocale.salmonRunTitle + " (Splatoon 3)")
                            .addField(lang.botLocale.salmonStage, lang.botLocale.getS3SalmonMap(nextRotation.setting.coopStage.coopStageId), true)
                            .addField(lang.botLocale.weapons,
                                    nextRotation.setting.weapons[0].name + ", " +
                                            nextRotation.setting.weapons[1].name + ", " +
                                            nextRotation.setting.weapons[2].name + ", " +
                                            nextRotation.setting.weapons[3].name
                                    , true)
                            .setImage(nextRotation.setting.coopStage.image.url)
                            .setFooter(lang.botLocale.footer_starts)
                            .setTimestamp(Instant.ofEpochSecond(nextRotation.getEndTime()))
                            .setDescription(lang.botLocale.noTranslations)
                            .build()).queue();
                    break;
            }

    }
    private static String getWeaponName(Locale lang, Weapons w) {
        if (w.weapon == null && w.coop_special_weapon != null) {
            return lang.coop_special_weapons.get(w.coop_special_weapon.image).name;
        } else {
            return lang.weapons.get(w.id).name;
        }
    }
}
