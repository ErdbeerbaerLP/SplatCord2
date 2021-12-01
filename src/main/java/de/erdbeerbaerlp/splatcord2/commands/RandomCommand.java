package de.erdbeerbaerlp.splatcord2.commands;

import de.erdbeerbaerlp.splatcord2.Main;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon2.translations.Locale;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon2.translations.Stage;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon2.translations.Weapon;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

import java.util.Random;

public class RandomCommand extends BaseCommand {
    public RandomCommand(Locale l) {
        super("random", l.botLocale.cmdRandomDesc);
        final SubcommandData weapon = new SubcommandData("weapon", l.botLocale.cmdRandomWeaponDesc);
        weapon.addOption(OptionType.INTEGER, "amount", l.botLocale.cmdRandomAmountDesc);
        final SubcommandData stage = new SubcommandData("stage", l.botLocale.cmdRandomStageDesc);
        stage.addOption(OptionType.INTEGER, "amount", l.botLocale.cmdRandomAmountDesc);
        addSubcommands(weapon, stage);
    }

    @Override
    public boolean requiresManageServer() {
        return false;
    }

    @Override
    public void execute(SlashCommandEvent ev) {
        final Random r = new Random();
        final Locale lang = Main.translations.get(Main.iface.getServerLang(ev.getGuild().getIdLong()));
        boolean hide = false;
        final String subcmd = ev.getSubcommandName();
        if(subcmd == null) return;
        int amount = 1;
        final OptionMapping amountOption = ev.getOption("amount");
        if (amountOption != null) try {
            amount = Math.min(Integer.parseInt(amountOption.getAsString()), 10);
        } catch (NumberFormatException ignored) {
        }
        if (subcmd.equals("weapon")) {
            final StringBuilder weaponString = new StringBuilder();
            final Weapon[] weapons = lang.weapons.values().toArray(new Weapon[0]);
            for (int i = 0; i < amount; ++i) {
                final int weapon = r.nextInt(lang.weapons.size() - 1);
                weaponString.append(weapons[weapon].name).append("\n");
            }
            ev.reply(weaponString.toString().trim()).queue();
        } else if(subcmd.equals("stage")){
            final StringBuilder stageString = new StringBuilder();
            final Stage[] stages = lang.stages.values().toArray(new Stage[0]);
            for (int i = 0; i < amount; ++i) {
                final int stage = r.nextInt(lang.stages.size() - 1);
                stageString.append(stages[stage].getName()).append("\n");
            }
            ev.reply(stageString.toString().trim()).queue();
        }
    }
}
