package de.erdbeerbaerlp.splatcord2.commands;

import de.erdbeerbaerlp.splatcord2.Main;
import de.erdbeerbaerlp.splatcord2.storage.Emote;
import de.erdbeerbaerlp.splatcord2.storage.S3Rotation;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon2.coop_schedules.Weapons;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon2.translations.Locale;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon3.rotation.Coop3;
import de.erdbeerbaerlp.splatcord2.util.ScheduleUtil;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.time.Instant;

public class SalmonCommand extends BaseCommand {
    public SalmonCommand(Locale l) {

        super("salmon", l.botLocale.cmdSalmonDesc);

        final SubcommandData splat2 = new SubcommandData("splatoon2", l.botLocale.cmdSalmonDesc);
        final SubcommandData splat3 = new SubcommandData("splatoon3", l.botLocale.cmdSalmonDesc);
        addSubcommands(splat2, splat3);
    }

    private static String getWeaponName(Locale lang, Weapons w) {
        if (w.weapon == null && w.coop_special_weapon != null) {
            return lang.coop_special_weapons.get(w.coop_special_weapon.image).name;
        } else {
            return lang.weapons.get(w.id).name;
        }
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
                            .setImage(Main.coop_schedules.details[0].outImageURL)
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
                            .setImage(Main.coop_schedules.details[1].outImageURL)
                            .setFooter(lang.botLocale.footer_starts)
                            .setTimestamp(Instant.ofEpochSecond(Main.coop_schedules.details[1].start_time))
                            .build()).queue();
                    break;
                case "splatoon3":
                    final S3Rotation currentS3Rotation = ScheduleUtil.getCurrentS3Rotation();
                    final Coop3 nextRotation = ScheduleUtil.getNextS3Coop();
                    final String prediction = switch (currentS3Rotation.getCoop().__splatoon3ink_king_salmonid_guess) {
                        case "Cohozuna" -> String.valueOf(Emote.COHOZUNA);
                        case "Horrorboros" -> String.valueOf(Emote.HORRORBOROS);
                        case "Megalodontia" -> String.valueOf(Emote.MEGALODONTIA);
                        case "Triumvirate" -> String.valueOf(Emote.TRIUMVIRATE);
                        default -> String.valueOf(Emote.ERROR_CONTACT_DEVELOPER);
                    };
                    final String prediction2 = switch (nextRotation.__splatoon3ink_king_salmonid_guess) {
                        case "Cohozuna" -> String.valueOf(Emote.COHOZUNA);
                        case "Horrorboros" -> String.valueOf(Emote.HORRORBOROS);
                        case "Megalodontia" -> String.valueOf(Emote.MEGALODONTIA);
                        case "Triumvirate" -> String.valueOf(Emote.TRIUMVIRATE);
                        default -> String.valueOf(Emote.ERROR_CONTACT_DEVELOPER);
                    };
                    ev.replyEmbeds(new EmbedBuilder().setTitle(lang.botLocale.salmonRunTitle + " (Splatoon 3)")
                            .addField(lang.botLocale.salmonStage, lang.s3locales.stages.get(currentS3Rotation.getCoop().setting.coopStage.id).name, true)
                            .addField(lang.botLocale.weapons,
                                    lang.s3locales.weapons.get(currentS3Rotation.getCoop().setting.weapons[0].__splatoon3ink_id).name + ", " +
                                            lang.s3locales.weapons.get(currentS3Rotation.getCoop().setting.weapons[1].__splatoon3ink_id).name + ", " +
                                            lang.s3locales.weapons.get(currentS3Rotation.getCoop().setting.weapons[2].__splatoon3ink_id).name + ", " +
                                            lang.s3locales.weapons.get(currentS3Rotation.getCoop().setting.weapons[3].__splatoon3ink_id).name
                                    , true)
                            .setImage(currentS3Rotation.getCoop().outImageURL)
                            .setDescription(lang.botLocale.salmonPrediction + prediction)
                            .setFooter(lang.botLocale.footer_ends)
                            .setTimestamp(Instant.ofEpochSecond(currentS3Rotation.getCoop().getEndTime()))
                            .build(), new EmbedBuilder().setTitle(lang.botLocale.salmonRunTitle + " (Splatoon 3)")
                            .addField(lang.botLocale.salmonStage, lang.s3locales.stages.get(nextRotation.setting.coopStage.id).name, true)
                            .addField(lang.botLocale.weapons,
                                    lang.s3locales.weapons.get(nextRotation.setting.weapons[0].__splatoon3ink_id).name + ", " +
                                            lang.s3locales.weapons.get(nextRotation.setting.weapons[1].__splatoon3ink_id).name + ", " +
                                            lang.s3locales.weapons.get(nextRotation.setting.weapons[2].__splatoon3ink_id).name + ", " +
                                            lang.s3locales.weapons.get(nextRotation.setting.weapons[3].__splatoon3ink_id).name
                                    , true)
                            .setImage(nextRotation.outImageURL)
                            .setDescription(lang.botLocale.salmonPrediction + prediction2)
                            .setFooter(lang.botLocale.footer_starts)
                            .setTimestamp(Instant.ofEpochSecond(nextRotation.getStartTime()))
                            .build()).setActionRow(Button.danger("delete", Emoji.fromUnicode("U+1F5D1"))).queue();
                    break;
            }

    }
}
