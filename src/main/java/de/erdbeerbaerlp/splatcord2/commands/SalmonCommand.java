package de.erdbeerbaerlp.splatcord2.commands;

import de.erdbeerbaerlp.splatcord2.Main;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon2.coop_schedules.Weapons;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon2.translations.Locale;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.time.Instant;

public class SalmonCommand extends BaseCommand {
    public SalmonCommand(Locale l) {
        super("salmon", l.botLocale.cmdSalmonDesc);
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


                case "splatoon1":

                    break;

                case "splatoon2":
                    ev.replyEmbeds(new EmbedBuilder().setTitle(lang.botLocale.salmonRunTitle)
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
                            .build(), new EmbedBuilder().setTitle(lang.botLocale.salmonRunTitle)
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
