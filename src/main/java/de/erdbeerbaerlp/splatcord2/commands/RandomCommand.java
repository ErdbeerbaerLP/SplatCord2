package de.erdbeerbaerlp.splatcord2.commands;

import de.erdbeerbaerlp.splatcord2.Main;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon2.translations.Locale;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon2.translations.Stage;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon2.translations.Weapon;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class RandomCommand extends BaseCommand {

    public RandomCommand(Locale l) {
        super("random", l.botLocale.cmdRandomDesc);
        final SubcommandData weapon = new SubcommandData("weapon", l.botLocale.cmdRandomWeaponDesc);
        weapon.addOption(OptionType.INTEGER, "amount", l.botLocale.cmdRandomAmountDesc);
        final SubcommandData stage = new SubcommandData("stage", l.botLocale.cmdRandomStageDesc);
        stage.addOption(OptionType.INTEGER, "amount", l.botLocale.cmdRandomAmountDesc);
        final SubcommandData team = new SubcommandData("team", l.botLocale.cmdRandomTeamDesc);
        team.addOption(OptionType.INTEGER, "players", l.botLocale.cmdRandomTeamAmountDesc);
        team.addOption(OptionType.BOOLEAN, "weapons", l.botLocale.cmdRandomTeamWeapons);
        addSubcommands(weapon, stage, team);
    }

    static void shuffleArray(int[] ar) {
        final Random rnd = new Random();
        for (int i = ar.length - 1; i > 0; i--) {
            int index = rnd.nextInt(i + 1);
            // Simple swap
            int a = ar[index];
            ar[index] = ar[i];
            ar[i] = a;
        }
    }

    @Override
    public boolean requiresManageServer() {
        return false;
    }

    @Override
    public void execute(SlashCommandEvent ev) {
        System.out.println(ev.getName());
        System.out.println(ev.getChannel());
        final Random r = new Random();
        final Locale lang = Main.translations.get(Main.iface.getServerLang(ev.getGuild().getIdLong()));
        final String subcmd = ev.getSubcommandName();
        if (subcmd == null) return;
        int amount = 1;
        final OptionMapping amountOption = ev.getOption("amount");
        if (amountOption != null) try {
            amount = Math.min(Integer.parseInt(amountOption.getAsString()), 10);
        } catch (NumberFormatException ignored) {
        }
        final Weapon[] weapons = lang.weapons.values().toArray(new Weapon[0]);
        System.out.println(subcmd);
        switch (subcmd) {
            case "weapon":
                final StringBuilder weaponString = new StringBuilder();
                for (int i = 0; i < amount; ++i) {
                    final int weapon = r.nextInt(lang.weapons.size() - 1);
                    weaponString.append(weapons[weapon].name).append("\n");
                }
                ev.reply(weaponString.toString().trim()).queue();
                break;
            case "stage":
                final StringBuilder stageString = new StringBuilder();
                final Stage[] stages = lang.stages.values().toArray(new Stage[0]);
                for (int i = 0; i < amount; ++i) {
                    final int stage = r.nextInt(lang.stages.size() - 1);
                    stageString.append(stages[stage].getName()).append("\n");
                }
                ev.reply(stageString.toString().trim()).queue();
                break;
            case "team":

                boolean genWeapons = false;
                final OptionMapping weaponOption = ev.getOption("weapons");
                if (weaponOption != null) genWeapons = weaponOption.getAsBoolean();

                int players = 10;
                final OptionMapping playersOption = ev.getOption("players");
                if (playersOption != null) try {
                    players = Math.max(3, Math.min(Integer.parseInt(playersOption.getAsString()), 10));
                } catch (NumberFormatException ignored) {
                }
                final int[] playerArray = new int[players];
                for (int i = 0; i < players; i++) {
                    playerArray[i] = i+1;
                }
                shuffleArray(playerArray);
                System.out.println(Arrays.toString(playerArray));
                final StringBuilder teamString = new StringBuilder();
                teamString.append(lang.botLocale.cmdRandomTeamAlpha+":\n");
                teamString.append("[" + playerArray[0]+"] ");
                if(genWeapons) teamString.append(weapons[r.nextInt(lang.weapons.size() - 1)].name);
                teamString.append("\n");
                if (players >= 4) {
                    teamString.append("[" + playerArray[2]+"] ");
                    if(genWeapons) teamString.append(weapons[r.nextInt(lang.weapons.size() - 1)].name);
                    teamString.append("\n");
                }
                if (players >= 6) {
                    teamString.append("[" + playerArray[4]+"] ");
                    if(genWeapons) teamString.append(weapons[r.nextInt(lang.weapons.size() - 1)].name);
                    teamString.append("\n");
                }
                if (players >= 8) {
                    teamString.append("[" + playerArray[6]+"] ");
                    if(genWeapons) teamString.append(weapons[r.nextInt(lang.weapons.size() - 1)].name);
                    teamString.append("\n");
                }
                teamString.append(lang.botLocale.cmdRandomTeamBravo+":\n");
                teamString.append("[" + playerArray[1]+"] ");
                if(genWeapons) teamString.append(weapons[r.nextInt(lang.weapons.size() - 1)].name);
                teamString.append("\n");
                if (players >= 4) {
                    teamString.append("[" + playerArray[3]+"] ");
                    if(genWeapons) teamString.append(weapons[r.nextInt(lang.weapons.size() - 1)].name);
                    teamString.append("\n");
                }
                if (players >= 6) {
                    teamString.append("[" + playerArray[5]+"] ");
                    if(genWeapons) teamString.append(weapons[r.nextInt(lang.weapons.size() - 1)].name);
                    teamString.append("\n");
                }
                if (players >= 8) {
                    teamString.append("[" + playerArray[7]+"] ");
                    if(genWeapons) teamString.append(weapons[r.nextInt(lang.weapons.size() - 1)].name);
                    teamString.append("\n");
                }
                teamString.append(lang.botLocale.cmdRandomTeamSpec+":\n");
                if(players == 3) teamString.append("[" + playerArray[2] + "]\n");
                if(players == 5) teamString.append("[" + playerArray[4] + "]\n");
                if(players == 7) teamString.append("[" + playerArray[6] + "]\n");
                if (players >= 9) teamString.append("[" + playerArray[8] + "]\n");
                if (players == 10) teamString.append("[" + playerArray[9] + "]\n");
                System.out.println(teamString.toString());

                ev.reply(teamString.toString().trim()).queue();
                break;
        }
    }
}
